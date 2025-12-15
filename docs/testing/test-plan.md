# Testing Plan

## Unit Tests
- JUnit 5 for backend services
- Jest for React frontend
- Coverage target: 80%

## Integration Tests
- Spring Boot Test
- Testcontainers for database
- MockMvc for API testing

## Load Testing
- Apache JMeter
- Gatling for performance testing
- Target: 1000 concurrent users

## API Testing
- Postman collections
- Newman for CI/CD

## Run Tests

```bash
# Backend unit tests
cd backend
mvn test

# Backend integration tests
mvn verify

# Frontend tests
cd frontend
npm test

# Load tests
cd docs/testing
jmeter -n -t load-test.jmx -l results.jtl
```
