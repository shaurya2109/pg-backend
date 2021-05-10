package pocketgrocer;

// import com.google.gson.Gson;
import static spark.Spark.*;
import org.json.JSONObject;
import spark.Filter;

import java.util.Date;

/*
409 - conflict
400 - error
200 - success
*/

public class Server {
    public static void main(String[] args) throws Exception {
        Query query = new Query();
        query.prepareStatements();
        // port(8080);

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        // Spark HTTP Endpoints

        // Hello World API Test
        get("/hello", (req, res) -> "Hello World");

        // stop
        get("/stop", (request, response) -> {
            query.closeConnection();
            stop();
            return "Server Stopped";
        });

        // check if user exists
        get("/users/exists", (request, response) -> {
            try {
                JSONObject user = new JSONObject(request.body());
                String username = user.getString("userName");

                if (!query.userExists(username)) {
                    response.status(200);
                    return ("Username available");
                } else {
                    response.status(409);
                    return ("Username already taken");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
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
                    response.status(409);
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
            JSONObject user = new JSONObject(request.body());
            String username = user.getString("userName");

            try {
                if (!query.userExists(username)) {
                    response.status(409);
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

        get("/groups/exists", (request, response) -> {
            JSONObject group = new JSONObject(request.body());
            String groupname = group.getString("groupName");

            try {
                if (!query.checkGroupExists(groupname)) {
                    response.status(200);
                    return (groupname + " is available");

                } else {
                    response.status(400);
                    return ("Group Name " + groupname + " is taken");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        post("/groups/removeuser", (request, response) -> {
            JSONObject create = new JSONObject(request.body());
            String username = create.getString("userName");

            try {
                if (!query.userExists(username)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else if (!query.isMemberInGroup(username)) {
                    response.status(400);
                    return ("User isn't in a group");
                } else if (query.updateGroupName(username, "")) {
                    response.status(200);
                    return ("User successfully removed from group");
                } else {
                    response.status(400);
                    return ("Failed removing user from group");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        post("/groups/add", (request, response) -> {
            JSONObject create = new JSONObject(request.body());
            String username = create.getString("userName");
            String groupname = create.getString("groupName");

            try {
                if (!query.userExists(username)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else if (query.isMemberInGroup(username)) {
                    response.status(428);
                    return ("User is already in a group");
                } else if (!query.checkGroupExists(groupname)) {
                    response.status(412);
                    return ("Group Name doesn't exist");
                } else if (query.updateGroupName(username, groupname)) {
                    response.status(200);
                    return ("User successfully added to group");
                } else {
                    response.status(400);
                    return ("Failed adding user to group");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

         post("/groups/create", (request, response) -> {
             JSONObject create = new JSONObject(request.body());
             String username = create.getString("userName");
             String groupname = create.getString("groupName");

             try {
                 if (!query.userExists(username)) {
                     response.status(409);
                     return ("Username doesn't exist");
                 } else if (query.isMemberInGroup(username)) {
                     response.status(428);
                     return ("User is already in a group");
                 } else if (query.checkGroupExists(groupname)) {
                     response.status(412);
                     return ("Group Name already exists");
                 } else if (query.updateGroupName(username, groupname)) {
                     response.status(200);
                     return ("Successfully created group and added the user");
                 } else {
                     response.status(400);
                     return ("Failed creating group");
                 }
             } catch (Exception e) {
                 response.status(400);
                 return (e);
             }
         });

        post("/items/add", (request, response) -> {
            JSONObject items = new JSONObject(request.body());
            String itemName = items.getString("itemName");
            String userName = items.getString("userName");
            Integer shared = items.getInt("shared");
            String category = items.getString("category");
            Integer storage = items.getInt("storage");
            //I couldn't get a date object from the Json parameter, so for now it will be a string YYYY-MM-DD
            //and in Query.java it gets converted to a sql date
            String expiration = items.getString("expiration");
            Integer quantity = items.getInt("quantity");

            try {
                if (!query.userExists(userName)) {
                    response.status(409);
                    return ("Username doesn't exist");
                }  else if (query.addItem(itemName, userName, shared, category, storage, expiration,quantity )) {
                    response.status(200);
                    return ("Successfully added item(s) to the inventory");
                } else {
                    response.status(400);
                    return ("Failed adding item(s)");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        get("/items/get", (request, response) -> {
            JSONObject user = new JSONObject(request.body());
            String userName = user.getString("userName");

            try {
                if (!query.userExists(userName)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else {
                    JSONObject itemsList = query.get_user_items(userName);
                    response.status(200);
                    return itemsList;
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        post("/items/delete", (request, response) -> {
            JSONObject item = new JSONObject(request.body());
            Integer itemID = item.getInt("itemID");

            try {
                if (!query.checkItem(itemID)) {
                    response.status(409);
                    return ("Item doesn't exist");
                } else if (query.delete_item(itemID)) {
                    response.status(200);
                    return ("Successfully deleted item");
                } else {
                    response.status(400);
                    return ("Failed deleting item");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });
    }
}