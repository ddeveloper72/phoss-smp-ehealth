# Heroku Docker Migration Steps

## 1. Login to Heroku (if not already logged in)

```bash
heroku login
```

## 2. Find your app name

```bash
heroku apps
```

## 3. Switch to Container Stack

```bash
# Replace 'your-app-name' with your actual app name
heroku stack:set container -a your-app-name
```

## 4. Remove Java Buildpack (no longer needed)

```bash
# Check current buildpacks
heroku buildpacks -a your-app-name

# Clear all buildpacks (containers don't use buildpacks)
heroku buildpacks:clear -a your-app-name
```

## 5. Update Config Variables

You'll need to update some config vars since Docker uses different environment variables:

```bash
# Keep these MongoDB settings
heroku config:set MONGODB_URI="mongodb+srv://ehealth:knOJ9FlPjSoDg260@task-manager.8i0tx.mongodb.net/task-manager?retryWrites=true&w=majority" -a your-app-name
heroku config:set DB_NAME="task-manager" -a your-app-name

# Keep these SMP settings  
heroku config:set SMP_IDENTIFIERTYPE="bdxr" -a your-app-name
heroku config:set SMP_REST_TYPE="bdxr" -a your-app-name
heroku config:set SMP_PUBLIC_URL="https://your-app-name.herokuapp.com" -a your-app-name

# Remove old Maven-specific variables (no longer needed)
heroku config:unset MAVEN_OPTS -a your-app-name
heroku config:unset JAVA_RUNTIME_VERSION -a your-app-name

# Set Docker-optimized JVM settings
heroku config:set JAVA_OPTS="-Xmx512m -Dfile.encoding=UTF-8" -a your-app-name
```

## 6. Remove system.properties (no longer needed for Docker)

The system.properties file is only used by Java buildpacks, not Docker containers.

## 7. Deploy with Docker

```bash
git push heroku master
```

## 8. Monitor the deployment

```bash
heroku logs --tail -a your-app-name
```

## 9. Verify the app is running

```bash
heroku ps -a your-app-name
heroku open -a your-app-name
```

## What Changes

- ✅ **Buildpack → Docker**: Heroku builds using Dockerfile instead of Java buildpack
- ✅ **Maven Runtime → Container**: All build happens in Docker, not on Heroku dyno
- ✅ **system.properties**: No longer needed (Docker specifies Java version)
- ✅ **Procfile**: Still used but now runs the container command
- ✅ **Config vars**: Most stay the same, just remove Maven-specific ones
