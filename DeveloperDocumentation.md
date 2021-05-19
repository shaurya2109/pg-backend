# DEVELOPER GUIDELINES

## Obtaining source code
Thanks for your interest in our project! PocketGrocer is split up into 2 main github repositories. This repository
is responsible for all the backend code and testing infrastructure. Refer to [the frontend github repo](https://github.com/libbyk000/pocket-grocer)
to learn how to build and install the frontend web application. Both repositories will need to be cloned, built, and run in order to get started with the application.

## Directory Layout
- .github/workflows
    * mavern.yml - build and deploy the script on Github
- .idea - all project-specific settings for the project are stored in this folder. This folder is not responsible for the working of the backend. 
- src/main/java/pocketgrocer
    * resources/application.properties - the following file needs to be added by the user from the Google Drive link provided. The file contains the link of the server, the password and other important information needed to connect to Azure SQL database. 
    * Query.java - contains the Prepared statements, and the function definitions of all the methods being used in the Server.java file.
    * Server.java - contains the method definition of all the API endpoints.
    * QueryTest.java - contains the Prepared statements, and the function definitions of all the methods being used to test the working of the backend in TestQuery.java 
    * TestQuery.java - tests all the functions defined in the QueryTest file. Independently testing all the functions makes it easier to find errors and inconsistencies. 
    
###Important files  
@Tushar add the directory layout
1. Server.java - contains the method definition of all the API endpoints.
2. Query.java - contains the Prepared statements, and the function definitions of all the methods being used in the Server.java file.

## How to build the software
Please refer to "Installing software" and "How to build/run" sections in the [user documentation](UserDocumentation.md)

## How to test the software
Provide clear instructions for how to run the system’s test cases. In some cases, the instructions may need to include 
information such as how to access data sources or how to interact with external systems. You may reference the user documentation 
(e.g., prerequisites) to avoid duplication.  
@tushar JUnit testing
@shaurya this is a good place to walk through how to test via postman
@Shaurya, adding the info about the CI build 

## Adding new tests
Are there any naming conventions/patterns to follow when naming test files? Is there a particular test harness to use?
@Myka

## Build/Release
Describe any tasks that are not automated. For example, should a developer update a version number (in code and documentation) 
prior to invoking the build system? Are there any sanity checks a developer should perform after building a release?  
@Shaurya 

## Coding conventions
In order to use consistent coding standards, please follow [this Java style guide](https://google.github.io/styleguide/javaguide.html), [this SQL style guide](https://about.gitlab.com/handbook/business-technology/data-team/platform/sql-style-guide/). 
This will help keep our code clean and readable!