# DEVELOPER GUIDELINES

## Obtaining source code
If your system uses multiple repositories or submodules, provide clear instructions for how to obtain all relevant sources.
@Sophia
## Directory Layout
What do the various directories (folders) contain, and where to find source files, tests, documentation, data files, etc.
How to build the software. Provide clear instructions for how to use your project’s build system to build all system components.
@Tushar
###Important files  
@Tushar add the directory layout
1. Server.java - contains the method definition of all the API endpoints.
2. Query.java - contains the Prepared statements, and the function definitions of all the methods being used in the Server.java file.

## How to build the software


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




