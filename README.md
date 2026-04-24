# Smart Campus Sensor & Room Management API


**Name:** Vinushki Fernando

**Student ID:** w2120303

**Module:** 5COSC022W Client-Server Architectures

**Coursework:** Smart Campus Sensor & Room Management API

**Demo Vidieo:** https://drive.google.com/file/d/1n3SWk1t8ET8h0HpwW_pYUGmCV4WwImmp/view?usp=sharing



---

## API Overview

A RESTful API built with **JAX-RS (Java EE 8)** and deployed on **Payara Server 5** for managing campus rooms, sensors, and sensor readings.
This project was developed for the **5COSC022W Client-Server Architectures coursework**. The system represents a Smart Campus backend service where university rooms can contain multiple sensors, and each sensor can store historical sensor readings.

| Property | Details |
|---|---|
| Base URL | `http://localhost:8080/SmartCampusAPI/api/v1` |
| Architecture | REST (JAX-RS / Java EE 8) |
| Data Format | JSON |
| Storage | In-memory ConcurrentHashMap (no database) |
| Server | Payara Server 5 |


---

## Main Features

* RESTful API built with JAX-RS (Java EE 8) and deployed on Payara Server
* Discovery endpoint at `GET /api/v1/` implementing HATEOAS with navigational resource links
* Full room management including creation, retrieval, and safe deletion with sensor conflict detection
* Sensor management with referential integrity validation against existing rooms
* Sensor filtering using `@QueryParam` e.g. `GET /api/v1/sensors?type=Temperature`
* Sub-Resource Locator pattern using a dedicated `SensorReadingResource` class for reading history
* Automatic update of a sensor's `currentValue` when a new reading is posted
* Custom ExceptionMappers for 409, 422, 403, and 500 returning structured JSON error responses
* Global safety net mapper preventing raw Java stack traces from being exposed to clients
* Request and response logging using `ContainerRequestFilter` and `ContainerResponseFilter`
* All data stored in-memory using `ConcurrentHashMap` — no database required

---

## Data Models

### Room

A room contains an ID, name, capacity, and a list of assigned sensor IDs.

### Sensor

A sensor contains an ID, type, status, current value, and the room ID of the room where it is installed.

### Sensor Reading

A sensor reading contains an ID, timestamp, and recorded value.

---

## How to Build and Run the Project

### Prerequisites

Before running the project, make sure the following are installed:

- Java JDK 8 or Java JDK 11
- Apache Maven
- Payara Server 5
- Postman or curl for API testing

### Step 1: Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_LINK_HERE
cd SmartCampusAPI
```

### Step 2: Build the Project

```bash
mvn clean package
```

After the build is complete, the WAR file will be created inside the `target` folder:

```text
target/SmartCampusAPI.war
```

### Step 3: Deploy the WAR File to Payara Server

Start Payara Server and deploy the generated WAR file.

Using the Payara Admin Console:

1. Open the Payara Admin Console.
2. Go to Applications.
3. Click Deploy.
4. Select `target/SmartCampusAPI.war`.
5. Deploy the application.

Using the command line:

```bash
asadmin deploy target/SmartCampusAPI.war
```

### Step 4: Access the API

After deployment, the API can be accessed from:

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## API Endpoints

### Discovery Endpoint

- `GET /api/v1` — API discovery endpoint.

### Room Endpoints

- `POST /api/v1/rooms` — Create a new room.
- `GET /api/v1/rooms` — Retrieve a list of all rooms.
- `GET /api/v1/rooms/LIB-301` — Retrieve room `LIB-301`.
- `DELETE /api/v1/rooms/LIB-301` — Attempt to delete room `LIB-301`.

### Sensor Endpoints

- `POST /api/v1/sensors` — Register a valid sensor.
- `GET /api/v1/sensors` — Retrieve all sensors.
- `GET /api/v1/sensors?type=Temperature` — Retrieve filtered temperature sensors.
- `POST /api/v1/sensors` — Attempt sensor creation with a fake room.
- `POST /api/v1/sensors` — Register a maintenance sensor.
- `GET /api/v1/sensors/TEMP-001` — Retrieve sensor `TEMP-001`.

### Sensor Reading Endpoints

- `POST /api/v1/sensors/MAINT-001/readings` — Attempt to add a reading while the sensor is in maintenance.
- `POST /api/v1/sensors/TEMP-001/readings` — Add a new sensor reading.
- `GET /api/v1/sensors/TEMP-001/readings` — Retrieve reading history for sensor `TEMP-001`.

### Error Testing Endpoint

- `GET /api/v1/debug/crash` — Trigger an unexpected server error to test the global exception mapper.

---

## Sample curl Commands

### API Discovery Endpoint

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### Create a Room

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### Retrieve All Rooms

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### Retrieve Room LIB-301

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### Register a Valid Sensor

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### Retrieve All Sensors

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### Filter Sensors by Type

```bash
curl -i -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

