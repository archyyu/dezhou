# 🔧 Troubleshooting Dependency Issues

## Common Maven Dependency Problems

### 1. Dependency Not Found in Repository

**Error:**
```
Could not resolve dependencies for project ...: Could not find artifact ... in central (https://repo.maven.apache.org/maven2)
```

**Solutions:**

#### A. Force Maven to Update Dependencies
```bash
# Clear local cache and force update
mvn dependency:purge-local-repository
mvn clean install -U
```

#### B. Check Dependency Availability
- Visit: https://search.maven.org/
- Search for the artifact (e.g., `dom4j`)
- Verify the groupId, artifactId, and version

#### C. Try Different Version
```xml
<!-- Example: Try a different version -->
<dependency>
    <groupId>org.dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>2.1.1</version>  <!-- Changed from 2.1.3 -->
</dependency>
```

### 2. Dependency Version Conflicts

**Error:**
```
Conflict: Multiple versions of the same dependency found
```

**Solutions:**

#### A. Exclude Conflicting Dependencies
```xml
<dependency>
    <groupId>some.group</groupId>
    <artifactId>some-artifact</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>conflicting.group</groupId>
            <artifactId>conflicting-artifact</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### B. Use Dependency Management
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>3.12.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3. Network/Repository Issues

**Error:**
```
Could not transfer artifact ... from/to central (https://repo.maven.apache.org/maven2): Connection timed out
```

**Solutions:**

#### A. Check Network Connection
```bash
# Test connection to Maven Central
curl -v https://repo.maven.apache.org/maven2
```

#### B. Use Mirror Repository
```xml
<settings>
    <mirrors>
        <mirror>
            <id>maven-central-mirror</id>
            <url>https://repo1.maven.org/maven2/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

#### C. Configure Proxy (if needed)
```xml
<settings>
    <proxies>
        <proxy>
            <id>my-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.example.com</host>
            <port>8080</port>
            <username>proxyuser</username>
            <password>proxypass</password>
        </proxy>
    </proxies>
</settings>
```

### 4. Corrupted Local Cache

**Error:**
```
Invalid artifact: Corrupted local cache
```

**Solutions:**

#### A. Clean Local Repository
```bash
# Remove specific artifact
rm -rf ~/.m2/repository/org/dom4j/

# Or clean entire local repo (careful!)
rm -rf ~/.m2/repository/*
```

#### B. Rebuild with Clean Cache
```bash
mvn clean install -U
```

## 🎯 Dezhou Poker Server Specific Fixes

### 1. dom4j Dependency Issue

**Problem:** `dom4j:dom4j:jar:2.1.3` not found

**Solution Applied:**
```xml
<!-- Changed from groupId="dom4j" to "org.dom4j" -->
<!-- Changed version from 2.1.3 to 2.1.1 -->
<dependency>
    <groupId>org.dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>2.1.1</version>
</dependency>
```

### 2. MySQL Connector Version

**Problem:** Missing version for MySQL connector

**Solution Applied:**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>
```

### 3. Repository Configuration

**Problem:** Dependencies not resolving from central

**Solution Applied:**
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

## 🚀 Build Commands

### Basic Build
```bash
mvn clean compile
```

### Force Update Dependencies
```bash
mvn clean install -U
```

### Offline Build (after first successful build)
```bash
mvn -o clean install
```

### Dependency Tree Analysis
```bash
mvn dependency:tree
```

### Dependency Analysis
```bash
mvn dependency:analyze
```

## 📋 Common Maven Commands

| Command | Description |
|---------|-------------|
| `mvn clean` | Remove build artifacts |
| `mvn compile` | Compile source code |
| `mvn test` | Run unit tests |
| `mvn package` | Create JAR/WAR file |
| `mvn install` | Install to local repository |
| `mvn deploy` | Deploy to remote repository |
| `mvn clean install` | Clean build and install |
| `mvn dependency:tree` | Show dependency tree |
| `mvn dependency:analyze` | Analyze dependencies |
| `mvn -U` | Force update snapshots |
| `mvn -o` | Offline mode |
| `mvn -X` | Debug mode |

## 🔍 Debugging Tips

### 1. Enable Debug Logging
```bash
mvn -X clean install
```

### 2. Check Effective POM
```bash
mvn help:effective-pom
```

### 3. List All Dependencies
```bash
mvn dependency:list
```

### 4. Check Dependency Conflicts
```bash
mvn dependency:tree -Dverbose
```

## 🎯 Next Steps

### 1. Verify Build Works
```bash
mvn clean compile
```

### 2. If Still Issues
```bash
# Try these steps:
1. mvn dependency:purge-local-repository
2. rm -rf ~/.m2/repository/org/dom4j/
3. mvn clean install -U
```

### 3. Test Application
```bash
mvn spring-boot:run
curl http://localhost:8080/health
```

## 📚 Additional Resources

- **Maven Central Search:** https://search.maven.org/
- **Maven Documentation:** https://maven.apache.org/
- **Spring Boot Maven Plugin:** https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/
- **Dependency Management:** https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html

## ✅ Success Criteria

The build is working when:
- ✅ `mvn clean compile` completes without errors
- ✅ All dependencies are resolved
- ✅ `mvn spring-boot:run` starts the application
- ✅ API endpoints respond correctly

If you're still experiencing issues, the problem might be:
1. **Network connectivity** - Check internet connection
2. **Maven configuration** - Check `~/.m2/settings.xml`
3. **Corrupted cache** - Try `mvn dependency:purge-local-repository`
4. **Java version** - Ensure Java 17+ is installed

Use `mvn -X` for detailed debugging information.