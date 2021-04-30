package pocketgrocer;

// import com.google.gson.Gson;
import static spark.Spark.*;
import org.json.JSONObject;

/*
409 - conflict
400 - error
200 - success
*/

public class Server {
    public static void main(String[] args) {
        Query query = new Query();
        // port(8080);

        // Spark HTTP Endpoints¸
        get("/hello", (req, res) -> "Hello");

        get("/stop", (request, response) -> {
            stop();
            return "Server Stopped";
        });

        // create user
        post("/users/add", (request, response) -> {
            try {
                JSONObject user = new JSONObject(request.body());
                String username = user.getString("Username");
                String firstName = user.getString("FirstName");
                String lastName = user.getString("LastName");

                if (query.userExists(username)) {
                    response.status(409);
                    return ("Username already taken");
                } else if (query.addUser(username, firstName, lastName, user.getString("Password"))) {
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