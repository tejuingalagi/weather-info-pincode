# Weather Info by Pincode

## Overview

This project provides a REST API to fetch weather information for a specific pincode and date.

The system integrates with OpenWeather APIs and stores results in a PostgreSQL database.
To minimize external API usage, responses are cached using a **(pincode + date)** combination.

No UI is included. The API can be tested using Postman.

---

## API Endpoint

**GET /api/weather**

### Query Parameters

| Parameter | Required | Description                                          |
| --------- | -------- | ---------------------------------------------------- |
| pincode   | Yes      | 6 digit Indian pincode                               |
| forDate   | No       | Date in YYYY-MM-DD format (defaults to current date) |

### Example Request

```
http://localhost:8080/api/weather?pincode=560001&forDate=2026-02-17
```

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

Stores mapping of pincode to coordinates

| Column    | Description |
| --------- | ----------- |
| pincode   | Primary Key |
| latitude  | Latitude    |
| longitude | Longitude   |
| city      | City name   |

### Table: weather_info

Stores weather for a pincode and date

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

## Caching Logic

1. Convert pincode → latitude/longitude using Geocoding API
2. Store coordinates in database
3. Fetch weather using lat/long
4. Store weather using (pincode + date)
5. Repeated request returns cached data without external API call

---

## Validations

* Missing date → defaults to current date
* Invalid pincode → error response
* Future date → not supported
* External API failure → handled gracefully

---

## Tech Stack

* Java 8
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* Lombok

---

## External APIs

Geocoding:
https://api.openweathermap.org/geo/1.0/zip

Weather:
https://api.openweathermap.org/data/2.5/weather

---

## Configuration

Set environment variable:

```
WEATHER_API_KEY=your_api_key
```

`application.properties`

```
weather.api.key=${WEATHER_API_KEY}
```

---

## Run Application

Create database:

```
weather_db
```

Run:

```
mvn spring-boot:run
```

---

## Testing

Test using Postman:

```
GET http://localhost:8080/api/weather?pincode=560001
```

Repeated requests return cached results.

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

