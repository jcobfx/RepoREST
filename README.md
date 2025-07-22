# RepoInfo

RepoInfo is a RESTful API for retrieving GitHub repository information. It provides a simplified interface to fetch non-forked repositories and their branches for a specified GitHub user.

## Overview

This Spring Boot application serves as a wrapper around GitHub's API, offering the following features:

- Fetch all non-forked repositories for a GitHub user
- Include branch information for each repository
- Provide a simplified response format with essential information
- Handle various error cases with appropriate HTTP status codes and messages

## Installation

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Steps

1. Clone the repository:
   ```
   git clone https://github.com/jcobfx/RepoInfo.git
   cd RepoInfo
   ```

2. Build the application:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start on port 8080 by default.

## API Documentation

### Endpoints

#### Get Repositories

Retrieves all non-forked repositories for a GitHub user, including branch information for each repository.

```
GET /repos?username={username}
```

**Parameters:**

- `username` (required): The GitHub username to fetch repositories for

**Response:**

- Status: 200 OK
- Content-Type: application/json
- Body: Array of repository objects

**Example Response:**

```json
[
  {
    "name": "repo-name",
    "ownerLogin": "github-username",
    "branches": [
      {
        "name": "main",
        "commitSha": "f7d286aa6381bbb5045288496403d9427b0746e2"
      },
      {
        "name": "develop",
        "commitSha": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0"
      }
    ]
  }
]
```

## Error Handling

The API handles various error cases with appropriate HTTP status codes and messages:

### User Not Found

If the specified GitHub user does not exist:

```
Status: 404 Not Found
```

```json
{
  "status": 404,
  "message": "User not found: username"
}
```

### Repositories Not Found

If repositories for the user cannot be fetched:

```
Status: 404 Not Found
```

```json
{
  "status": 404,
  "message": "Failed to fetch repositories."
}
```

### Branches Not Found

If branches for a repository cannot be fetched:

```
Status: 404 Not Found
```

```json
{
  "status": 404,
  "message": "Failed to fetch branches for the repository: repo-name"
}
```

## Usage Examples

### Using cURL

```bash
curl -X GET "http://localhost:8080/repos?username=octocat"
```

### Using JavaScript (Fetch API)

```javascript
fetch('http://localhost:8080/repos?username=octocat')
  .then(response => {
    if (!response.ok) {
      return response.json().then(error => Promise.reject(error));
    }
    return response.json();
  })
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

## Dependencies

- Spring Boot 3.5.3
- Spring Web
- Spring RestClient

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgements

- [GitHub API](https://docs.github.com/en/rest) for providing the repository data