# Heroku Deployment Setup for phoss SMP (Docker)

## Docker Deployment (Recommended)

Your application is now configured for Docker deployment on Heroku, which is more reliable than JAR deployment.

### Required Files Created:
- **`Dockerfile`** - Multi-stage build for production-ready container
- **`heroku.yml`** - Heroku container deployment configuration  
- **`.dockerignore`** - Optimizes Docker build by excluding unnecessary files

## Essential Config Variables

### 1. Database Configuration
```bash
# MongoDB Atlas connection (replace YOUR_PASSWORD with actual password)
heroku config:set MONGODB_URI="mongodb+srv://ehealth:YOUR_PASSWORD@task-manager.8i0tx.mongodb.net/task-manager?retryWrites=true&w=majority" -a your-app-name
heroku config:set DB_NAME="task-manager" -a your-app-name
```

### 2. SMP Configuration
```bash
# BDXR configuration for eHealth compliance
heroku config:set SMP_IDENTIFIERTYPE="bdxr" -a your-app-name
heroku config:set SMP_REST_TYPE="bdxr" -a your-app-name
heroku config:set SMP_PUBLIC_URL="https://your-app-name.herokuapp.com" -a your-app-name
```

### 3. Java/Container Configuration
```bash
# JVM settings optimized for containers
heroku config:set JAVA_OPTS="-Xmx512m -Dfile.encoding=UTF-8" -a your-app-name
```

## Docker Deployment Steps

### 1. Set Stack to Container
```bash
heroku stack:set container -a your-app-name
```

### 2. Set Config Variables
```bash
# Set your MongoDB connection string
heroku config:set MONGODB_URI="your-mongodb-connection-string" -a your-app-name
heroku config:set DB_NAME="task-manager" -a your-app-name
heroku config:set SMP_IDENTIFIERTYPE="bdxr" -a your-app-name
heroku config:set SMP_REST_TYPE="bdxr" -a your-app-name
heroku config:set SMP_PUBLIC_URL="https://your-app-name.herokuapp.com" -a your-app-name
```

### 3. Deploy via Git
```bash
git push heroku master
```

### 4. Monitor Deployment
```bash
# Watch the container build process
heroku logs --tail -a your-app-name

# Check app status
heroku ps -a your-app-name
```

## Docker Benefits

✅ **Reliable Builds**: Maven build happens inside container, not affected by Heroku runtime limitations  
✅ **Consistent Environment**: Same container runs locally and on Heroku  
✅ **Better Performance**: Optimized JVM settings for containers  
✅ **Easy Local Testing**: Test the exact same container locally with `docker build`  

## Local Testing

```bash
# Build the container locally
docker build -t phoss-smp-ehealth .

# Run locally (requires your MongoDB connection)
docker run -p 8080:8080 -e PORT=8080 -e MONGODB_URI="your-connection-string" phoss-smp-ehealth
```

## Troubleshooting

- **Build Failures**: Check `heroku logs --tail` for build output
- **Container Won't Start**: Verify config vars are set correctly
- **Port Issues**: Heroku automatically sets `$PORT` environment variable

## Important Notes

- Replace `your-app-name` with your actual Heroku app name
- Replace connection strings with your actual MongoDB credentials  
- The container uses Java 11 JRE Alpine for optimal size and performance
- Health checks are built-in for container monitoring