### Attempt Sensor Creation with a Fake Room

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### Register a Maintenance Sensor

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### Attempt Reading While Sensor is in Maintenance

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/MAINT-001/readings
```

### Add New Sensor Reading

```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### Retrieve Reading History

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### Retrieve Sensor TEMP-001

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001
```

### Attempt Delete with Assigned Sensors

```bash
curl -i -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### Trigger Unexpected Error

```bash
curl -i -X GET http://localhost:8080/SmartCampusAPI/api/v1/debug/crash
```

---

# Conceptual Report

## Part 1 — Service Architecture and Setup

### Question 1

**Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.**

By default, JAX-RS resource classes have a request-scoped lifecycle. This implies that each incoming HTTP request causes a new instance of the resource class to be created on JAX-RS runtime Payara Server, and used to process the request after which it is destroyed after the response has been returned. The other is the singleton lifecycle with @Singleton, in which there is a single instance that serves all the requests. Due to this request scoped default, any instance level variables within resource classes such as RoomResource or SensorResource would be lost with each request.

To address this, I introduced a separate DataStore class that has static fields. Statics exist at the JVM class level and not instance level, that is, the data will remain as long as the application is running, no matter how many instances of resources are created and destroyed. 
Because there is a possibility of several HTTP requests being received at the same time and all of them use the same static DataStore, thread safety is of utmost importance. The data collections that I used all three rooms, sensors, and readings with ConcurrentHashMap.

ConcurrentHashMap supports many threads reading simultaneously and supports write operations in a safe manner without explicit synchronisation blocks, eliminating race conditions which could result otherwise, should two requests attempt to register a sensor or create a room at exactly the same time.


### Question 2

