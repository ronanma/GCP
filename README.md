# GCP
## An introduction to basic GCP functionality
This is a small demo showing how to create a simple Web Service. The
service exposes a REST API call which takes in a "source" and "destination".
It then creates a message with this content, pushes it onto a pub/sub topic,
where a Cloud function is then invoked to process the message. Finally, the
Cloud function create a response message with the same content and publishes
the response on another topic.
The GCP functionality you will be using for this project covers:
- Google App Engine
- Cloud Pub/Sub
- Cloud Functions
- Google Cloud Console
- Google Cloud Command-Line

## Setup
- Install IntelliJ (CE is fine)
- Get your Git account set up locally
- Subscribe for a free Google Cloud Account
- Adding Billing account (You won’t be charged, but be careful!)
- Install GCloud SDK:
  https://cloud.google.com/sdk/docs/quickstart
- Clone this repo to your local environment
- Open the GCP folder in IntelliJ
- Make sure you can run **_gcloud init_**, and initialize your environment with default settings

### You are ready to start

## Instructions
### Part 1: The App Engine
- Run **_gcloud config list_**. A useful command to see your current config
- Let's create a new project. Run **_gcloud projects create [your-web-service-id]_**. The name 
  has to be unique 
- Switch to the new project by running **_gcloud config set project [your-web-service-id]_**
- Verify by running **_gcloud config list_**
- Change directory to **_cloud-app-engine_**
- Update the **_project.id_** property in application.properties to your new project ID
- Let's try to deploy. Run **_gcloud app deploy_**
- Choose [8] for **_europe-west_**
- When you see **_Do you want to continue (Y/n)?_** , choose 'Y'
- Oops! You will get an error. That's because Cloud Build has not been enabled for your
  project yet. Browse to URL provided and enable it for your project
- You'll be prompted for your billing account. Select it.
  (Don't worry, you have that $300 free credit!)
- When enabled, rerun **_gcloud app deploy_**
- This time, it should be successful. Take note of the URL. When you open it, 
  you should get a quote from a well-known movie! Alternatively, you can run
  **_google app browse_** to open the link

### Part 2: Setting up your topics
- Run **_gcloud pubsub topics create distance-requests_** to create the request topic for the project
- Run **_gcloud pubsub topics create distance-responses_** to create the response topic for the project

### Part 3: Creating your Cloud function
- Browse to the cloud-function-subscriber directory
- Run **_gcloud functions deploy consumer-function --entry-point consumer.ConsumeMessage --trigger-topic distance-requests --runtime java11_**
- When prompted with on whether or not you wish to enable 'Cloud Functions' for this project, select 'y' 
  //Deploy Cloud Function

### Part 4: Putting it all together
- Go to your browser and invoke the Rest API with
  https://...../calculate?source=London&destination=Edinburgh. (The parameters really don't matter!)
- Oops! You might get an error! This is because the Cloud function is referencing an environment variable that wasn't set up. 
  You could either re-deploy specifying your system vars on the command-line, OR
- You can update the runtime environment variables in the Cloud Console and re-deploy your cloud function
- Redeploy your cloud function after adding the GCP_PROJECT **run-time** variable to your project

### Part 5: How do I know it is working ?
- Go to your Cloud console and go to topics
- You can create a subscriber topic manually by selecting the response topic you created earlier.
  Note that Cloud functions create their own subscribers automatically when you deploy them
  Now, when you run the API again, you'll be able to 'Peek' at the respones using your manually
  created subscriber


