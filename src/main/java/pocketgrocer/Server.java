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
    public static void main(String[] args) throws Exception {
        Query query = new Query();
        query.prepareStatements();
        port(8080);

        // Spark HTTP Endpoints

        // Hello World API Test
        get("/hello", (req, res) -> "Hello World");

        // stop
        get("/stop", (request, response) -> {
            query.closeConnection();
            stop();
            return "Server Stopped";
        });

        // create user
        post("/users/add", (request, response) -> {
            try {
                JSONObject user = new JSONObject(request.body());
                String username = user.getString("userName");
                String firstName = user.getString("firstName");
                String lastName = user.getString("lastName");

                if (query.userExists(username)) {
                    response.status(409);
                    return ("Username already taken");
                } else if (query.addUser(username, firstName, lastName, user.getString("password"))) {
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

        delete("/users/delete", (request, response) -> {
            try {
                JSONObject user = new JSONObject(request.body());
                String username = user.getString("userName");

                if (query.userExists(username) && query.deleteUser(username)) {
                    response.status(200);
                    return ("Success, deleted " + username);
                } else {
                    response.status(400);
                    return ("Error deleting user");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        // login user
        get("/users/login", (request, response) -> {
            try {
                JSONObject login = new JSONObject(request.body());
                String username = login.getString("userName");
                String password = login.getString("password");
                if (!query.userExists(username)) {
                    response.status(400);
                    return ("Username doesn't exist");
                } else if (query.checkLogin(username, password)) {
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

        get("/users/ingroup", (request, response) -> {
            String username = request.queryParams("userName");

            try {
                if (!query.userExists(username)) {
                    response.status(400);
                    return ("Username doesn't exist");
                } else if (query.isMemberInGroup(username)) {
                    response.status(200);
                    return ("User " + username + " is in a group");
                } else {
                    response.status(400);
                    return ("User " + username + " is not in a group");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

         post("/groups/create", (request, response) -> {
             String username = request.queryParams("userName");
             String groupname = request.queryParams("groupName");

             try {
                 if (!query.userExists(username)) {
                     response.status(400);
                     return ("Username doesn't exist");
                 } else if (query.isMemberInGroup(username)) {
                     response.status(400);
                     return ("User is already in a group");
                 } else if (query.checkGroupExists(groupname)) {
                     response.status(400);
                     return ("Group Name already exists");
                 } else if (query.addMemberToGroup(username, groupname)) {
                     response.status(200);
                     return ("User successfully added to group");
                 } else {
                     response.status(400);
                     return ("Username and password don't match");
                 }
             } catch (Exception e) {
                 response.status(400);
                 return (e);
             }
         });
    }
}