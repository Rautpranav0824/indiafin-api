# IndiaFin API

A Spring Boot REST API for validating and looking up Indian financial and postal identifiers: IFSC, GSTIN, PAN and pincode.

> Work in progress. Built step by step as a learning project.

## Tech stack
- Java 17+
- Spring Boot 4.1.1
- Maven
- Apache Commons CSV

## Run locally

1. Clone the repo
```bash
git clone https://github.com/Rautpranav0824/indiafin-api.git
cd indiafin-api
```

2. Download the datasets. The data files are not stored in this repo.

   **IFSC:** download `IFSC.csv` from the [latest release of razorpay/ifsc](https://github.com/razorpay/ifsc/releases) and place it at:
```
src/main/resources/data/IFSC.csv
```

**Pincode:** download the "All India Pincode Directory" CSV from [data.gov.in](https://www.data.gov.in/catalog/all-india-pincode-directory), rename it to `pincodes.csv` and place it at:
```
src/main/resources/data/pincodes.csv
```
The pincode file must have these lowercase column names: `circlename, regionname, divisionname, officename, pincode, officetype, delivery, district, statename, latitude, longitude`.

3. Start the app
```bash
./mvnw spring-boot:run
```
The API starts on `http://localhost:8080`. On startup it loads about 183,000 bank branches and about 165,000 post offices into memory, so lookups are instant.

## Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/ping` | Health check | Done |
| GET | `/ifsc/{code}` | Look up a bank branch by IFSC code | Done |
| GET | `/pan/{number}` | Validate a PAN and return the holder type | Done |
| GET | `/gstin/{number}` | Validate a GSTIN (format and checksum), return state code and PAN | Done |
| GET | `/pincode/{code}` | List all post offices under a pincode | Done |

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
  "timestamp": "2026-10-13T05:10:41.512344Z"
}
```

`400 Bad Request` for a GSTIN with the right format but a wrong checksum (`GET /gstin/27ABCPE1234F1Z5`), with the message `Invalid GSTIN checksum`.

`404 Not Found` when an IFSC or pincode has a valid format but is not in the dataset (`GET /ifsc/ABCD0123456` or `GET /pincode/999999`):
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "IFSC code not found",
  "timestamp": "2026-10-13T05:11:02.103871Z"
}
```

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
- [ ] Unit and integration tests
- [ ] API key auth and rate limiting
- [ ] OpenAPI / Swagger docs
- [ ] Docker and deployment
- [ ] Listing on an API marketplace

## Changelog
- **2026-10-13:** Added the pincode endpoint (about 19,600 pincodes grouped from about 165,000 post offices). Fixed a startup crash caused by coordinates written in degrees/minutes/seconds, which now load as `null`.
- **2026-10-09:** Added PAN validation and GSTIN validation with checksum.
- **2026-10-08:** Project setup, IFSC lookup with real data, consistent error format.