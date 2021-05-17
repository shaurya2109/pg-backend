# pg-backend
Maven based API for the Pocket Grocer application. The project has the folloing contents - 
1. src/main/java/pocketgrocer - contains important files like Server.java and Query.java - both of these files are essential to run the backend for pocket grocer.
2. pom.xml - contains information about all the dependencies required to build the project. 

## Important files - 
1. Server.java - contains the method definition of all the API endpoints.
2. Query.java - contains the Prepared statements, and the function definitions of all the methods being used in the Server.java file.

## Use cases on the backend folder - 
The backend folder of pocket grocer supports the following calls - 
1. adding a new user in the database
2. checking the login information of an existing user
3. setting up a new account of a user
4. deleting a previously created account of a user
5. adding a current user to an existing household
6. adding an item to inventory
7. deleting an item from the inventory
8. getting all the inventory items for a particular person/group

## Running the project -
The project can be started using the following steps -
1. Download the application.properties file from https://drive.google.com/file/d/1nW3plfgfajFuT449ayEtSrPEvr-kgZr8/view?usp=sharing
2. Add the application.properties file to location ```src/main/java/pocketgrocer/resources/application.properties```.
3. In the directory location of this folder type these lines in the terminal -
    1. ```mvn clean install```
    2.```java -jar ./target/pocketgrocer-1.0-jar-with-dependencies.jar```

Once the jar file is run the server.java runs and the localhost starts at port number 4567.
