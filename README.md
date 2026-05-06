# Cinema Tickets Service

A Java service for purchasing cinema tickets with business rule validation, payment processing, and seat reservation.

## Overview

This service implements a ticket purchasing system that handles three types of tickets:
- **Infant** (£0) - No seat allocated, sits on an adult's lap
- **Child** (£15) - Seat allocated
- **Adult** (£25) - Seat allocated

## Business Rules

- Maximum 25 tickets per purchase
- Child and Infant tickets cannot be purchased without an Adult ticket
- Infants are free and do not require a seat
- Only Adults and Children are allocated seats
- All accounts with ID > 0 are valid and have sufficient funds


## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Dependencies

- **JUnit Jupiter** 5.12.0 - Testing framework
- **Mockito** 5.14.2 - Mocking framework for unit tests

## Building the Project

## Clean and compile
mvn clean compile

## Run tests
mvn test

## Package the application
mvn package


