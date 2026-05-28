# Portfolio Risk System

## 📑 Table of Contents

* [Project Overview](#project-overview)
* [Problem Statement](#problem-statement)
* [Objective](#objective)
* [Architecture Overview](#architecture-overview)
* [AWS Services Used](#aws-services-used)
* [Project Workflow](#project-workflow)
* [Docker Implementation](#docker-implementation)
* [Technologies Used](#technologies-used)
* [Deployment](#deployment)

  * [Local Deployment](#local-deployment)
  * [AWS Deployment](#aws-deployment)
* [Team Member](#team-member)
* [Live Application](#live-application)
* [GitHub Repository](#github-repository)
* [Conclusion](#conclusion)


---

# Project Overview

Portfolio Risk System is a cloud-based financial and market analysis platform designed to provide investment intelligence and portfolio monitoring capabilities. The project focuses on beauty and fashion companies and aims to combine cloud computing, monitoring, event-driven architecture, and scalable deployment into a single working ecosystem.

The application was developed using Spring Boot and deployed using Docker and AWS cloud services. It integrates cloud-native tools for backend hosting, event routing, monitoring, notifications, and data handling.

The platform provides users with a browser-based dashboard where market insights and portfolio-related information can be explored through a scalable cloud architecture.

---

# Problem Statement

Traditional financial and portfolio systems often struggle with scalability, centralized monitoring, and automated event processing.

A modern cloud-based solution requires:

* Scalable backend deployment
* Centralized monitoring
* Automated alerts
* Event-driven communication
* Reliable infrastructure deployment
* Cloud-native architecture

The Portfolio Risk System addresses these requirements using AWS services and containerized deployment.

---

# Objective

The main objectives of this project are:

* Build a cloud-based portfolio and market dashboard
* Focus on beauty and fashion companies
* Deploy scalable backend services using AWS ECS
* Implement event-driven architecture
* Enable monitoring and logging
* Use Docker for consistent deployment
* Demonstrate real-world AWS cloud integration

---

# Architecture Overview

The application follows a cloud-native architecture.

### User Browser

Users access the system through a browser using the deployed Load Balancer URL.

### Application Load Balancer (ALB)

The ALB routes incoming traffic to healthy backend containers and manages load balancing.

### ECS Backend

Amazon ECS hosts the Spring Boot backend using Docker containers.

The backend:

* Serves frontend files
* Handles API requests
* Performs health checks
* Processes business logic

### DynamoDB

DynamoDB is used as the cloud database layer for storing application data.

### EventBridge

EventBridge handles routing of system and risk-related events across services.

### SNS Email Alerts

SNS enables notification workflows and email alert functionality.

### CloudWatch Monitoring

CloudWatch provides:

* Application logs
* Monitoring
* Metrics
* Operational visibility

---

# AWS Services Used

| AWS Service | Purpose                        |
| ----------- | ------------------------------ |
| ECS         | Backend container hosting      |
| ALB         | Load balancing                 |
| DynamoDB    | Data storage                   |
| EventBridge | Event routing                  |
| SNS         | Email notifications            |
| CloudWatch  | Logs and monitoring            |
| ECR         | Docker image storage           |
| IAM         | Access control and permissions |

---

# Project Workflow

### Step 1: Application Development

Backend services were developed using Java Spring Boot.

### Step 2: Containerization

Docker was used to package the application and dependencies into containers.

### Step 3: Cloud Infrastructure

AWS resources were configured and deployed.

### Step 4: ECS Deployment

Backend containers were deployed and managed using Amazon ECS.

### Step 5: Monitoring and Health Checks

CloudWatch and ECS health metrics were used to verify service health.

### Step 6: Load Balanced Access

The Application Load Balancer routed users to healthy ECS tasks.

---

# Docker Implementation

Docker was used to containerize the application.

Benefits of Docker in this project:

* Consistent environments
* Easy deployment
* Dependency isolation
* Simplified scaling
* ECS compatibility

Docker ensured the application behaved consistently during both local and AWS deployment.

---

# Technologies Used

### Backend

* Java
* Spring Boot

### Frontend

* JavaScript
* HTML
* CSS

### Containerization

* Docker
* Docker Compose

### Cloud & Infrastructure

* AWS ECS
* IAM
* DynamoDB
* EventBridge
* SNS
* CloudWatch
* ALB
* ECR
* Terraform

---

# Deployment

## Local Deployment

The application can be executed locally using Docker.

Steps:

1. Clone repository
2. Run Docker containers
3. Launch backend and frontend
4. Access dashboard locally

Localhost deployment was used during testing and debugging.

---

## AWS Deployment

The project was deployed to AWS using ECS and containerized infrastructure.

Deployment included:

* ECS backend service
* Docker container deployment
* Load balancing through ALB
* Monitoring using CloudWatch
* Event handling through EventBridge
* Notification integration through SNS

---

# Team Member

### Sri Ramya Katrapalli

Email:
[sriramyakatrapalli@gmail.com](mailto:sriramyakatrapalli@gmail.com)

GitHub:
[Ramyak005](https://github.com/Ramyak005)

---

# Live Application

### AWS Deployment URL

[http://prs2026052711174122050000001f-1250655124.us-east-1.elb.amazonaws.com](http://prs2026052711174122050000001f-1250655124.us-east-1.elb.amazonaws.com/)

---

# GitHub Repository

Repository Link:

https://github.com/Ramyak005/Portfolio-risk-management.git

---

## 🎥 Project Demonstration Video

Watch the complete project walkthrough and deployment explanation here:

[▶ Watch Project Demo](https://drive.google.com/file/d/1fmPGEwE04APpSDrkXTDTn7F6TNDqMTbn/view?usp=sharing)

---

# Conclusion

Portfolio Risk System demonstrates how modern cloud services can be combined to create a scalable and monitored application ecosystem.

The project integrates containerization, cloud deployment, monitoring, and event-driven architecture while focusing on portfolio and market intelligence for beauty and fashion companies.

By using Docker and AWS services together, the system achieves scalability, reliability, and cloud-native deployment while showcasing practical implementation of enterprise cloud architecture.
