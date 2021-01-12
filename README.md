# Java-RMI-Chat-App
 A chat application using java RMI and  UDP (for video calls) 


# Description

This project is a client-server chat application using java RMI technology (we also used UDP for the video call part).

It follows an MVC pattern. In the **source directory** you will find 4 main directories:

* **assets :** where the app's icons are stored.
* **controllers :** contains relevant code for the client chat GUI interactions.
* **models :** the code for the central server and main classes.
* **views :** includes interfaces and their components.


# Features

Every user can either register or sign into his account which leads him to this main window:

![interface](/README_images/interface.png)


Each user can:

* See all connected users and contact them.
* Search for a particular user in the connected users list.
* Search for a particular group in the groups list.
* Create a group of at least 3 users.
* Send messages or files (pictures, records...).
* Make video calls.
* Change his profile picture.

# Technologies

To run this app correctly, install:

* JDK 1.8
* You also have to add the following jars (located in **Jars** folder): 
  * AnimateFX-1.2.0
  * bridj-0.7.0
  * gson-2.6.0
  * jfeonix-8.0.10
  * slf4j-api-1.7.2
  * webcam-capture-0.3.12

# Usage

Before running this app, make sure you change files paths in:

* **UpdateDB.java:** where you must specify your chat-database location (Database folder).
* **Server.java :** where you must specify a folder to store sent files and profile pictures (Files and Pictures folders).

After that, follow these steps:

1. compile java files located in ***models  folder***. To do so, open ***models  folder*** in cmd and run this line:

```
$ javac *.java

```

2. Open ***src  folder*** in cmd and run the following code to start the RMI registry:

```
$ start rmiregistry

```

3. Start your server by running **Server.java**

4. Run your first client by running **Project.java** 
