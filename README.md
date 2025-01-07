# Rewards Calculation Service

## Overview
The Rewards Calculation Service is a RESTful API designed to calculate customer rewards based on their transaction history. This service processes customer transactions and calculates the reward points for a given date range.

## Features
- Calculate reward points for transactions within a specified date range.
- Return detailed monthly reward breakdown.
- Display transaction details for the specified period.

## Technologies Used
- **Java**: Backend logic.
- **Spring Boot**: Framework for building the REST API.
- **Maven**: Dependency management.
- **JUnit & Mockito**: Testing framework.
- **SLF4J**: Logging.
- **MockMvc**: Testing Spring MVC.

## API Endpoint
### GET `/rewards/calculate`

#### Description
Calculates the rewards for a customer based on their transactions within a specified date range.

#### Request Parameters
| Parameter   | Type   | Description                          |
|-------------|--------|--------------------------------------|
| customerId  | Long   | The ID of the customer.              |
| startDate   | String | The start date of the range (YYYY-MM-DD). |
| endDate     | String | The end date of the range (YYYY-MM-DD).   |

#### Sample Request
```
GET http://localhost:8080/rewards/calculate?customerId=1&startDate=2025-01-10&endDate=2025-02-05
```

#### Sample Response
```json
{
    "customerId": 1,
    "customerName": "Gauri Sharma",
    "monthlyRewards": {
        "JANUARY": 120,
        "FEBRUARY": 150
    },
    "transactions": [
        {
            "id": 1,
            "amount": 120.0,
            "date": "2025-01-15"
        },
        {
            "id": 2,
            "amount": 80.0,
            "date": "2025-01-20"
        },
        {
            "id": 3,
            "amount": 150.0,
            "date": "2025-02-05"
        }
    ]
}
```



## Installation and Setup
1. Clone the repository:
   ```bash
   git clone git clone https://github.com/gauris864/reward-calculator.git
   ```
2. Navigate to the project directory:
   ```bash
   cd reward-calculator
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Running Tests
To execute the tests, run:
```bash
mvn test
```

## Logging
The service uses SLF4J for logging. Logs are generated for each API request, including the total execution time for reward calculations.


