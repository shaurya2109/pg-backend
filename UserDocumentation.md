# USER MANUAL

### Project Description
Pocket Grocer is a web application that empowers individuals, as well as those living in communal living spaces, to keep
track of grocery items and respective expiration dates in order to minimize their food waste. The 3 main operational use 
cases with which this application aims to help users are:
1. Keeping track of their groceries in both the fridge and pantry
2. Creating household "groups" in which they can add other users, and see and share the items in that household
3. Filtering and sorting the items in their grocery inventory

### Team members
1. Fadel - Frontend developer
2. Libby - Frontend developer
3. Myka - Backend developer + Product Manager + User Testing
4. Sophia - Backend developer + Product Manager 
5. Shaurya - General developer
6. Tushar - General developer

### Installing software
Before the project can be started:
1. Clone this repo. IntelliJ is a recommended IDE, and it can easily be [downloaded and installed using student credentials](https://courses.cs.washington.edu/courses/cse373/19wi/resources/intellij/setup/)
2. Download the application.properties file [here](https://drive.google.com/file/d/1nW3plfgfajFuT449ayEtSrPEvr-kgZr8/view?usp=sharing)
   . Only CSE students with a valid @cse.edu email will have access to download this file.
3. Add the application.properties file to this location:
   ```src/main/java/pocketgrocer/resources/application.properties```
### How to build/run
Once the steps above are completed, follow the final step below to run the project
4. In the location of the main pg-backend folder, type these two commands in the terminal  
    - ```mvn clean install```  
     
   - ```java -jar ./target/pocketgrocer-1.0-jar-with-dependencies.jar```
### How to use the system
After the steps above are executed and the jar file runs, the server is connected and running. The public endpoint
 can be accessed at `localhost:4567` in the browser of your choice. In order to interact with the frontend part of
  the system as well, please refer to [the frontend github repo](https://github.com/libbyk000/pocket-grocer) and the
   repository's User and Developer Guidelines.
instructions as well.  
In order to make sure the connection is successful after running and building, in your localhost url,
type `/hello` after the port number and hit enter [`localhost:4567/hello`]. This will call our hello endpoint that will
 display 'hello' on your screen. 
For more information regarding the endpoints available, refer to [this spreadsheet](https://docs.google.com/spreadsheets/d/1rmU_KVO2o5DR8Hg_9x49V4n4ijTAwP74im51zqhfkEI/edit?ts=608f346d#gid=0) as well as the 'How to test the
 software' section in the [developer guidelines](DeveloperDocumentation.md).  
Once the backend is deployed, the frontend will automatically work with the API, without having the user to install
 , build, or run the backend.
### Bug Reporting
We will use [GitHub Issues](https://github.com/shaurya2109/pg-backend/issues) to keep track of bugs. If you experience an issue, please navigate to this page and add "New issue" via the green "New issue" button.
1. Write a brief description of the bug to be the report title. A good summary should succinctly describe the issue experienced.
2. Write a detailed, ordered list of steps to reproduce the issue. Screenshots or pictures are also welcome if it helps clarify the description.
3. Describe the observed (actual) result as well as the expected result. 

Thank you for filling out a report for any bug you find, this will greatly help the developing team in making pocket grocer
a more seamless experience for you!
See [Mozilla bug report writing guidelines](https://developer.mozilla.org/en-US/docs/Mozilla/QA/Bug_writing_guidelines) for more information.

### Known Bugs
See [GitHub Issues](https://github.com/shaurya2109/pg-backend/issues).

### Want to contribute to Pocket Grocer?
Check out our [developer documentation](DeveloperDocumentation.md) if you are interested in making our product better!