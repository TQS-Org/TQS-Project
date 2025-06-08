# TQS-Project
Projeto de TQS 2024-2025 - ChargeHub

## Abstract

This project was developed in the context of the Test and Quality Software (TQS) course, which emphasizes building robust, testable, and maintainable software systems. Throughout the project, we applied principles of agile development, user-centered design, and quality assurance.

Our product, ChargeHub, is a digital platform designed to improve the experience of electric vehicle (EV) drivers and facilitate efficient management for station operators. It aims to simplify the process of discovering, reserving, and managing EV charging sessions, while also providing essential administrative tools. Below lies a comprehensive list of our core functionalities:

#### Station Discovery
- Search for nearby charging stations based on location.
- Filter stations by charger type, availability, and cost.

#### Slot Booking & Scheduling
- Reserve a charging slot in advance.

#### Charging Session Management
- Start/stop charging sessions using a reservation token.
- View real-time session data including time and energy used.

#### Payment Integration
- Review and pay for charging sessions within the app.

#### Back Office Operations for Station Operators
- Update station data such as pricing and availability.
- Admin features include account creation and station assignment.

## Project Team

| Name | Email | NMEC | Role |
| ---- | ----- | ---- | ---- |
| Rodrigo Abreu | rodrigo.abreu@ua.pt | 113626 | QA Engineer |
| Hugo Ribeiro | hugo.ribeiro04@ua.pt | 113402​ | Team Leader |
| João Neto | jneto04@ua.pt | 113482 | Product Owner |
| Eduardo Lopes | eduardolplopes@ua.pt | 103070 | DevOps Master |

## Running 

Ensure you have [Docker]([https://](https://www.docker.com/)) installed, with [Docker Compose]([https://](https://docs.docker.com/compose/)) available.

Go to the ChargeHub directory.

To run the development environment:

```bash
docker compose up -d
```

To run the production environment:

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

## Deployment

Only accessible within the UA network or with UA VPN

http://deti-tqs-23.ua.pt:3000/

## Documentation

[Swagger]([https://](https://swagger.io/)) documentation based on the OpenAPI specification can be accessed at the following endpoint:

- Development: `http://localhost:8080/swagger-ui/index.html`
- Production: `http://deti-tqs-23.ua.pt:8080/swagger-ui/index.html`

### Project Backlog

https://hugod.atlassian.net/jira/software/projects/SCRUM/boards/1


### Project specification report

You can find the pdf version of the final Project Specification Report [here](<reports/TQS Product Specification Report.pdf>).

### Project QA Manual

You can find the pdf version of the final Project QA Manual [here](<reports/QA - Manual.pdf>).