**Why is the provision of Hypermedia (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**

Hypermedia as the Engine of Application State (HATEOAS) is regarded as a characteristic feature of the advanced RESTful design since it enables the server to provide the clients with the dynamism of the API by placing navigational links within the responses directly. Instead of using hardcoded URLs, or depending on the external documentation, clients can learn what actions and resources are available by browsing the links displayed by the server.

This is helpful in a number of ways to client developers. When the server modifies an endpoint structure, clients using hypermedia links automatically resiliently adapt. It also causes the API to be self-documenting and less reliant on the static documentation which could be redundant. In the current project, a Discovery endpoint at GET /api/v1/ shows this by providing links to /api/v1/rooms and /api/v1/sensors to allow any client a navigable starting point to explore the entire API in a one-request.

---

## Part 2 — Room Management

### Question 3

**When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side 
processing.**

Sending room IDs alone saves network bandwidth as a large payload is minimized and this can be useful in scenarios where the collections are very large. Nonetheless, it also adds considerable complexity to the client-side since the client has to make a separate GET request to access the actual room details per ID, with the effect of adding several network round trips and increasing overall latency.

Full room objects are returned to increases the payload size and serve the whole required data in one response, so the API is much more convenient to the client. In this project, full room objects are the right decision since the dataset is not very large, the room objects are not too huge, and it maintains the API uncomplicated and simple to use without having to combine various requests to each other.


### Question 4

**Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**


Yes, DELETE operation is idempotent in this implementation. In case of a DELETE request to a room that exists, but has no sensors assigned, the room is deleted and the server sends 204 No Content. When the same DELETE request is sent twice, the room has already been deleted and hence the server replies with 404 Not Found. 

The response codes in the first and second calls are different, but after every repeated request the server state is the same - the room just does not exist. Repeated calls do not produce any additional side effects. This fulfills the meaning of idempotency, which is that repeated calls to the same request result in the same final state on the server, no matter the number of times it is invoked. The business logic constraint also holds true, since the sensors are still allocated, all the DELETEs will be returned with 409 Conflict, and nothing will change.


---

## Part 3 — Sensor Operations and Linking

### Question 5

**We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on 
the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle 
this mismatch?**

The @Consumes(MediaType.APPLICATION_JSON) annotation indicates to the JAX-RS runtime on Payara that the resource method will accept request bodies only with a Content-Type of application/json. When a client makes a request using a different content type like text/plain or application/xml, then the JAX-RS runtime will see the mismatch and not even forward the request to the resource method. In this case, JAX-RS sends an automatic response of HTTP 415 Unsupported Media Type. 

This imposes a strict API contract, does not allow incompatible or malformed payloads to be processed by the business logic and eliminates internal parsing errors that would otherwise arise in case the MessageBodyReader tried to deserialise an XML or plain text body into a Java Sensor object. It also gives a clear informative error signal to the client, showing exactly what type of content is expected.

### Question 6

**You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?**

The preferred method of filtering is query parameter since they represent an optional constraint on an existing collection resource without defining a new resource. The endpoint /api/v1/sensors is the entire set of sensors and appending to it with the query type=Temperature will only refine the result set, but not introduce a new conceptual resource. It is in line with the REST standards, in which path segments can represent resources and query parameters can alter or filter representations. 

The query parameter method is also more adaptable and extendable. It is easy to combine multiple filters, e.g. ?type=CO2&status=ACTIVE, but it does not alter the URL format. Contrary, placing the filter in the path like /api/v1/sensors/type/CO2 would mean that type/CO2 is a separate resource, which is not semantically accurate. It also renders the API inflexible - a second filter would have to be designed in a way to create a whole new set of path structures which would not result in the proliferation of unnecessary endpoints. 


---

## Part 4 — Deep Nesting with Sub-Resources

### Question 7

**Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**

Sub-Resource Locator pattern helps enhance modularity, separation of concern, and long-term maintainability as well. In this project, instead of installing all sensor and reading logic in a single large controller, the SensorResource class serves as a router. When a request is received under /api/v1/sensors/{sensorId)/readings, the sub-resource locator method of SensorResource handles a new SensorReadingResource, and the JAX-RS runtime on Payara refers to the request to that resource. 

This implies that each of the classes has only one, distinct task - SensorResource deals with sensors and SensorReadingResource deals with reading history. With large APIs and lots of nested resources, having all the paths in a single controller results in a monolithic class that is hard to read, test, and extend. The sub-resource locator pattern maintains the hierarchy of URIs explicit and sensible and splits the implementation up into small, cohesive classes. 


---

## Part 5 — Advanced Error Handling, Exception Mapping and Logging

### Question 8

**Why is HTTP 422 often considered more semantically accurate than a standard 404 
when the issue is a missing reference inside a valid JSON payload?**

In this case, HTTP 422 Unprocessable Entity is more semantically correct since the issue is not the absence of a URL resource but rather a logically invalid value in a otherwise valid request. In the event that a client submits a new sensor with a roomId that is non-existent, the request URI /api/v1/sensors is still valid and the JSON payload can be syntactically correct. It can be parsed without any problem by the server and it just cannot work since the referred room is not there.

A 404 Not Found would be a misleading information to the client that the endpoint was not found, which is false. 422 conveys just this: the payload was received and interpreted, but had a semantic error - the dependency it mentioned was not available. This provides the client with the precise information to correct the request and the API becomes more accurate and less cumbersome to consume.


### Question 9

**From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**

The leakage of internal Java stack traces to external API consumers is a critical cybersecurity threat since it discloses sensitive information on the internal structure of the application. Based on a stack trace, an attacker may be able to obtain package names, class names, method names, file paths, framework version, library names and version, database query logic and the specific line number on which the error was found. 

This data enables a hacker to map the system architecture, detect obsolete libraries that are vulnerable to attacks, and develop targeted exploits against certain vulnerabilities. As an example, being aware of the precise version of a library might enable an attacker to consult published CVEs of that version. The GlobalExceptionMapper used in this project intercepts all Throwable instances and records the entire stack trace safely on the Payara server to be viewed by the administrator, and sends a generic sanitised JSON response to the client - no internal information should be ever leaked over the API response.


### Question 10

**Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**

The benefits of logging with JAX-RS filters are that logging is a cross-cutting concern, and must apply uniformly to all endpoints of the API. The class LoggingFilter in this project achieves this by implementing both ContainerRequestFilter and ContainerResponseFilter, i.e. it automatically intercepts all incoming requests and outgoing responses with no resource method having to be changed. 

By adding Logger.info() statements into each resource method manually, the code would be very repetitive and difficult to maintain. There would also be easy to forget to include logging when creating new endpoints, resulting in inconsistent observability. Logging through a single filter class reduces all the resource methods to business logic only, simplifies the codebase, and provides a consistent logging behaviour throughout the API without the possibility of oversights. This style directly reflects the separation of concerns principle taught in the module.

---
