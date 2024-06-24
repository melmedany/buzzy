# Buzzy: Modular & Distributed Chat System

Buzzy is a modular and distributed chat system designed to provide scalable, secure, and efficient communication solution. It consists of several interconnected modules that work together seamlessly to offer a comprehensive chat experience.

## Architecture Design

![Buzzy Architecture](buzzy.svg)



The architecture of Buzzy revolves around:

- Centralized Authentication/Authorization Server
- Chatting API
- Communication channel for streaming events within the system
- Frontend Client to Access the System from Browser

Each module of this monorepo plays a crucial role in the overall functionality of Buzzy:

- **buzzy-sso** handles user registration, authentication and authorization centrally, ensuring secure access to the system.
- **buzzy-api** provides the core chat functionalities, enabling users to send / receive messages, create and manage conversations.
- **buzzy-webapp** offers a user-friendly interface through which users can interact with the chat system via web browsers.

## Contents of Repository

- **/buzzy-sso**: Contains the authentication and authorization server.
- **/buzzy-api**: Implements the chat functionalities via API.
- **/buzzy-common**: Shared library for utilities and shared models.
- **/buzzy-webapp**: Frontend client for web-based access.

## Usage

### Prerequisites

* Basic dev environment with JDK installed (preferably latest version)
* Docker Compose
* Make sure that ports `9000` `8088` `3000` `5432` and `15432` are not used, as they are needed for build and run. Alternatively, you can configure the desired ports to use on [docker-compose.yml](docker-compose.yml) for each service

### Build

1. Clone the repo.
   ```
   git clone https://github.com/melmedany/buzzy.git
   ```

2. Point your terminal to the cloned repo.
    ```
    cd buzzy
    ```

4. Run tests and build.
    ```
    ./gradlew clean build

### Usage

You have successfully built/fetched all dependencies, now it is time to run it.

1. Build image and run it.
    ```
     docker compose up -d buzzy_webapp
    ```
   Now you have 5 running docker containers

2. Now by navigating to [Main](http://localhost:3000) page, you will be able to log in page.

3. System is still under active development and more features will be added.