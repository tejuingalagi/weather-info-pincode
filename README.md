# Weather Info by Pincode (Backend Assignment)

## Overview

This project provides a single REST API to fetch weather information for a **specific pincode and particular date**.

The application integrates with the OpenWeather APIs and stores results in a PostgreSQL database.
To reduce repeated external API calls, responses are cached using a **(pincode + date)** combination.

No UI is included. The API can be tested using Postman.

---

## API Endpoint

**POST /api/weather**

### Request

```json
{
  "pincode": "560001",
  "forDate": "2020-10-15"
}
```

`forDate` is optional
If not provided → current date is used

### Response

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

**source values**

* API → fetched from OpenWeather
* CACHE → returned from database

---

## Database Design

### Table: pincode_location

Stores mapping of pincode to coordinates.

| Column    | Description |
| --------- | ----------- |
| pincode   | Primary Key |
| latitude  | Latitude    |
| longitude | Longitude   |
| city      | City name   |

### Table: weather_info

Stores weather for a pincode and date.

| Column              | Description  |
| ------------------- | ------------ |
| id                  | Primary Key  |
| pincode             | Pincode      |
| for_date            | Weather date |
| temperature         | Temperature  |
| humidity            | Humidity     |
| weather_description | Description  |

**Unique constraint:** `(pincode, for_date)`

---

## API Optimization Logic

The system minimizes external API usage using two-step caching.

### Step 1 — Location Cache

* Convert pincode → latitude/longitude using Geocoding API
* Save in database
* Future requests reuse stored coordinates

### Step 2 — Weather Cache

* Weather stored using (pincode + date)
* If same request repeats → return DB data
* Weather API is not called again

---

## Request Flow

1. Client sends request to API
2. Controller forwards request to Service layer

3. Service checks weather data in database
   - If found → return cached response

4. If weather not found:
   - Check location (pincode → lat/long) in DB
       - If not found → call Geocoding API → save location

5. Call Weather API using coordinates
6. Save weather data in DB
7. Return response to client


---

## Validations

* Missing date → defaults to current date
* Invalid pincode → handled via exception handler
* External API failure → error response returned

---

## Tech Stack

* Java 8
* Spring Boot 2.7
* Spring Data JPA
* PostgreSQL
* Maven
* Lombok

---

## External APIs

**Geocoding:**
https://api.openweathermap.org/geo/1.0/zip

**Weather:**
https://api.openweathermap.org/data/2.5/weather

---

## Configuration

API key is not stored in source code.

`application.properties`

```
weather.api.key=${WEATHER_API_KEY}
```

Set environment variable:

```
export WEATHER_API_KEY=your_api_key
```

---

## How to Run

Create database:

```
weather_db
```

Configure DB credentials in `application.properties`

Set API key environment variable

Run:

```
mvn spring-boot:run
```

---

## Testing

Test using Postman:

```
POST http://localhost:8080/api/weather
```

Repeated requests for the same pincode & date will return cached results.

---

## Project Structure

* controller – REST endpoint
* service – business logic
* repository – database access
* entity – tables
* dto – request/response models
* exception – error handling

---

## Author

Tejeshwini Ingalagi
