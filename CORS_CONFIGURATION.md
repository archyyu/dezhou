# CORS Configuration in Texas Holder Spring Boot Application

## Overview

The Texas Holder Spring Boot application now has comprehensive CORS (Cross-Origin Resource Sharing) configuration implemented using both `CorsFilter` and `WebMvcConfigurer` approaches.

## Configuration Details

### 1. WebConfig.java - Dual CORS Configuration

The `WebConfig` class now implements `WebMvcConfigurer` and provides two layers of CORS configuration:

#### A. CorsFilter Bean Configuration
```java
@Bean
public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    // Configure allowed origins
    config.setAllowedOrigins(List.of(
        "http://localhost:5173",  // Vue development server
        "http://localhost:5174",  // Alternative Vue port
        "http://localhost:3000",  // Common alternative
        "http://localhost:8080"   // Same origin
    ));

    // Configure allowed methods
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Configure allowed headers
    config.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "Accept",
        "X-Requested-With"
    ));

    // Allow credentials (for JWT authentication)
    config.setAllowCredentials(true);

    // Set max age for preflight requests
    config.setMaxAge(3600L); // 1 hour

    // Apply to all endpoints
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
}
```

#### B. WebMvcConfigurer Implementation
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(
            "http://localhost:5173",
            "http://localhost:5174", 
            "http://localhost:3000",
            "http://localhost:8080",
            "http://127.0.0.1:5173"
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
}
```

### 2. SecurityConfig.java - Security Layer CORS

The `SecurityConfig` class also includes CORS configuration for the security layer:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(
        "http://localhost:5173",  // Vue development server
        "http://localhost:5174",  // Alternative Vue port
        "http://localhost:3000",  // Common alternative
        "http://localhost:8080",  // Same origin
        "http://localhost:5173",  // Vue development server (duplicate for safety)
        "http://127.0.0.1:5173"   // Alternative localhost format
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## How CORS Works in This Application

### Allowed Origins
The application allows requests from:
- `http://localhost:5173` - Vue.js development server (default Vite port)
- `http://localhost:5174` - Alternative Vue.js port
- `http://localhost:3000` - Common development port (React, etc.)
- `http://localhost:8080` - Same origin (Spring Boot default port)
- `http://127.0.0.1:5173` - Alternative localhost format

### HTTP Methods
All standard HTTP methods are allowed:
- `GET` - Retrieve resources
- `POST` - Create resources
- `PUT` - Update resources
- `DELETE` - Delete resources
- `OPTIONS` - CORS preflight requests

### HTTP Headers
The application allows:
- Specific headers: `Authorization`, `Content-Type`, `Accept`, `X-Requested-With`
- All headers (`*`) in the WebMvcConfigurer implementation

### Credentials
- `AllowCredentials(true)` - Enables sending cookies and authentication headers
- Essential for JWT token-based authentication

### Preflight Caching
- `MaxAge(3600)` - Caches preflight responses for 1 hour
- Reduces the number of OPTIONS requests

## Testing

The configuration includes comprehensive unit tests in `WebConfigTest.java`:

```java
@Test
void testWebConfigImplementsWebMvcConfigurer() {
    assertTrue(webConfig instanceof WebMvcConfigurer, "WebConfig should implement WebMvcConfigurer");
}

@Test
void testCorsFilterBean() {
    CorsFilter corsFilter = webConfig.corsFilter();
    assertNotNull(corsFilter, "CORS filter bean should not be null");
}

@Test
void testCorsMappingsConfiguration() {
    // Tests the WebMvcConfigurer implementation
}

@Test
void testCorsConfigurationDetails() {
    // Tests specific CORS configuration details
}
```

## How to Add More Origins

To add additional allowed origins (e.g., for production deployment):

1. **In WebConfig.java**:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://your-production-domain.com",
    "https://api.your-domain.com"
));
```

2. **In SecurityConfig.java**:
```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://your-production-domain.com",
    "https://*.your-domain.com"  // Wildcard for subdomains
));
```

## Troubleshooting CORS Issues

If you encounter CORS issues:

1. **Check browser console** for specific CORS error messages
2. **Verify the origin** is in the allowed origins list
3. **Ensure credentials** are enabled if using JWT authentication
4. **Check headers** - make sure all required headers are allowed
5. **Test with Postman/curl** to isolate frontend vs backend issues

## Example CORS Response Headers

When a request is made from an allowed origin, the Spring Boot application will include these headers in the response:

```
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

This configuration ensures that your Vue.js frontend can communicate seamlessly with the Spring Boot backend during development and production.