# Secure Resource Booking API

A secure RESTful Resource Booking API built using Spring Boot, Java, Spring Security, JWT Authentication, PostgreSQL, and JPA/Hibernate.

The application provides authentication, role-based access control, resource management, reservation management, ownership protection, filtering, pagination, sorting, validation, and structured error handling.

---

## Technologies Used

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT Authentication
- BCrypt Password Hashing
- PostgreSQL
- Hibernate / JPA
- Maven
- Postman
- Lombok
- Jakarta Bean Validation

---

## Features

### Authentication & Security

- JWT-based authentication
- BCrypt password hashing
- Stateless authentication
- Role-based access control
- ADMIN and USER roles
- Protected API endpoints

### Resource Management

ADMIN users can:

- Create resources
- View resources
- Update resources
- Delete resources

USER users can:

- View resources
- View individual resources

### Reservation Management

USER users can:

- Create reservations
- View their own reservations
- View individual reservations belonging to them

ADMIN users can:

- View all reservations
- View individual reservations
- Create reservations
- Update reservations
- Update reservation status
- Delete reservations

Reservation statuses:

- PENDING
- CONFIRMED
- CANCELLED

### Filtering, Pagination & Sorting

Reservations support:

- Status filtering
- Minimum price filtering
- Maximum price filtering
- Pagination using `page` and `size`
- Sorting using Spring Data sorting parameters

Example:

```text
GET /reservations?page=0&size=10&sort=id,desc