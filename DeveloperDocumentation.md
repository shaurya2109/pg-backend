# DEVELOPER GUIDELINES

## Obtaining source code

Thanks for your interest in our project! PocketGrocer is split up into 2 main github repositories. This repository
is responsible for all the backend code and testing infrastructure. Refer to [the frontend github repo](https://github.com/libbyk000/pocket-grocer)
to learn how to build and install the frontend web application. Both repositories will need to be cloned, built, and run in order to get started with the application.

## Directory Layout

- .github/workflows
  - mavern.yml - build and deploy the script on Github
- .idea - all project-specific settings for the project are stored in this folder. This folder is not responsible for the working of the backend.
- src/main/java/pocketgrocer
  - resources/application.properties - the following file needs to be added by the user from the Google Drive link provided. The file contains the link of the server, the password and other important information needed to connect to Azure SQL database.
  - Query.java - contains the Prepared statements, and the function definitions of all the methods being used in the Server.java file.
  - Server.java - contains the method definition of all the API endpoints.
  - QueryTest.java - contains the Prepared statements, and the function definitions of all the methods being used to test the working of the backend in TestQuery.java
  - TestQuery.java - tests all the functions defined in the QueryTest file. Independently testing all the functions makes it easier to find errors and inconsistencies.

## How to build the software

Please refer to "Installing software" and "How to build/run" sections in the [user documentation](UserDocumentation.md)

## How to test the software

Testing the methods using Junit testing:
Once you clone the file and set it up on IntelliJ, the Junit testing can be done by running the TestQuery.java file. This file tests all the methods individually. This helps in testing the backend from the ground up. No other file is needed to be running for the Junit testing.

Testing the HTTP Endpoints:
Once the server is running using the "How to build/run" sections in the [user documentation](UserDocumentation.md), we can test the HTTP endpoints using a browser [limitations apply] or using a service like Postman.  
Download and install the Postman Desktop Agent from [here](https://www.postman.com/downloads/) (to overcome the Cross
Object Resource Sharing (CORS) limitations of browsers) if you want to use Postman's web interface. You can even
directly use Postman's desktop application.  
Follow the steps in the Postman [tutorial](https://learning.postman.com/docs/getting-started/sending-the-first
-request/) to make a GET request to the hello endpoint, `localhost:4567/hello`.  
For more information regarding the endpoints available, refer to [this spreadsheet](https://docs.google.com/spreadsheets/d/1rmU_KVO2o5DR8Hg_9x49V4n4ijTAwP74im51zqhfkEI/edit?ts=608f346d#gid=0) and remember to be careful while
making requests, since you could potentially do unintended CRUD operations to the database.
Look at this image to see an example of a POST request made to add a user to the server.  
[Postman Add User](/images/postman.png)

## Adding new tests

Adding more tests to the backend of pocket-grocer is very straightforward. More tests can be added in the form of new methods in the TestQuery.java file. This files uses functions defined in the QueryTest.java file.

## Build/Release
The `pom.xml` is the heart of this maven repository, storing crucial information about the project and its built.  
We are currently on version 1.0 of the project, and `pom.xml` is used to maintain the versioning of the project
. Major changes after launching the application can solicit new version numbers, which should be updated here and in
 the `pom.xml` file.  
Every commit to the repository goes through a CI pipeline which tests the API by successfully building and running the
 server. Beyond this, you can manually do a maven clean install to build, run, and test the server using the
  instructions above.
## Coding conventions

In order to use consistent coding standards, please follow [this Java style guide](https://google.github.io/styleguide/javaguide.html), [this SQL style guide](https://about.gitlab.com/handbook/business-technology/data-team/platform/sql-style-guide/).
This will help keep our code clean and readable!
