# IndiaFin API

A Spring Boot REST API for validating and looking up Indian financial identifiers: IFSC, GSTIN, PAN and pincode.

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

2. Download the IFSC dataset. The data file is not stored in this repo. Download `IFSC.csv` from the [latest release of razorpay/ifsc](https://github.com/razorpay/ifsc/releases) and place it at:
```
src/main/resources/data/IFSC.csv
```

3. Start the app
```bash
./mvnw spring-boot:run
```
The API starts on `http://localhost:8080`. On startup it loads about 183,000 branches into memory, so lookups are instant.

## Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/ping` | Health check | Done |
| GET | `/ifsc/{code}` | Look up a bank branch by IFSC code | Done |
| GET | `/gstin/{number}` | Validate a GSTIN | Planned |
| GET | `/pan/{number}` | Validate a PAN | Planned |
| GET | `/pincode/{code}` | Pincode lookup | Planned |

### Example: `GET /ifsc/SBIN0000001`
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

Codes are case-insensitive: `/ifsc/sbin0000001` returns the same result.

### Errors

All errors use the same JSON shape.

`400 Bad Request` when the IFSC format is invalid (`GET /ifsc/hello`):
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid IFSC Code Format",
  "timestamp": "2026-10-09T05:10:41.512344Z"
}
```

`404 Not Found` when the format is valid but the code is not in the dataset (`GET /ifsc/ABCD0123456`):
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "IFSC code not found",
  "timestamp": "2026-10-09T05:11:02.103871Z"
}
```

## IFSC format
An IFSC is 11 characters: 4 letters (bank code), the digit `0`, then 6 letters or digits (branch code). Example: `SBIN0001234`.

## Data source
IFSC data is derived from RBI publications, compiled in the open-source [razorpay/ifsc](https://github.com/razorpay/ifsc) dataset. Some small co-operative banks have no bank name in the source data, so `bank` can be `null`. Always verify critical payment details with the bank.

## Roadmap
- [x] Project setup and health check
- [x] IFSC format validation and consistent error responses
- [x] Real IFSC data lookup
- [ ] PAN, GSTIN and pincode endpoints
- [ ] Unit and integration tests
- [ ] API key auth and rate limiting
- [ ] OpenAPI / Swagger docs
- [ ] Docker and deployment
- [ ] Listing on an API marketplace