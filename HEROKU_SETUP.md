# Heroku Deployment Setup for phoss SMP

## Required Buildpacks

Your application needs the **Java buildpack**:

```bash
heroku buildpacks:set heroku/java -a your-app-name
```

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

### 3. Java/Maven Configuration
```bash
# JVM settings for Heroku
heroku config:set MAVEN_OPTS="-Xmx1024m" -a your-app-name
heroku config:set JAVA_OPTS="-Dfile.encoding=UTF-8" -a your-app-name
```

### 4. Certificate Configuration (Optional)
For production deployment with certificates, you'll need to set:
```bash
heroku config:set SMP_KEYSTORE_TYPE="PKCS12" -a your-app-name
heroku config:set SMP_KEYSTORE_PATH="path/to/keystore" -a your-app-name
heroku config:set SMP_KEYSTORE_PASSWORD="your-keystore-password" -a your-app-name
heroku config:set SMP_KEYSTORE_KEY_ALIAS="your-key-alias" -a your-app-name
heroku config:set SMP_KEYSTORE_KEY_PASSWORD="your-key-password" -a your-app-name
```

## Files Added for Heroku

1. **Procfile** - Tells Heroku how to run your app
2. **system.properties** - Specifies Java 11 runtime
3. **application-heroku.properties** - Heroku-specific configuration using environment variables

## Deployment Steps

1. **Create Heroku app:**
   ```bash
   heroku create your-app-name
   ```

2. **Set buildpack and config vars** (use commands above)

3. **Deploy:**
   ```bash
   git push heroku master
   ```

4. **Monitor logs:**
   ```bash
   heroku logs --tail -a your-app-name
   ```

## Important Notes

- Replace `your-app-name` with your actual Heroku app name
- Replace `YOUR_PASSWORD` with your actual MongoDB password
- Certificates need special handling in Heroku - consider using external certificate management or encoding them in environment variables
- The application will be available at `https://your-app-name.herokuapp.com`
