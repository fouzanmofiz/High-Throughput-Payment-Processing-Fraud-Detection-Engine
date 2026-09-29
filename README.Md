# High-Throughput Payment Processing & Fraud Detection Engine

A backend payment processing system built with Java and Spring Boot that processes payment requests asynchronously, evaluates transactions for fraud using rule-based and AI-assisted analysis, and moves approved transactions through an idempotent settlement workflow.

The system is designed around asynchronous event processing using Apache Kafka, fast-access caching using Redis, persistent storage using MySQL, and secure REST APIs using JWT authentication.

---

## 🚀 Project Overview

Modern payment systems need to process a large number of transactions while maintaining:

- Reliable transaction processing
- Fraud detection
- Duplicate request protection
- Asynchronous processing
- Secure APIs
- Fast access to frequently used data
- Failure recovery
- Reliable settlement

This project implements these concepts in a Spring Boot based payment processing architecture.

### High-Level Flow

Payment Request

↓

Request Validation

↓

Payment Persistence

↓

Kafka Event Publishing

↓

Asynchronous Fraud Processing

↓

Rule-Based Fraud Detection

+

AI-Assisted Risk Analysis

↓

Combined Risk Decision

↓

APPROVED / REVIEW / BLOCKED

↓

Settlement

↓

Final Payment Status

---

# 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │  REST / Swagger UI  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Spring Boot API   │
                         │                     │
                         │ JWT Authentication  │
                         │ Request Validation │
                         │ Idempotency         │
                         └──────────┬──────────┘
                                    │
                                    ▼
                              ┌───────────┐
                              │   MySQL   │
                              │ Payments  │
                              └───────────┘
                                   
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Apache Kafka      │
                         │ Payment Events      │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Payment Consumer    │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┴────────────────┐
                    │                                │
                    ▼                                ▼
          ┌──────────────────┐             ┌──────────────────┐
          │ Rule-Based Fraud │             │ Spring AI /      │
          │ Detection        │             │ Mistral Analysis │
          └────────┬─────────┘             └────────┬─────────┘
                   │                                │
                   └──────────────┬─────────────────┘
                                  ▼
                       ┌─────────────────────┐
                       │ Combined Risk Score │
                       │                     │
                       │ APPROVED            │
                       │ REVIEW              │
                       │ BLOCKED             │
                       └──────────┬──────────┘
                                  │
                                  ▼
                       ┌─────────────────────┐
                       │    Settlement      │
                       │    Service         │
                       │    Idempotency     │
                       └──────────┬──────────┘
                                  │
                                  ▼
                             ┌──────────┐
                             │  MySQL   │
                             └──────────┘

                    ┌──────────────────────┐
                    │        Redis         │
                    │                      │
                    │ Fraud Result Cache   │
                    │ JWT Blacklist        │
                    └──────────────────────┘

## 🧠 Why These Technologies?

### Java + Spring Boot
Used to build the core REST APIs and backend business logic.

### Apache Kafka
Used to decouple payment ingestion from asynchronous fraud processing.

### MySQL
Used for persistent transactional data such as payments and settlements.

### Redis
Used for low-latency cached data and JWT blacklist information.

### Spring AI + Mistral
Used to provide an additional AI-based risk signal alongside deterministic fraud rules.

### Docker
Used to containerize the application and supporting infrastructure.

### JWT
Used to secure protected APIs.