<p align="center">
  <a href="" rel="noopener">
 <img width=200px height=200px src="https://i.imgur.com/6wj0hh6.jpg" alt="Project logo"></a>
</p>

<h3 align="center">Simple Selling Application</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![GitHub Issues](https://img.shields.io/github/issues/kylelobo/The-Documentation-Compendium.svg)](https://github.com/kylelobo/The-Documentation-Compendium/issues)
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/kylelobo/The-Documentation-Compendium.svg)](https://github.com/kylelobo/The-Documentation-Compendium/pulls)
[![License](https://img.shields.io/badge/license-GPLv3-blue)](/LICENSE)

</div>

---

<p align="center"> Few lines describing your project.
    <br> 
</p>

## 📝 Table of Contents

- [Overview](#overview)
- [Getting Started](#getting_started)
- [Deployment](#deployment)
- [Develop Structurer](#structure)
- [License](#license)

## 🧐 Overview <a name = "about"></a>

Simple Selling Application is a ready-to-use solution designed for small and medium businesses (SMBs) looking to streamline their sales operations. This application provides an intuitive and efficient platform for managing sales while keeping operational costs minimal.

### Features

- **Business-Friendly**: Specifically designed for SMBs to simplify the selling process.
- **Ready-to-Use**: No extensive setup required; start managing sales immediately.
- **User Behavior** Data Aggregation: Collects and processes raw data for AI-driven insights and analytics.
- **Cost-Effective**: Runs on **a minimal budget** while maintaining efficiency and scalability.
- **Standardized Operations**: Ensures smooth and uniform business processes for improved workflow management.

### Benefits

- Gain AI-driven insights from user behavior data.
- **Reduce operational costs** while maintaining efficiency.
- Simplify sales management with an **easy-to-use** interface.
- Improve **decision-making** with structured and standardized processes.

## 🏁 Getting Started <a name = "getting_started"></a>

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See [deployment](#deployment) for notes on how to deploy the project on a live system.

### Prerequisites

```
**Java version 21, Spring Boot version 3.4.1**
```

### Insight

Run and generate test reports to HTML page with Sunfire

```
make report
```

Docker compose up / down all services

```
make up / down
```

Docker compose email up / down (dev only)

```
make up/down-email
```

### And coding style tests

Layer architecture, monolithic architecture

## 🚀 Deployment <a name = "deployment"></a>

Add additional notes about how to deploy this on a live system.

## 🏗️ Development Structure <a name="structure"></a>

```
├── .env.dev
├── .gitattributes
├── .gitignore
├── .mvn
    └── wrapper
    │   └── maven-wrapper.properties
├── Dockerfile
├── LICENSE
├── Makefile
├── README.md
├── docker-compose.app.yaml
├── docker-compose.db.yaml
├── docker-compose.email.yaml
├── docker-compose.merged.yaml
├── docker-compose.yaml
├── gitleaks.toml
├── helm
    ├── Chart.yaml
    ├── README.md
    └── templates
    │   ├── pgadmin-deployment.yaml
    │   ├── pgadmin-persistentvolumeclaim.yaml
    │   ├── pgadmin-service.yaml
    │   ├── postgres-deployment.yaml
    │   ├── postgres-persistentvolumeclaim.yaml
    │   ├── postgres-service.yaml
    │   ├── simple-selling-application-deployment.yaml
    │   └── simple-selling-application-service.yaml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │   │   └── shitcode
    │   │   │       └── demo1
    │   │   │           ├── Demo1Application.java
    │   │   │           ├── annotation
    │   │   │               ├── logging
    │   │   │               │   ├── LogCollector.java
    │   │   │               │   └── impl
    │   │   │               │   │   └── LoggingCollectorAspect.java
    │   │   │               ├── spring
    │   │   │               │   ├── LazyAutowired.java
    │   │   │               │   ├── LazyComponent.java
    │   │   │               │   └── LazyConfiguration.java
    │   │   │               └── validation
    │   │   │               │   ├── DoEquals.java
    │   │   │               │   ├── DoNotEquals.java
    │   │   │               │   ├── Email.java
    │   │   │               │   ├── GreaterOrEquals.java
    │   │   │               │   ├── GreaterThan.java
    │   │   │               │   ├── LessThan.java
    │   │   │               │   ├── Password.java
    │   │   │               │   ├── Phone.java
    │   │   │               │   └── impl
    │   │   │               │       ├── DoEqualsValidator.java
    │   │   │               │       ├── DoNotEqualsValidator.java
    │   │   │               │       ├── EmailValidator.java
    │   │   │               │       ├── GreaterOrEqualsValidator.java
    │   │   │               │       ├── GreaterThanValidator.java
    │   │   │               │       ├── LessThanValidator.java
    │   │   │               │       ├── PasswordValidator.java
    │   │   │               │       └── PhoneValidator.java
    │   │   │           ├── component
    │   │   │               ├── DatabaseLock.java
    │   │   │               └── IpAddressResolver.java
    │   │   │           ├── config
    │   │   │               ├── CacheConfig.java
    │   │   │               ├── Ip2LocationConfig.java
    │   │   │               ├── JwtConfig.java
    │   │   │               ├── MessagesConfig.java
    │   │   │               ├── OpenAPIConfiguration.java
    │   │   │               └── SecurityConfig.java
    │   │   │           ├── controller
    │   │   │               ├── AuthController.java
    │   │   │               ├── CategoryController.java
    │   │   │               └── ProductController.java
    │   │   │           ├── dto
    │   │   │               ├── AbstractAuditableEntity.java
    │   │   │               ├── AuthDTO.java
    │   │   │               ├── CategoryDTO.java
    │   │   │               ├── DiscountDTO.java
    │   │   │               ├── GenericDTO.java
    │   │   │               ├── ProductDTO.java
    │   │   │               ├── ProductInteractionDTO.java
    │   │   │               ├── ResponseDTO.java
    │   │   │               └── SpringUserDTO.java
    │   │   │           ├── entity
    │   │   │               ├── AbstractAuditableEntity.java
    │   │   │               ├── Category.java
    │   │   │               ├── Discount.java
    │   │   │               ├── Product.java
    │   │   │               ├── ProductInteraction.java
    │   │   │               ├── RegistrationToken.java
    │   │   │               └── SpringUser.java
    │   │   │           ├── exception
    │   │   │               ├── handler
    │   │   │               │   ├── ApplicationExceptionHandler.java
    │   │   │               │   ├── GlobalExceptionHandler.java
    │   │   │               │   └── LogExceptionAspect.java
    │   │   │               └── model
    │   │   │               │   ├── AspectException.java
    │   │   │               │   ├── CacheEvictionException.java
    │   │   │               │   ├── CacheMissException.java
    │   │   │               │   ├── ConflictTokenException.java
    │   │   │               │   ├── DiscountOverTimeException.java
    │   │   │               │   ├── EntityExistsException.java
    │   │   │               │   ├── EntityNotChangedException.java
    │   │   │               │   ├── EntityNotFoundException.java
    │   │   │               │   ├── ErrorModel.java
    │   │   │               │   ├── InvalidRequestException.java
    │   │   │               │   ├── KeyLockMissedException.java
    │   │   │               │   ├── ResourceNotFoundException.java
    │   │   │               │   ├── RevokeTokenException.java
    │   │   │               │   ├── SendingMailException.java
    │   │   │               │   ├── TokenExpiredException.java
    │   │   │               │   ├── UserDisabledException.java
    │   │   │               │   ├── UserUnAuthException.java
    │   │   │               │   └── WorkerBusyException.java
    │   │   │           ├── helper
    │   │   │               ├── DateFormatConverter.java
    │   │   │               ├── DatetimeFormat.java
    │   │   │               ├── DiscountDateTimeConverter.java
    │   │   │               ├── PaginationProvider.java
    │   │   │               └── RoleConverter.java
    │   │   │           ├── init
    │   │   │               ├── ApplicationInitializer.java
    │   │   │               └── SQLRunner.java
    │   │   │           ├── jwt
    │   │   │               └── JwtService.java
    │   │   │           ├── mapper
    │   │   │               ├── CategoryMapper.java
    │   │   │               ├── DiscountMapper.java
    │   │   │               ├── ProductMapper.java
    │   │   │               └── SpringUserMapper.java
    │   │   │           ├── properties
    │   │   │               ├── AccountsConfigData.java
    │   │   │               ├── AuthTokenConfigData.java
    │   │   │               ├── ClientConfigData.java
    │   │   │               ├── FontendServerConfigData.java
    │   │   │               ├── JwtConfigData.java
    │   │   │               ├── LvoxxServerConfigData.java
    │   │   │               ├── MailingConfigData.java
    │   │   │               ├── RateLimiterConfigData.java
    │   │   │               ├── RsaKeyConfigData.java
    │   │   │               └── SecurityPathsConfigData.java
    │   │   │           ├── repository
    │   │   │               ├── CategoryRepository.java
    │   │   │               ├── DiscountRepository.java
    │   │   │               ├── ProductInteractionRepository.java
    │   │   │               ├── ProductRepository.java
    │   │   │               ├── RegistrationTokenRepository.java
    │   │   │               └── SpringUserRepository.java
    │   │   │           ├── scheduler
    │   │   │               └── CheckingExpiredDiscountScheduler.java
    │   │   │           ├── security
    │   │   │               ├── JWTAuthenticationEntryPoint.java
    │   │   │               ├── SpringUserDetails.java
    │   │   │               └── SpringUserDetailsService.java
    │   │   │           ├── service
    │   │   │               ├── AuthService.java
    │   │   │               ├── CategoryService.java
    │   │   │               ├── DiscountService.java
    │   │   │               ├── InterationEventService.java
    │   │   │               ├── Ip2LocationService.java
    │   │   │               ├── MailService.java
    │   │   │               ├── ProductService.java
    │   │   │               ├── RateLimiterService.java
    │   │   │               ├── RegistrationTokenService.java
    │   │   │               ├── ResponseService.java
    │   │   │               ├── SpringUserService.java
    │   │   │               └── impl
    │   │   │               │   ├── AuthServiceImpl.java
    │   │   │               │   ├── CategoryServiceImpl.java
    │   │   │               │   ├── DiscountServiceImpl.java
    │   │   │               │   ├── InterationEventServiceImpl.java
    │   │   │               │   ├── Ip2LocationServiceImpl.java
    │   │   │               │   ├── MailServiceImpl.java
    │   │   │               │   ├── ProductServiceImpl.java
    │   │   │               │   ├── RateLimiterServiceImpl.java
    │   │   │               │   ├── RegistrationTokenServiceImpl.java
    │   │   │               │   ├── ResponseServiceImpl.java
    │   │   │               │   └── SpringUserServiceImpl.java
    │   │   │           └── utils
    │   │   │               ├── ApplicationCache.java
    │   │   │               ├── DiscountType.java
    │   │   │               ├── InteractionEvent.java
    │   │   │               ├── KeyLock.java
    │   │   │               ├── LogPrinter.java
    │   │   │               ├── LoggingModel.java
    │   │   │               ├── RateLimiterPlan.java
    │   │   │               └── cache
    │   │   │                   ├── CategoryCacheType.java
    │   │   │                   ├── DiscountCacheType.java
    │   │   │                   ├── Ip2LocationCacheType.java
    │   │   │                   ├── ProductCacheType.java
    │   │   │                   └── UserCacheType.java
    │   └── resources
    │   │   ├── application.yml
    │   │   ├── banner.txt
    │   │   ├── certs
    │   │       ├── private-key.pem
    │   │       └── public-key.pem
    │   │   ├── config
    │   │       ├── accounts.yml
    │   │       ├── auth.yml
    │   │       ├── database.yml
    │   │       ├── email.yml
    │   │       ├── logging.yml
    │   │       ├── rate-limiter.yml
    │   │       ├── security.yml
    │   │       └── server.yml
    │   │   ├── database
    │   │       ├── categories.sql
    │   │       ├── discounts.sql
    │   │       └── products.sql
    │   │   ├── html
    │   │       └── activation.htm
    │   │   ├── ip2location
    │   │       ├── IP2LOCATION-LITE-DB1.BIN
    │   │       ├── LICENSE-CC-BY-SA-4.0.TXT
    │   │       └── README_LITE.TXT
    │   │   ├── logback.xml
    │   │   └── message
    │   │       └── messages.yaml
    └── test
    │   └── java
    │       └── com
    │           └── shitcode
    │               └── demo1
    │                   ├── controller
    │                       ├── AuthControllerTest.java
    │                       └── CategoryControllerTest.java
    │                   ├── jwt
    │                       └── JwtServiceTest.java
    │                   ├── repository
    │                       ├── CategoryRepositoryTest.java
    │                       ├── DiscountRepositoryTest.java
    │                       ├── ProductInteractionRepositoryTest.java
    │                       ├── ProductRepositoryTest.java
    │                       ├── RegistrationTokenRepositoryTest.java
    │                       └── SpringUserRepositoryTest.java
    │                   ├── service
    │                       ├── AuthServiceTest.java
    │                       ├── CategoryServiceTest.java
    │                       ├── Ip2LocationServiceTest.java
    │                       ├── MailServiceTest.java
    │                       ├── RegistrationTokenServiceTest.java
    │                       └── SpringUserServiceTest.java
    │                   └── testcontainer
    │                       ├── AbstractRepositoryTest.java
    │                       └── PostgresTestContainerConfig.java
└── wait-for-it.sh

```

## 🧾 License <a name = "license"></a>
This project is licensed under the Gnu General Public License. See the LICENSE file for details.