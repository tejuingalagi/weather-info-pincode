# Weather Info by Pincode (Backend Assignment)

## Overview

This project provides a single REST API to fetch weather information for a **specific pincode and particular date**.

The system integrates with the OpenWeather API and stores results in a PostgreSQL database.
To avoid repeated external API calls, the application implements a **two-level caching strategy** based on `(pincode + date)`.

The API is testable using Postman or Swagger and does not include any UI as per requirements.

---

## Key Objective

Design a backend service that:

* Retrieves weather data for a given pincode
* Stores location and weather data in DB
* Reuses stored data to optimize API calls
* Minimizes external API usage

---

## Functional Coverage

### 1. Single REST API

**POST /api/weather**

Request:

```json
{
  "pincode": "560001",
  "forDate": "2020-10-15"
}
```

`forDate` is optional → defaults to current date.

Response:

```json
{
  "pincode": "560001",
  "city": "Bengaluru",
  "temperature": 26.49,
  "humidity": 37,
  "weatherDescription": "few clouds",
  "source": "CACHE"
}
```

`source` values:

* API → fetched from OpenWeather
* CACHE → returned from database

---

## Database Design

### Table: pincode_location

| Column    | Description |
| --------- | ----------- |
| pincode   | Primary Key |
| latitude  | Latitude    |
| longitude | Longitude   |
| city      | City name   |

### Table: weather_info

| Column              | Description  |
| ------------------- | ------------ |
| id                  | Primary Key  |
| pincode             | Pincode      |
| for_date            | Weather date |
| temperature         | Temperature  |
| humidity            | Humidity     |
| weather_description | Description  |

**Unique Constraint:** `(pincode, for_date)`

---

## API Optimization Strategy (Most Important)

The application avoids unnecessary external API calls using **two levels of caching**.

### Level 1 — Location Cache

* Convert Pincode → Latitude/Longitude using Geocoding API
* Store in database
* Future requests reuse stored coordinates

### Level 2 — Weather Cache

* Weather stored using `(pincode + date)`
* If same request repeats → return DB data
* External weather API is NOT called again

This significantly improves performance and reduces API usage.

---

## Request Flow

Client
↓
Controller
↓
Service
↓
Check Weather in DB
→ Found → Return Response

→ Not Found
  Check Location in DB
  → Not Found → Call Geo API → Save Location
  Call Weather API → Save Weather
  Return Response

---

## Validations & Error Handling

* Missing date → defaults to current date
* Invalid pincode → handled via global exception handler
* External API failure → proper error response returned

---

## Assumptions

* OpenWeather free API provides current weather data
* For past dates, latest available weather is stored
* First successful response for `(pincode + date)` is cached

---

## Tech Stack

* Java 8
* Spring Boot 2.7
* Spring Data JPA
* PostgreSQL
* Maven
* Lombok
* OpenWeather API

---

## External APIs Used

Pincode → Lat/Long
https://api.openweathermap.org/geo/1.0/zip

Lat/Long → Weather
https://api.openweathermap.org/data/2.5/weather

---

## Environment Configuration

API key is not stored in source code.

application.properties:

```
weather.api.key=${WEATHER_API_KEY}
```

Set environment variable (Git Bash):

```
export WEATHER_API_KEY=your_api_key
```

---

## How to Run

1. Create database:

```
weather_db
```

2. Configure DB credentials

3. Set API key

4. Run:

```
mvn spring-boot:run
```

---

## Testing

Use Postman or Swagger:

```
POST http://localhost:8080/api/weather
```

Repeated requests return cached results.

---

## Project Structure

* controller → REST API layer
* service → business logic
* repository → database access
* entity → database tables
* dto → request/response models
* exception → global error handling

---

## Testability

The API is designed to be testable independently using Postman/Swagger.
Service layer logic is structured to support unit testing for caching behavior and validation.

---

## Possible Enhancements

* Add unit tests (TDD)
* Add Swagger documentation
* Add Redis caching
* Support historical weather APIs

---

## Author

Tejeshwini Ingalagi
