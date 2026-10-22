# IndiaFin API

A Spring Boot REST API for validating and looking up Indian financial and postal identifiers: IFSC, GSTIN, PAN and pincode.

> Work in progress. Built step by step as a learning project.

## Tech stack
- Java 21
- Spring Boot 4.1.1
- Maven
- Apache Commons CSV (dataset loading)
- Bucket4j (rate limiting)
- springdoc-openapi (API documentation, in progress)
- JUnit 5 and MockMvc (tests)

## Run locally

### 1. Clone the repo

```bash
git clone https://github.com/Rautpranav0824/indiafin-api.git
cd indiafin-api
```

### 2. Download the datasets

The data files are not stored in this repo.

**IFSC:** download `IFSC.csv` from the [latest release of razorpay/ifsc](https://github.com/razorpay/ifsc/releases) and place it at:

```
src/main/resources/data/IFSC.csv
```

**Pincode:** download the "All India Pincode Directory" CSV from [data.gov.in](https://www.data.gov.in/catalog/all-india-pincode-directory), rename it to `pincodes.csv` and place it at:

```
src/main/resources/data/pincodes.csv
```

The pincode file must have these lowercase column names: `circlename, regionname, divisionname, officename, pincode, officetype, delivery, district, statename, latitude, longitude`.

### 3. Set your API key

The app **refuses to start without one**, and there is no default key. Generate a random one:

```bash
openssl rand -hex 32
```

Then provide it in either of these ways.

**Environment variable:**

```bash
export INDIAFIN_API_KEY=<your key>
```

**Local config file** (ignored by git, so the key never gets committed). Create `config/application.properties` in the project folder:

```properties
indiafin.api-key=<your key>
```

### 4. Start the app

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. On startup it loads about 183,000 bank branches and about 165,000 post offices into memory, so lookups are instant.

## Authentication

Every endpoint except `/ping` and the documentation pages requires your key in the `X-API-Key` header:

```bash
curl -H "X-API-Key: <your key>" http://localhost:8080/ifsc/SBIN0000001
```

A missing or wrong key returns `401 Unauthorized`.

## Rate limiting

Requests are limited per client with a token bucket (Bucket4j). By default a client can send a burst of **10** requests, then **5 per second** after that. When the limit is exceeded the API returns `429 Too Many Requests` with a `Retry-After` header (in seconds).

- The client is identified by the `X-RapidAPI-User` header when present, otherwise by its IP address.
- `/ping` and the documentation pages are not rate limited.
- The limits can be changed with `indiafin.rate-limit.capacity` and `indiafin.rate-limit.refill-per-second`.
- Limits are kept in memory, so they apply per running server instance.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/ping` | Health check (no key needed) |
| GET | `/ifsc/{code}` | Look up a bank branch by IFSC code |
| GET | `/pan/{number}` | Validate a PAN and return the holder type |
| GET | `/gstin/{number}` | Validate a GSTIN (format and checksum), return state code and PAN |
| GET | `/pincode/{code}` | List all post offices under a pincode |

Identifiers containing letters are case-insensitive.

### IFSC: `GET /ifsc/SBIN0000001`

```json
{
  "ifsc": "SBIN0000001",
  "bank": "State Bank of India",
  "branch": "KOLKATA MAIN",
  "address": "SAMRIDDHI BHAWAN, 1 STRAND ROAD, KOLKATA 700 001",
  "city": "KOLKATA",
  "district": "KOLKATA",
  "state": "WEST BENGAL",
  "micr": "700002021",
  "contact": null,
  "neft": true,
  "rtgs": true,
  "imps": true,
  "upi": true
}
```

An IFSC is 11 characters: 4 letters (bank code), the digit `0`, then 6 letters or digits (branch code).

### PAN: `GET /pan/ABCPE1234F`

```json
{
  "pan": "ABCPE1234F",
  "valid": true,
  "entityType": "Individual"
}
```

A PAN is 10 characters: 5 letters, 4 digits, 1 letter. The 4th character is the holder type (`P` Individual, `C` Company, `H` Hindu Undivided Family, `F` Firm, `A` Association of Persons, `T` Trust, `B` Body of Individuals, `L` Local Authority, `J` Artificial Juridical Person, `G` Government).

### GSTIN: `GET /gstin/27AAPFU0939F1ZV`

```json
{
  "gstin": "27AAPFU0939F1ZV",
  "valid": true,
  "stateCode": "27",
  "pan": "AAPFU0939F"
}
```

A GSTIN is 15 characters: a 2-digit state code, the holder's 10-character PAN, an entity number, the letter `Z`, and a check character. The check character is verified with the official checksum algorithm, so mistyped numbers are rejected even when the format looks right.

> PAN and GSTIN validation confirms that a number is correctly **formed** (and, for GSTIN, that its checksum matches). It does **not** confirm that the number is registered to a real person or business.

### Pincode: `GET /pincode/504273`

```json
{
  "pincode": "504273",
  "count": 14,
  "postOffices": [
    {
      "officeName": "Kothimir B.O",
      "pincode": "504273",
      "officeType": "BO",
      "delivery": true,
      "district": "KUMURAM BHEEM ASIFABAD",
      "state": "TELANGANA",
      "latitude": 19.3638689,
      "longitude": 79.5376658
    },
    {
      "officeName": "Rechini S.O",
      "pincode": "504273",
      "officeType": "PO",
      "delivery": true,
      "district": "MANCHERIAL",
      "state": "TELANGANA",
      "latitude": 19.180413,
      "longitude": 79.500405
    }
  ]
}
```

The example is shortened: the real response lists all 14 post offices.

A pincode is 6 digits and never starts with 0. One pincode can cover **many post offices**, and sometimes **more than one district**, so the response is a list. `delivery` is `false` for offices that do not deliver mail.

> **About coordinates:** latitude and longitude come from the source dataset and should be treated as **approximate**. About 7% of post offices have no coordinates (`null`), and some values look wrong: for example, hundreds of offices share an identical placeholder-looking longitude, and a few entries are not valid decimal numbers (these are returned as `null`). Do not use these coordinates for precise navigation or distance calculations.

### Errors

All errors use the same JSON shape.

`400 Bad Request` for an invalid format (`GET /pan/hello`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid PAN format",
  "timestamp": "2026-10-22T05:10:41.512344Z"
}
```

`400 Bad Request` for a GSTIN with the right format but a wrong checksum (`GET /gstin/27AAPFU0939F1ZX`), with the message `Invalid GSTIN checksum`.

`401 Unauthorized` for a missing or invalid API key:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Missing or invalid API key",
  "timestamp": "2026-10-22T05:10:41.512344Z"
}
```

`404 Not Found` when an IFSC or pincode has a valid format but is not in the dataset (`GET /ifsc/ABCD0123456` or `GET /pincode/999999`):

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "IFSC code not found",
  "timestamp": "2026-10-22T05:11:02.103871Z"
}
```

`429 Too Many Requests` when the rate limit is exceeded, with a `Retry-After` header:

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded",
  "timestamp": "2026-10-22T05:12:15.884438Z"
}
```

## Tests

```bash
./mvnw test
```

The test suite covers every endpoint (success, bad input, not found), the GSTIN checksum, the API key filter and the rate limiter, including the `429` response and per-client baskets.

The tests start the application with their own test API key and generous rate limits, so they do not need your real key. They do load the real datasets, so the two CSV files must be in place first.

## Data sources

- **IFSC:** derived from RBI publications, compiled in the open-source [razorpay/ifsc](https://github.com/razorpay/ifsc) dataset. Some small co-operative banks have no bank name in the source data, so `bank` can be `null`. Always verify critical payment details with the bank.
- **Pincode:** "All India Pincode Directory", published by the Government of India through the [Open Government Data Platform](https://www.data.gov.in/catalog/all-india-pincode-directory) under the Government Open Data License (GODL-India). The dataset is not updated in real time, so some post offices may be outdated.

## Roadmap

- [x] Project setup and health check
- [x] IFSC format validation and consistent error responses
- [x] Real IFSC data lookup
- [x] PAN validation with holder type
- [x] GSTIN validation with checksum
- [x] Pincode lookup
- [x] API key authentication
- [x] Rate limiting (token bucket, per client)
- [x] Tests for all endpoints, the API key filter and the rate limiter
- [ ] OpenAPI / Swagger docs (in progress: docs page is up, key authorization and endpoint descriptions to do)
- [ ] Docker and deployment
- [ ] Listing on an API marketplace

## Changelog

- **2026-10-22:** Added tests for pincode, the API key filter and the rate limit filter. Cleaned up startup code (CSV files are now always closed). Started the OpenAPI documentation.
- **2026-10-20:** Removed the default API key (the app now refuses to start without one), made rate limits configurable, and added test settings.
- **2026-10-19:** Added rate limiting with Bucket4j (`429` with `Retry-After`).
- **2026-10-15:** Added API key authentication (`X-API-Key` header) and endpoint tests.
- **2026-10-13:** Added the pincode endpoint (about 19,600 pincodes grouped from about 165,000 post offices). Fixed a startup crash caused by coordinates written in degrees/minutes/seconds, which now load as `null`.
- **2026-10-09:** Added PAN validation and GSTIN validation with checksum.
- **2026-10-08:** Project setup, IFSC lookup with real data, consistent error format.