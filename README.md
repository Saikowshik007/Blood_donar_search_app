# Blood_donar_search_app
BLOOD BANK

Introduction:
	 
This is an android application which uses user current location and finds all the blood donors in his locality. This app uses a firebase collections firestore and location functionality of the mobile.

Components used:

1.	Intents
2.	Firebase (Google)
3.	Location system service
4.	Recycler View

  About:

The main idea is to help people who requires blood in panic situations without doing unnecessary steps.
First the user will register in the app and then he will find people who are donors in his locality based on their blood groups. The data of the app is planned to store in firebase and retrieved from it during starting of the app.

A location service is provided which checks periodically current location of the user to suggest the donors. Threads are used to fetch location and data to omit the freeze of UI. Validations are performed using regex. A recycler view Is used to show the fetched data from database in a list view format. The screenshots of the app are provided with the images attachment folder.


Github: https://github.com/Saikowshik007/Blood_donar_search_app


Firebase: https://console.firebase.google.com/

