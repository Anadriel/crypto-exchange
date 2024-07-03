
# Cryptocurrency Exchange Microservices

## Overview

This project is a demo application designed to explore and demonstrate the integration of Spring Boot, RabbitMQ, and PostgreSQL working together in a microservices architecture. The platform consists of two microservices: OrderBook and Balance. These services communicate via RabbitMQ to handle order placements and balance updates for users.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup and Running the Services](#setup-and-running-the-services)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Usage](#usage)

## Overview

The platform allows users to place orders to sell one cryptocurrency and buy another. The system matches these orders and updates user balances accordingly. The OrderBook service handles order placements and matches, while the Balance service manages user balances.

## Architecture

- **OrderBook Service**: Manages the order book and matches incoming orders with existing ones.
- **Balance Service**: Manages user balances and updates them based on matched orders.
- **RabbitMQ**: Used for communication between OrderBook and Balance services.
- **PostgreSQL**: Used for persistent storage of orders and balances.

## Prerequisites

Ensure you have the following software installed:

- Java 11 or later
- Maven
- RabbitMQ
- PostgreSQL

## Setup and Running the Services

### Step 1: Set Up PostgreSQL Databases

Create the databases for the OrderBook and Balance services.

```sh
sudo -i -u postgres
psql
CREATE DATABASE orderbookdb;
CREATE DATABASE balancedb;
\q
exit
```

### Step 2: Clone the Repository

```sh
git clone git@github.com:Anadriel/crypto-exchange.git
cd crypto-exchange
```

### Step 3: Build and Run the Services

Build the project using Maven:

```sh
mvn clean install
```

Run the OrderBook service:

```sh
cd orderbook-service
mvn spring-boot:run
```

Run the Balance service:

```sh
cd balance-service
mvn spring-boot:run
```

## Running Tests

You can run tests for each service using Maven. Ensure RabbitMQ and PostgreSQL are running before executing the tests.

For OrderBook service tests:

```sh
cd orderbook-service
mvn test
```

For Balance service tests:

```sh
cd balance-service
mvn test
```

## Configuration

Configuration for RabbitMQ and PostgreSQL is done through `application.properties` files in each service. Here are the key configurations:

### OrderBook Service (`orderbook-service/src/main/resources/application.properties`)

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.queue=match_queue

spring.datasource.url=jdbc:postgresql://localhost:5432/orderbookdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

server.port=8081
```

### Balance Service (`balance-service/src/main/resources/application.properties`)

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.queue=match_queue

spring.datasource.url=jdbc:postgresql://localhost:5433/balancedb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

server.port=8082
```

## Usage

### Placing an Order

To place an order, send a POST request to the OrderBook service:

```sh
POST /orders
{
  "userId": 1,
  "orderType": "SELL",
  "sellCurrency": "BTC",
  "buyCurrency": "ETH",
  "amount": 0.5,
  "price": 30000.0
}
```

### Balance Update

The Balance service automatically updates user balances based on matched orders.
Deposit and withdraw functions should be added in the next steps.

