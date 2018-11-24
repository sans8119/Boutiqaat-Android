# Boutiqaat-Android
## Case-study: An app which downloads images from net, shows location updates, sets up a profile information , maintains multiple Sign-ins

### FEATURE OVERVIEW

1. As soon as the application is lauched *‘Categories’* screen is shown. Bottom navigation tabs are also shown. The ‘Categories ’ screen shows a 20 images downloaded from internet; more specifically from Instagram. User can toggle between list and grid representations for viewing these 20 images. 
2. When user selects ‘My Locations’ tab a list of locations is shown. Each location is at a distance of 500 meters from the previous one. As soon as the user moves 500 m away in radius from his current location his new location is automatically updated. He can also get a instantaneous update of his current position by clicking ‘Trigger Location Update’ button on this screen. A signed-in users location information is also stored in database. This enables retracing his historical information when he signs-out and signs-in again.
When user selects ‘Profile’ tab Profile screen is shown. Here he can update/change name,email, address,photo and phone information. If he is logged in then this information will overwrite the information he provided during signup. By tapping on the Image on the top user gets an option to select image from gallery or camera. When user clicks on ‘Update’ button his information will be permanently stored if he is a logged-in user; else if he is a anonymous user then the information will be temporarily stored until application is in foreground or background and not yet reclaimed by android platform. 
Selecting ‘Settings’ tab opens Settings screen. Here the user can change location updates by toggling ‘Save location info’ radio button to on or off.  He can change language to Arabic, English or French. If he is signed-in then a ‘Sign Out’ button will be shown. Clicking on this button will sign him out and remove all information from Profile page. At this moment he will be termed as a ‘Anonymous’ user. Now Sign out button will be replaced by Sing-in button and a link for opening Sign-up page. 
The application can handle multiple sign-ins. When ever a user signs-in with a different email and password, the corresponding information provided during Sign-up or updated in ‘Profile’ screen will be shown.User can sign-up using sign-up page and update the information provided during sign-up in Profile page. In his profile screen he can add more information like image from camera or gallery, his address and gender. When ever a user signs-in the his respective information is pulled out from db and shown in Profile screen. An anonymous user is the one who has not signed in. He can save his information in Profile page. This information will be maintained until the application is in foreground or background.The info will not be persisted for ever.

### DESIGN AND ARCHITECTURE GIST:

MVVM is predominently used for architecture. Some of the latest architectural components introduces by Android platform like - Room, LiveData etc are used in sync with android data binding. Usage of RxAndroid, LiveData and Data binding makes for a reactive architecture. This is done in order to reduce response times for seeing a screen. Dagger is used for dependency injection and maintaining of various objects.

### EXTERNAL LIBRARIES used:

1)	Retrofit : https://square.github.io/retrofit/ 
2)	Dagger 2.0 : https://google.github.io/dagger/ 
3)	RxJava and RxAndroid : https://github.com/ReactiveX/RxJava and https://github.com/ReactiveX/RxAndroid 
4)	Timber: https://github.com/JakeWharton/timber
5)	Gson: https://github.com/google/gson 
6)	Glide: https://github.com/bumptech/glide 
7)	PermissionDispatcher: https://github.com/permissions-dispatcher/PermissionsDispatcher 

