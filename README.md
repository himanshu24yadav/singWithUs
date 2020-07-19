# About singWithUs
 This is a simple music streaming application that offers to listen to songs closest to your heart. It lets you choose from your local storage and play.
 
-  Screenshots

   <img src="https://user-images.githubusercontent.com/20729864/87876237-57c5b780-c9f4-11ea-9514-1524e597f34b.png" width="250" height="480">
   <img src="https://user-images.githubusercontent.com/20729864/87876329-2699b700-c9f5-11ea-8528-3e7fb2336b77.png" width="250" height="480"> 
   <img src="https://user-images.githubusercontent.com/20729864/87876334-2bf70180-c9f5-11ea-847e-66c974dfd159.png" width="250" height="480"> 
   <img src="https://user-images.githubusercontent.com/20729864/87876340-31544c00-c9f5-11ea-8e8c-012264e4ea64.png" width="250" height="480">
   <img src="https://user-images.githubusercontent.com/20729864/87876394-8beda800-c9f5-11ea-8ee6-5a96411f3007.png" width="250" height="480">
   
   
#  What We Used
   
   -  Kotlin coroutine for all the background tasks
   -  Foreground service for playing songs even when the application is in the background
   -  Fresco for rendering images
   -  RoomDB for caching songs data
   -  Used MVVM as an architecture component
   -  ExoPlayer Library for playing songs and keep application UI sync with the player
   -  Firebase for login feature
   
#  Requirements
   
   Android 5.0 or higher
   
#  References
   
   - https://exoplayer.dev/playlists.html (ExoPlayer Playlist feature)
   - https://medium.com/androiddevelopers/7-steps-to-room-27a5fe5f99b2 (RoomDB)
   - https://stackoverflow.com/questions/52473974/binding-playerview-with-simpleexoplayer-from-a-service (ExoPlayer with Service)
