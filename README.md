```
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

## 📡 Key REST Endpoints (/crud/)
* Admins: POST /crud/admins, POST /crud/admins/primary, GET /crud/admins/{id}, PUT /crud/admins/{id}, DELETE /crud/admins/{id}
* Members: POST /crud/members, GET /crud/members/{id}, PUT /crud/members/{id}/trainer, PUT /crud/members/{id}/facial-status
* Trainers: POST /crud/trainers, GET /crud/trainers/all, PUT /crud/trainers/approve/{trainerid}

---

## ⚙️ Build & Deployment Instructions
# Standard JVM Run
./gradlew bootRun

# GraalVM Native Image Compilation
./gradlew nativeCompile

# Paketo OCI Native Container Image
./gradlew bootBuildImage

```
