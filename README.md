# IndiaFin API

A Spring Boot REST API for validating and looking up Indian financial identifiers: IFSC, GSTIN, PAN and pincode.

> Work in progress. Built step by step as a learning project.

## Tech stack
- Java 17
- Spring Boot 4.1.1
- Maven

## Run locally
```bash
git clone https://github.com/Rautpranav0824/indiafin-api.git
cd indiafin-api
./mvnw spring-boot:run
```
The API starts on `http://localhost:8080`.

## Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/ping` | Health check | Done |
| GET | `/ifsc/{code}` | Validate an IFSC code (format only for now) | In progress |
| GET | `/gstin/{number}` | Validate a GSTIN | Planned |
| GET | `/pan/{number}` | Validate a PAN | Planned |
| GET | `/pincode/{code}` | Pincode lookup | Planned |

### Example: `GET /ifsc/SBIN0001234`
```json
{ "ifsc": "SBIN0001234", "bank": "Demo Bank", "branch": "Demo Branch" }
```
Bank and branch are placeholders until the real dataset is loaded.

### Example error: `GET /ifsc/hello`
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid IFSC Code Format",
  "timestamp": "2026-10-08T15:48:01.821858Z"
}
```

## Roadmap
- [x] Project setup and health check
- [x] IFSC format validation and consistent error responses
- [ ] Real IFSC data lookup
- [ ] GSTIN, PAN and pincode endpoints
- [ ] API key auth and rate limiting
- [ ] OpenAPI / Swagger docs
- [ ] Docker and deployment