# polygonal-geofences-android
The functionality of maps and spatial-temoral data is used in a lot of applications today. As a part of one of my undergraduate projects, I was required to make an Android Application that logs the users location coordinates every 5 minutes. We then wanted to use this data to determine relationship coefficients between sets of people. I decided to extend the application and enable adding geofences onto the Map. A geofence acts as a region of interest and can be used to study people's behaviour patterns. Also, Google Maps Android SDK doesnt provide an option to draw Polygonal geofences.

To run the Application on Android 6 and above, you will need to enable the permissions from the Settings -> Apps -> Polygonal Geofences -> Permissions -> Enable Location and Storage.

## The Application is divided into 4 parts.
1. Adding a Geofence.
2. Viewing all Geofences.
3. Starting Location Updates.
4. Stopping Location Updates.

## Adding a Geofence
The following steps need to be followed to add a geofence-
a. Select more than three points on the map to make a Polygon. To select a point one needs to do the long press action on the map fragment.
b. Click on Add a name to add a name for the geofence.
c. Click on Add Image to add a tile image to be displayed with the geofence.
d. Click on Add geofence to see the added geofence.
e. Repeat to add more geofences.

<p align="center">
  <img src="https://github.com/vrajasu/polygonal-geofences-android/blob/master/screenshots/create_0.png" width="250"/>
</p>

<p align="center">
  <img src="https://github.com/vrajasu/polygonal-geofences-android/blob/master/screenshots/create_one.png" width="250"/>
</p>


The geofences are locally stored on the device in a file.

## Viewing all Geofences
You can view all the geofences that you have added by simply clicking on View All Geofences in the Main Activity.

<p align="center">
  <img src="https://github.com/vrajasu/polygonal-geofences-android/blob/master/screenshots/view_all.png" width="250"/>
</p>

## Starting location updates
To keep a track of the geofences you have visited, you can click on the Start Updates button. This starts off a background process that captures the location every 10 minutes and finds the geofence you are in currently(if any). The update is shown via a notification.

<p align="center">
  <img src="https://github.com/vrajasu/polygonal-geofences-android/blob/master/screenshots/notification.png" width="250"/>
</p>

## Stopping the updates
To stop the updates click on the Stop updates button and clear the notification.

## Applications
The concept of geofencing has a lot of interesting applications. One of the foremost applications would be that of advertising, where business owners can attract customers depending on their proximity. Also, apps where people are looking to meet new people at a geofence'd location.

## Future Work
I would like to add the option to delete and update exisiting geofences. I will also like to add a consistency check to ensure that edges of the polygon do not intersect.
