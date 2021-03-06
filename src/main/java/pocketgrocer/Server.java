package pocketgrocer;

import static spark.Spark.*;
import org.json.JSONObject;
import spark.Filter;

public class Server {
    public static void main(String[] args) throws Exception {
        Query query = new Query();
        query.prepareStatements();
        // port(8080); // default: 4567

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        // Spark HTTP Endpoints
        get("/", (req, res) -> "Welcome to Pocket Grocer");

        // Hello World API Test
        get("/hello", (req, res) -> "Hello World");

        // Stops the Server
        get("/stop", (request, response) -> {
            query.closeConnection();
            stop();
            return "Server Stopped";
        });

        // Checks if a user exists
        post("/users/exists", (request, response) -> {
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
        post("/users/login", (request, response) -> {
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

        post("/users/ingroup", (request, response) -> {
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

        // get user group and group member data
        post("/users/groupdata", (request, response) -> {
            try {
                JSONObject user = new JSONObject(request.body());
                String username = user.getString("userName");

                if (!query.userExists(username)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else if (!query.isMemberInGroup(username)) {
                    response.status(428);
                    return ("User is not in any group");
                } else {
                    JSONObject groupDetails = query.groupNameAndGroupMates(username);
                    response.status(200);
                    response.type("application/json");
                    return groupDetails.toString();
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        post("/groups/exists", (request, response) -> {
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
            //the params get passed in as strings, so they need to be parsed as an int
            int shared = Integer.parseInt(items.getString("shared"));
            String category = items.getString("category");
            int storage = Integer.parseInt(items.getString("storage"));
            //I couldn't get a date object from the Json parameter, so for now it will be a string YYYY-MM-DD
            //and in Query.java it gets converted to a sql date
            String expiration = items.getString("expiration");
            int quantity = Integer.parseInt(items.getString("quantity"));

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

        post("/items/get", (request, response) -> {
            JSONObject user = new JSONObject(request.body());
            String userName = user.getString("userName");

            try {
                if (!query.userExists(userName)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else {
                    JSONObject itemsList = query.getUserItems(userName);
                    response.status(200);
                    response.type("application/json");
                    return itemsList.toString();
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

//        post("/items/getGroup", (request, response) -> {
//            JSONObject group = new JSONObject(request.body());
//            String groupName = group.getString("groupName");
//
//            try {
//                if (!query.checkGroupExists(groupName)) {
//                    response.status(409);
//                    return ("groupName doesn't exist");
//                } else {
//                    JSONObject itemsList = query.getGroupItems(groupName);
//                    response.status(200);
//                    response.type("application/json");
//                    return itemsList.toString();
//                }
//            } catch (Exception e) {
//                response.status(400);
//                return (e);
//            }
//        });

        post("/items/getRecent", (request, response) -> {
            JSONObject user = new JSONObject(request.body());
            String userName = user.getString("userName");

            try {
                if (!query.userExists(userName)) {
                    response.status(409);
                    return ("Username doesn't exist");
                } else {
                    JSONObject itemsList = query.getRecentlyPurchased(userName);
                    response.status(200);
                    response.type("application/json");
                    return itemsList.toString();
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });

        post("/items/delete", (request, response) -> {
            JSONObject item = new JSONObject(request.body());
            int itemID = Integer.parseInt(item.getString("itemID"));

            try {
                if (!query.checkItem(itemID)) {
                    response.status(409);
                    return ("Item doesn't exist");
                } else if (query.deleteItem(itemID)) {
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

        post("/items/shared", (request, response) -> {
            JSONObject item = new JSONObject(request.body());
            int itemID = Integer.parseInt(item.getString("itemID"));
            int shared = Integer.parseInt(item.getString("shared"));

            try {
                if (!query.checkItem(itemID)) {
                    response.status(409);
                    return ("Item doesn't exist");
                } else if (query.changeShared(itemID, shared)) {
                    response.status(200);
                    return ("Successfully changed shared value");
                } else {
                    response.status(400);
                    return ("Failed changing shared value");
                }
            } catch (Exception e) {
                response.status(400);
                return (e);
            }
        });
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Note: this may or may not be necessary in your particular application
            response.type("application/json");
        });
    }
}