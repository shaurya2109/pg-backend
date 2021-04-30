package pocketgrocer;

import com.google.gson.Gson;
import static spark.Spark.*;

/*
409 - conflict
400 - error
200 - success
*/

public class Server {
    public static void main(String[] args) {
        // Java serialization/deserialization library to convert Java Objects into JSON and back
        // https://github.com/google/gson
        Gson gson = new Gson();
        Query query = new Query();

        // Spark HTTP Endpoints¸
        get("/hello", (req, res) -> "Hello World");

        // create user
        post("/users/add", (request, response) -> {
            String username = request.queryParams("Username");
            String firstName = request.queryParams("FirstName");
            String lastName = request.queryParams("LastName");
            String password = request.queryParams("Password");

            try {
                if (query.userExists(username)) {
                    response.status(409);
                    return ("Username already taken");
                } else if (query.addUser(username, firstName, lastName, password)) {
                    response.status(200);
                    return ("Success, welcome " + username);
                } else {
                    response.status(400);
                    return ("Error creating user");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        // login user
        get("/users/login", (request, response) -> {
            String username = request.queryParams("Username");
            String password = request.queryParams("Password");

            try {
                if (!query.userExists(username)) {
                    response.status(400);
                    return ("Username doesn't exist");
                } else if (true) { //query.checkLogin(username, password)
                    response.status(200);
                    return ("Success");
                } else {
                    response.status(400);
                    return ("Username and password don't match");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        // post("/groups/create", (request, response) -> {
        //     String username = request.queryParams("Username");

        //     try {
        //         if (!query.userExists(username)) {
        //             response.status(400);
        //             return ("Username doesn't exist");
        //         } else if (query.inGroup(username)) {
        //             response.status(400);
        //             return ("You're already in a group");
        //         } else if (query.create(username, password)) {
        //             response.status(200);
        //             return ("Success");
        //         } else {
        //             response.status(400);
        //             return ("Username and password don't match");
        //         }
        //     } catch (Exception e) {
        //         response.status(400);
        //         return (e);
        //     }
        // });
    }
}