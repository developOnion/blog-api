# Blog API

A RESTful API for a personal blog application, providing endpoints for managing blog posts, user authentication, and health monitoring.

**Base URL:** `https://blog-api-1-0-dvt9.onrender.com`

## 🛠 Tool Technologies

- **Framework:** Spring Boot 4.0.3
- **Language:** Java 21
- **Database:** PostgreSQL
- **Persistence:** Spring Data JPA (Hibernate)
- **Migrations:** Flyway
- **Security:** Spring Security with JWT (JSON Web Token)
- **Documentation:** SpringDoc OpenAPI / Swagger UI
- **Utilities:** Lombok, Slugify, Jackson (JSR-310 support)

## 🏗 Application Architecture

The project follows a standard **N-tier architecture**:

- **Controllers:** Handle incoming REST HTTP requests, route them appropriately, and define API endpoints.
- **Services:** Implement the core business logic of the application.
- **Repositories:** Interface with the PostgreSQL database using Spring Data JPA.
- **Entities:** Represent the data model (PostgreSQL). Most entities extend `BaseEntity` for common fields (`id`, `createdAt`, `updatedAt`, `version`).
- **DTOs (Data Transfer Objects):** Request and Response objects (e.g., Records) are used to decouple the API from the internal data model.

## 🔌 API Endpoints

### Authentication Endpoints (`/auth`)

#### 1. Login User
- **URL:** `POST /auth/login`
- **Description:** Authenticates a user and returns a JWT token.
- **Example Request:**
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
- **Example Response (200 OK):**
  ```json
  {
    "access_token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

#### 2. Register User
- **URL:** `POST /auth/register`
- **Description:** Registers a new user.
- **Example Request:**
  ```json
  {
    "username": "newuser",
    "password": "password"
  }
  ```
- **Example Response (201 Created):**
  ```json
  {
    "access_token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

#### 3. Refresh Token
- **URL:** `POST /auth/refresh-token`
- **Description:** Refreshes an expired JWT using a valid refresh token (usually handled via cookies/headers).

### Blog Post Endpoints (`/posts`)

#### 1. Get All Posts
- **URL:** `GET /posts`
- **Description:** Retrieves a paginated list of blog posts.
- **Query Parameters:** `page`, `size`, `sort` (e.g., `?page=0&size=10&sort=createdAt,desc`)
- **Example Response (200 OK):**
  ```json
  {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "title": "My First Blog Post",
        "slug": "my-first-blog-post",
        "content": "Full content here...",
        "excerpt": "Short summary...",
        "authorId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
        "featuredImageUrl": "https://example.com/image.jpg",
        "status": "PUBLISHED"
      }
    ],
    "pageable": { ... },
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
  ```

#### 2. Get Post by ID
- **URL:** `GET /posts/{id}`
- **Description:** Retrieves a specific blog post by its UUID.

#### 3. Create a Post
- **URL:** `POST /posts`
- **Description:** Creates a new blog post. Requires Authentication.
- **Example Request:**
  ```json
  {
    "title": "New Post",
    "content": "Content of the post",
    "excerpt": "Summary",
    "featuredImageUrl": "https://example.com/img.png",
    "status": "PUBLISHED"
  }
  ```
- **Example Response (201 Created):**
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "title": "New Post",
    "slug": "new-post",
    "content": "Content of the post",
    "excerpt": "Summary",
    "authorId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "featuredImageUrl": "https://example.com/img.png",
    "status": "PUBLISHED"
  }
  ```

#### 4. Update a Post (Partial)
- **URL:** `PATCH /posts/{id}`
- **Description:** Partially updates an existing blog post. Requires Authentication.
- **Example Request:**
  ```json
  {
    "title": "Updated Title"
  }
  ```

#### 5. Delete a Post
- **URL:** `DELETE /posts/{id}`
- **Description:** Deletes a blog post by its UUID. Requires Authentication.

## ⚠️ Error Handling

The API returns a standardized error response for failed requests.

### Standard Error Structure
```json
{
  "timestamp": "2023-10-27 10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Specific error message here",
  "validationErrors": {
    "field_name": "error detail"
  }
}
```

### Common Error Examples

#### 1. Validation Failed (400 Bad Request)
Returned when request body constraints are violated.
```json
{
  "timestamp": "2023-10-27 10:05:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "validationErrors": {
    "title": "must not be empty",
    "status": "must not be null"
  }
}
```

#### 2. Resource Not Found (404 Not Found)
Returned when a requested resource (e.g., post ID) does not exist.
```json
{
  "timestamp": "2023-10-27 10:10:00",
  "status": 404,
  "error": "Not Found",
  "message": "Post not found with id: 550e8400-e29b-41d4-a716-446655440000"
}
```

#### 3. Unauthorized (401 Unauthorized)
Returned when authentication fails or is missing.
```json
{
  "timestamp": "2023-10-27 10:15:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

#### 4. Rate Limit Exceeded (429 Too Many Requests)
Returned when the rate limit is exceeded.
```json
{
  "timestamp": "2023-10-27 10:20:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later."
}
```
