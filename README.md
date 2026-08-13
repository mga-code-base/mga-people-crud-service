# 👥 mga-people-crud-service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4%2F4.0-6DB33F?logo=springboot)](https://spring.io/)
[![Java Version](https://img.shields.io/badge/Java-25-007396?logo=openjdk)](https://openjdk.org/)
[![Build Tool](https://img.shields.io/badge/Build-Gradle%209.4.1-066DA5?logo=gradle)](https://gradle.org/)
[![GraalVM](https://img.shields.io/badge/GraalVM-Native%20Image-orange?logo=graalvm)](https://www.graalvm.org/)
[![Database](https://img.shields.io/badge/Database-MongoDB-47A248?logo=mongodb)](https://www.mongodb.com/)
[![Network](https://img.shields.io/badge/Security-Cluster--Internal-red)](https://kubernetes.io/docs/concepts/services-networking/network-policies/)

## 📌 Service Overview

`mga-people-crud-service` is a cluster-internal MongoDB data-access microservice responsible for persistence, query execution, and sequence generation across identity domain entities (Admin, Member, and Trainer).

It operates strictly beneath `mga-people-service` in a CQRS/Data-Layer Separation pattern, firewalled behind Kubernetes NetworkPolicy.

### 🏛️ Architectural Features
* Firewalled CQRS Isolation: Completely firewalled from public ingress; invoked strictly via internal RestClient calls from mga-people-service and pre-verification calls from mga-facial-recognition-service.
* Atomic Sequential ID Generation: Uses MongoTemplate (findAndModify) to generate thread-safe, auto-incrementing business IDs (admin_0, member_1, etc.).
* GraalVM Native Image Packaging: Built Ahead-Of-Time (AOT) with Java 25 & Gradle 9.4.1 for sub-second startup and low memory footprint in K8s.

---

## 🛠️ Technical Specifications
* Port: 8080 (Cluster-Internal)
* Language/Build: Java 25 | Gradle 9.4.1
* Persistence: MongoDB Atlas / Compass (People DB)
* Shared Contract: mga-all-models-jars

---

## 📡 Core API Controller Endpoints

### Admin Controller (`/crud/admins`)
* `POST /crud/admins` - Save/register new admin profile
* `POST /crud/admins/primary` - Save primary admin profile (`admin_0`)
* `GET /crud/admins/{id}` - Fetch admin details by ID
* `GET /crud/admins/by-email/{email}` - Fetch admin profile by email address
* `GET /crud/admins/all` - List all registered admins
* `GET /crud/admins/inactive` - List inactive/unapproved admin profiles
* `PUT /crud/admins/{id}` - Update admin details by ID
* `DELETE /crud/admins/{id}` - Soft-delete or remove admin account by ID

### Member Controller (`/crud/members`)
* `POST /crud/members` - Save/register new member profile
* `GET /crud/members/{id}` - Fetch member details by ID
* `GET /crud/members/by-email/{email}` - Fetch member profile by email address
* `GET /crud/members/all` - List all registered members
* `PUT /crud/members/{id}` - Update member profile details
* `PUT /crud/members/{id}/trainer` - Update assigned trainer for member
* `PUT /crud/members/{id}/facial-status` - Update facial biometric registration status flag
* `DELETE /crud/members/{id}` - Delete member profile by ID

### Trainer Controller (`/crud/trainers`)
* `POST /crud/trainers` - Save/register new trainer profile
* `GET /crud/trainers/{id}` - Fetch trainer profile by ID
* `GET /crud/trainers/by-email/{email}` - Fetch trainer details by email address
* `GET /crud/trainers/all` - List all active trainers
* `GET /crud/trainers/inactive` - List pending/inactive trainer accounts
* `PUT /crud/trainers/{id}` - Update trainer profile details
* `PUT /crud/trainers/approve/{trainerId}` - Approve and activate trainer account
* `DELETE /crud/trainers/{id}` - Remove trainer record by ID

---

## ⚙️ Build & Deployment Instructions
# Standard JVM Run
./gradlew bootRun

# GraalVM Native Image Compilation
./gradlew nativeCompile

# Paketo OCI Native Container Image
./gradlew bootBuildImage
