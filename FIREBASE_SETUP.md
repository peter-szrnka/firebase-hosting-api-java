# Setting up a Firebase project with Firebase Hosting


This file contains a detailed guide how to create and configure a Firebase project with Hosting to use the **firebase-hosting-api-java** client library.

## Create a Firebase project in Firebase Console
Open https://console.firebase.google.com/u/0/ and press on "Add project":

![firebase_step1.png](assets/firebase_step1.png)

Define a name for the project:

![firebase_step2.png](assets/firebase_step2.png)

**REALLY IMPORTANT: If you define a non-unique name, then the project name and project id will be different**

Hint: You'll use the project ID as "siteId" in the library configuration:

![assets/firebase_step8.png](assets/firebase_step8.png)

Disable Analytics if you don't need it, then press "Create project":

![firebase_step3.png](assets/firebase_step3.png)

![firebase_step4.png](assets/firebase_step4.png)

Click on Project Settings:

![firebase_step5.png](assets/firebase_step5.png)

Click on "Service accounts" panel then "Generate new private key":

![firebase_step6.png](assets/firebase_step6.png)

Download the file and save it as "service-account.json" and save into "src/main/resources" folder.

Run the application, and you have to see many HTTP 200's. 

![log_example.png](assets/log_example.png)

Then check your project in Firebase console:

![firebase_step7.png](assets/firebase_step7.png)

**IMPORTANT:**
If you see non HTTP 200's, you might have some issues with your Google Cloud/Firebase account.