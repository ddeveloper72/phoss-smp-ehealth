# Heroku Docker Deployment Commands

## Step 1: Set Heroku to use container stack
heroku stack:set container -a your-app-name

## Step 2: Set your essential config variables
heroku config:set MONGODB_URI="mongodb+srv://ehealth:knOJ9FlPjSoDg260@task-manager.8i0tx.mongodb.net/task-manager?retryWrites=true&w=majority" -a your-app-name
heroku config:set DB_NAME="task-manager" -a your-app-name
heroku config:set SMP_IDENTIFIERTYPE="bdxr" -a your-app-name
heroku config:set SMP_REST_TYPE="bdxr" -a your-app-name
heroku config:set SMP_PUBLIC_URL="https://your-app-name.herokuapp.com" -a your-app-name

## Step 3: Deploy via git (Heroku will build the Docker container)
git push heroku master

## Step 4: Monitor the deployment
heroku logs --tail -a your-app-name

## Useful commands:
# Check if your app is running
heroku ps -a your-app-name

# Scale your app (if needed)
heroku ps:scale web=1 -a your-app-name

# Open your app in browser
heroku open -a your-app-name
