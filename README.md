# CivicConnect - Public Grievance Portal

CivicConnect is a full-stack web application where citizens report civic issues (potholes, water supply, electricity, sanitation) and track them using a reference number. An admin logs in, reviews complaints and updates their status until they are resolved.

Built for an individual 2-hour hackathon (Problem 24: CivicConnect - Public Grievance Portal).

## Features

**Citizen (no login required)**
- Submit a complaint with name, phone, category, location and description
- Receive a unique reference number (for example `CC-00001`)
- Track the status and remarks of a complaint using the reference number

**Admin (login required)**
- Log in with admin credentials
- View all submitted complaints
- Update the status (`SUBMITTED`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`) and add remarks

**General**
- Input validation (name and description are required)
- Error handling (404 for an unknown reference number, 401 for a wrong login)

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript (`fetch` API) |
| Backend | Java 17, Spring Boot 4.1.2 (Spring Web) |
| Database | H2 in-memory database, Spring Data JPA |
| Build tool | Maven (Maven Wrapper included) |

## Architecture

```
Browser (HTML / CSS / JavaScript)
        |  fetch() calls, JSON
        v
Spring Boot REST API (ComplaintController)
        |
Spring Data JPA (ComplaintRepository)
        |
H2 in-memory database
```

## Project Structure

```
demo/
├── pom.xml
├── mvnw / mvnw.cmd
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java       # Application entry point
    │   ├── Complaint.java             # Entity (database table)
    │   ├── ComplaintRepository.java   # Data access (JPA)
    │   └── ComplaintController.java   # REST API endpoints
    └── resources/
        ├── application.properties     # Database configuration
        └── static/
            └── index.html             # Complete frontend page
```

## API Endpoints

| Method | URL | Description | Access |
|---|---|---|---|
| POST | `/api/complaints` | Submit a new complaint | Public |
| GET | `/api/complaints/track/{ref}` | Track a complaint by reference number | Public |
| POST | `/api/complaints/login` | Admin login | Public |
| GET | `/api/complaints` | List all complaints | Not enforced by the server |
| PUT | `/api/complaints/{id}/status` | Update status and remarks | Not enforced by the server |

### Example: submit a complaint

```
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -d '{"name":"Ravi","phone":"9999999999","category":"Road","location":"Chennai","description":"Pothole near school"}'
```

Response:
```json
{"id":1,"referenceNo":"CC-00001","name":"Ravi","category":"Road","status":"SUBMITTED","remarks":null, "...": "..."}
```

## Prerequisites

- JDK 17 or later
- Internet access for the first build (to download Maven dependencies)

## How to Run

1. Open a terminal in the project folder (the folder containing `pom.xml`).
2. Start the application:

   Windows:
   ```
   .\mvnw.cmd spring-boot:run
   ```

   Mac / Linux:
   ```
   ./mvnw spring-boot:run
   ```
3. Wait for the line `Started DemoApplication`.
4. Open **http://localhost:8080** in your browser.

To stop the application, press `Ctrl+C` in the terminal and type `Y` when asked.

To run without internet after one successful build, add `-o`:
```
.\mvnw.cmd -o spring-boot:run
```

## Admin Login

Set `ADMIN_USERNAME` and `ADMIN_PASSWORD` in the environment before starting the application. Admin login is disabled if either variable is missing.

Windows PowerShell:
```
$env:ADMIN_USERNAME = "your-admin-username"
$env:ADMIN_PASSWORD = "your-strong-password"
.\mvnw.cmd spring-boot:run
```

Do not commit real credentials to the repository.

## How to Use

1. **Submit:** fill in the complaint form and click Submit. Note the reference number shown.
2. **Track:** enter the reference number in "Track Your Complaint" and click Track.
3. **Admin:** log in, change a complaint's status, add remarks and click Update.
4. **Track again** to see the updated status and remarks.

## Design Decisions

- **Reference number:** generated from the auto-incremented database ID, formatted as `CC-` plus a 5-digit number, so it is unique and readable.
- **Simple status field:** keeps the workflow easy to understand and extend.
- **H2 in-memory database and plain HTML/JS:** no external setup, which suits a time-limited, offline hackathon.

## Known Limitations

- Admin login only hides the admin section in the page; the admin API endpoints are not protected on the server.
- Admin credentials must be provided through environment variables and are not stored in the repository.
- Data is stored in memory and is lost when the application restarts.
- No file or photo attachments.

## Future Improvements

- Spring Security with JWT authentication, hashed passwords and separate citizen and admin roles
- Persistent database (MySQL or PostgreSQL)
- Photo and document upload with complaints
- Email or SMS notifications when the status changes
- Search, filters and pagination in the admin table
- Department assignment and a summary dashboard
