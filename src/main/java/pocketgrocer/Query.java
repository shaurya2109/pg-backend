package pocketgrocer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Query {
    // DB Connection
    private static Connection conn;

    private static final Logger log;

    //canned queries
    private static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?,?)";
    private PreparedStatement insertUser;


    private static final String DELETE_USER = "DELETE FROM USERS WHERE userName = (?)";
    private PreparedStatement deleteUser;


    private static final String CHECK_USER = "SELECT COUNT(*) FROM USERS WHERE userName = (?)";
    private PreparedStatement checkUser;

    private static final String GET_GROUPNAME = "SELECT groupName FROM USERS WHERE userName = (?)";
    private PreparedStatement getGroupName;

    private static final String GET_GROUP_MEMBERS = "SELECT userName FROM USERS WHERE groupName = (?)";
    private PreparedStatement getGroupMembers;


    private static final String CHECK_GROUP = "SELECT COUNT(*) FROM USERS WHERE groupName = (?)";
    private PreparedStatement checkGroup;

    private static final String SEARCH_USER = "SELECT * FROM USERS WHERE userName = (?)";
    private PreparedStatement searchUser;

    private static final String ADD_ITEM = "INSERT INTO INVENTORY VALUES (?,?,?,?,?,?,?,?)";
    private PreparedStatement addItem;

    private static final String DELETE_ITEM = "DELETE FROM INVENTORY WHERE itemID = (?)";
    private PreparedStatement deleteItem;

    private static final String GET_ID = "SELECT * FROM Counter";
    private PreparedStatement get_counter;

    private static final String UPDATE_ID = "UPDATE Counter set count = (?)";
    private PreparedStatement update_id;

    private static final String GET_USER_ITEMS = "SELECT * FROM INVENTORY WHERE userName = (?)";
    private PreparedStatement getUserItems;

    private static final String CHECK_ITEM = "SELECT COUNT(*) FROM INVENTORY WHERE itemID = (?)";
    private PreparedStatement checkItem;

    private static final String CHANGE_SHARED = "UPDATE INVENTORY SET shared = (?) WHERE itemID = (?)";
    private PreparedStatement changeShared;

    private static final String GET_GROUP_ITEMS = "SELECT * FROM INVENTORY WHERE groupName = (?)";
    private PreparedStatement getGroupItems;

    //adds a member to a group
    //this can also be used when a user is removed from a group because we set their groupName back to ""
    //if we want to use this statement for both it will need a more general name like UPDATE_MEMBER_GROUP
    private static final String ADD_MEMBER = "UPDATE USERS set groupName = (?) WHERE userName = (?)";
    private PreparedStatement addMember;

    private static final String GET_RECENT_ITEMS = "SELECT * FROM INVENTORY WHERE userName = (?) AND (dateAdded BETWEEN GETDATE() - 7 AND GETDATE())";
    private PreparedStatement getRecentItems;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(Query.class.getName());
    }

    /*
     * prepare all the SQL statements in this method.
     */
    public void prepareStatements() throws SQLException, IOException {
        insertUser =  conn.prepareStatement(INSERT_USER);
        deleteUser =  conn.prepareStatement(DELETE_USER);
        checkUser =  conn.prepareStatement(CHECK_USER);
        searchUser =  conn.prepareStatement(SEARCH_USER);
        checkGroup =  conn.prepareStatement(CHECK_GROUP);
        // checkMember = conn.prepareStatement(CHECK_MEMBER);
        addMember =  conn.prepareStatement(ADD_MEMBER);
        addItem =  conn.prepareStatement(ADD_ITEM);
        deleteItem = conn.prepareStatement(DELETE_ITEM);
        get_counter = conn.prepareStatement(GET_ID);
        update_id = conn.prepareStatement(UPDATE_ID);
        getUserItems = conn.prepareStatement(GET_USER_ITEMS);
        checkItem = conn.prepareStatement(CHECK_ITEM);
        changeShared = conn.prepareStatement(CHANGE_SHARED);
        getGroupName = conn.prepareStatement(GET_GROUPNAME);
        getGroupMembers = conn.prepareStatement(GET_GROUP_MEMBERS);
        getRecentItems = conn.prepareStatement(GET_RECENT_ITEMS);
        getGroupItems = conn.prepareStatement(GET_GROUP_ITEMS);
    }

    public Query() throws Exception {
        log.info("Loading application properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/java/pocketgrocer/resources/application.properties"));

        log.info("Connecting to the database");
        conn = DriverManager.getConnection(properties.getProperty("url"), properties);
        log.info("Database connection test: " + conn.getCatalog());

        log.info("Create database schema");
        Scanner scanner = new Scanner(new FileInputStream("src/main/java/pocketgrocer/resources/schema.sql"));
        Statement statement = conn.createStatement();
        while (scanner.hasNextLine()) {
            statement.execute(scanner.nextLine());
        }
    }

    // function to close the connection to a SQL database
    public void closeConnection() throws SQLException {
        log.info("Closing database connection");
        conn.close();
    }

    /**
     * checks if a user with the given username already exists in the database
     * @param userName
     * @return whether or not the user already exists
     */
    public boolean userExists(String userName) {
        try {
            checkUser.setString(1, userName);
            ResultSet rs = checkUser.executeQuery();
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            return num == 1;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Adds a user into the USERS table
     * @param userName
     * @param firstName
     * @param lastName
     * @param password
     * @return whether or not the user was successfully added
     */
    public boolean addUser(String userName, String firstName, String lastName, String password){
        try {
            userName = userName.toLowerCase();
            firstName = firstName.toLowerCase();
            lastName = lastName.toLowerCase();
            password = password.toLowerCase();

            insertUser.setString(1, userName);
            insertUser.setString(2, firstName);
            insertUser.setString(3, lastName);
            insertUser.setString(4, password);
            //adds an empty groupName for the user since its blank until they set one
            insertUser.setString(5, "");
            insertUser.execute();

            // // Generate a random cryptographic salt
            // SecureRandom random = new SecureRandom();
            // byte[] salt = new byte[16];
            // random.nextBytes(salt);
            // KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);

            // // Generate the hash
            // SecretKeyFactory factory = null;
            // byte[] hash = null;
            // try {
            //     factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            //     hash = factory.generateSecret(spec).getEncoded();
            // } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            //     throw new IllegalStateException();
            // }

            return true;


        } catch (SQLException error){
            return false;
        }

    }

    /**
     * Deletes a User
     * @param userName
     * @return whether or not the user was successfully deleted
     */
    public boolean deleteUser(String userName){
        try {
            //We don't need to check if the user exists in the table since the request is coming straight from
            deleteUser.setString(1, userName);
            deleteUser.execute();
            return true;

        } catch(SQLException error){
            return false;
        }
    }

    /**
     * Logs in a User
     * @param userName
     * @return whether or not the user was successfully logged in
     */
    public boolean checkLogin(String userName, String pass) throws SQLException {
        userName = userName.toLowerCase();
        pass =  pass.toLowerCase();
        searchUser.setString(1, userName);
        ResultSet userSet = searchUser.executeQuery();

        while(userSet.next()){
            String getUser = userSet.getString("userName");
            String getPass = userSet.getString("password");
            if(getUser.equals(userName) && getPass.equals(pass)) {
                return true;
            }
        }
        return false;
    }

    // method that returns all the information about a user like name, username, groupname, etc
    public JSONObject getUserDetails (String userName)  throws SQLException{
        searchUser.setString(1, userName);
        ResultSet userSet = searchUser.executeQuery();

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        while(userSet.next()){
            JSONObject record = new JSONObject();
            record.put("userName", userSet.getString("userName"));
            record.put("firstName", userSet.getString("firstName"));
            record.put("lastName", userSet.getString("lastName"));
            record.put("groupName", userSet.getString("groupName"));
            array.put(record);
        }

        jsonObject.put("UserInfo", array);
        return jsonObject;
    }

    /**
     * Checks whether or not this groupname already exists
     * @param groupname of the group being created
     * @return true if the groupName exists, false otherwise
     */
    public boolean checkGroupExists(String groupname){
        try {
            groupname =  groupname.toLowerCase();
            checkGroup.setString(1, groupname);
            ResultSet rs = checkGroup.executeQuery();
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            return num == 1;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Checks whether or not a member is already in a group
     * @param userName of the person wanting to create or add themselves to a group
     * @return true if the user is in a group
     */
    public boolean isMemberInGroup(String userName){
        try {
            searchUser.setString(1, userName);
            ResultSet rs = searchUser.executeQuery();
            while(rs.next()) {
                String getGroup = rs.getString("groupName");
                //if the group entry for the user is blank, they can now be added to a group
                if(getGroup.equals("")){
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        } catch (SQLException error){
            return false;
        }
    }

    public JSONObject groupNameAndGroupMates(String userName)  throws SQLException {
        List<String> groupMembers = new ArrayList<>();
        String groupName = "";

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        if(!isMemberInGroup(userName)){
            JSONObject record = new JSONObject();
            record.put("GroupName", groupName);
            record.put("groupMembers", groupMembers);
            array.put(record);
            jsonObject.put("Result", array);
            return jsonObject;
        }

        // now get the groupname of the current user -

        getGroupName.setString(1, userName);
        ResultSet rs = getGroupName.executeQuery();

        while(rs.next()) {
            groupName = rs.getString("groupName");
        }

        // getting all the members now -
        getGroupMembers.setString(1, groupName);
        ResultSet result = getGroupMembers.executeQuery();

        while(result.next()) {
            groupMembers.add(result.getString("userName"));
        }

        // returning the results -
        JSONObject record = new JSONObject();
        record.put("GroupName", groupName);
        record.put("groupMembers", groupMembers);
        array.put(record);
        jsonObject.put("Result", array);
        return jsonObject;
    }

    /**
     * Updates the groupName of a member. The two possible updates are either adding them to a group,
     * or removing them from a group. If a member is being removed, a blank "" is put in place of their groupName
     * @param userName of the person being added
     * @param groupName of the group
     * @return true if the groupName was
     */
    public boolean updateGroupName(String userName, String groupName){
        try {
            addMember.setString(1, groupName);
            addMember.setString(2, userName);
            addMember.execute();
            return true;
        } catch (SQLException error){
            error.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a an inventory item
     * @param itemName
     * @param userName
     * @param shared
     * @param category
     * @param quantity
     * @param storage
     * @param expiration
     * @param quantity
     * @return true if the item was added successfully, false otherwise
     */
    public boolean addItem(String itemName, String userName, int shared, String category,
                        int storage, String expiration, int quantity) throws ParseException {
        Date expirationDate = Date.valueOf(expiration);
        Date dateAdded = new Date(System.currentTimeMillis());

        for(int i = 0; i < quantity; i++){
            try {
//                System.out.println(1);
                int itemID = getID() + 1;
//                System.out.println(1);
                addItem.setInt(1, itemID);
                addItem.setString(2, itemName);
                addItem.setString(3, userName);
                addItem.setInt(4, shared);
                addItem.setString(5, category);
                addItem.setInt(6, storage);
                addItem.setDate(7, expirationDate);
                addItem.setDate(8, dateAdded);
//                System.out.println(1);
                addItem.execute();
//                System.out.println(1);
                UpdateID(itemID);
//                System.out.println(1);
                return true;
            } catch (SQLException error){
                System.out.println(error);
                return false;
            }
        }
        return true;
    }

    /**
     * gets the current ID in the counter table for adding inventory items
     * @return the ID for the next item
     */
    public int getID(){
        try{
            ResultSet rs = get_counter.executeQuery();
            while(rs.next()){
                return rs.getInt(1);
            }
        }catch(SQLException error){
            System.out.println(error);
            return -1;
        }
        return -1;
    }

    /**
     * Updates the ID in the counter table for the next inventory item
     * @return true if the counter was executed successfully, false otherwise
     */
    public boolean UpdateID(int id){
        try {
            update_id.setInt(1, id);
            update_id.execute();
            return true;
        } catch (SQLException error){
            error.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a singular item from the inventory table
     * @param itemID the id of the item being deleted
     * @return true if the item was deleted successfully, false otherwise
     */
    public boolean deleteItem(int itemID){
        try {
            deleteItem.setInt(1, itemID);
            deleteItem.execute();
            return true;

        } catch(SQLException error){
            System.out.println(error);
            return false;
        }
    }

    /**
     * Retrieves all of the items for a particular user
     * @param userName the user name we are getting the items for
     * @return JSONObject of all the items for that user
     */
    public JSONObject getUserItems (String userName) throws SQLException {

        getUserItems.setString(1, userName);
        ResultSet rs = getUserItems.executeQuery();

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        int itemNums = 1;
        while(rs.next()) {
            JSONObject record = new JSONObject();
            //Inserting key-value pairs into the json object
            record.put("itemID", rs.getInt("itemID"));
            record.put("itemName", rs.getString("itemName"));
            record.put("userName", rs.getString("userName"));
            record.put("shared", rs.getInt("shared"));
            record.put("category", rs.getString("category"));
            record.put("storage", rs.getInt("storage"));
            record.put("expiration", rs.getDate("expiration"));
            record.put("dateAdded", rs.getDate("dateAdded"));
            array.put(record);
        }
        jsonObject.put("Items", array);
        return jsonObject;
    }

    /**
     * Retrieves all of the items for a particular group
     * @param groupName the user name we are getting the items for
     * @return JSONObject of all the items for that group
     */
    public JSONObject getGroupItems(String groupName) throws SQLException {

        getGroupItems.setString(1, groupName);
        ResultSet rs = getGroupItems.executeQuery();

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        int itemNums = 1;
        while(rs.next()) {
            JSONObject record = new JSONObject();
            //Inserting key-value pairs into the json object
            record.put("itemID", rs.getInt("itemID"));
            record.put("itemName", rs.getString("itemName"));
            record.put("userName", rs.getString("userName"));
            record.put("shared", rs.getInt("shared"));
            record.put("category", rs.getString("category"));
            record.put("storage", rs.getInt("storage"));
            record.put("expiration", rs.getDate("expiration"));
            record.put("dateAdded", rs.getDate("dateAdded"));
            array.put(record);
        }
        jsonObject.put("Items", array);
        return jsonObject;
    }

    /**
     * Checks whether or not this item exists
     * @param itemID the unique item identifier
     * @return true if the item exists, false otherwise
     */
    public boolean checkItem(Integer itemID){
        try {
            checkItem.setInt(1, itemID);
            ResultSet rs = checkItem.executeQuery();
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            return num == 1;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Changes the shared value of an item
     * @param itemID the unique item identifier
     * @param currVal the current shared indicator value for the item
     * @return true if the item shared indicator was successfully changed
     */
    public boolean changeShared(int itemID, int currVal){
        try {
            int newVal = 0;
            if(currVal == 0){
                newVal = 1;
            }
            changeShared.setInt(1, newVal);
            changeShared.setInt(2, itemID);
            changeShared.execute();
            return true;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Retrieves the recently purchased items for a user. These will be displayed in a dropdown when inputting
     * the purchased items (people tend to buy similar items over time so we wanted to make those readily available)
     * @param userName the userName of the user for which we are getting the recently purchased items
     * @return JSONObject of the recently purchased items
     */
    public JSONObject getRecentlyPurchased(String userName) throws SQLException {
        getRecentItems.setString(1, userName);
        ResultSet rs = getUserItems.executeQuery();

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        int itemNums = 1;
        while(rs.next()) {
            JSONObject record = new JSONObject();
            //Inserting key-value pairs into the json object
            record.put("itemID", rs.getInt("itemID"));
            record.put("itemName", rs.getString("itemName"));
            record.put("userName", rs.getString("userName"));
            record.put("shared", rs.getInt("shared"));
            record.put("category", rs.getString("category"));
            record.put("storage", rs.getInt("storage"));
            record.put("expiration", rs.getDate("expiration"));
            record.put("dateAdded", rs.getDate("dateAdded"));
            array.put(record);
        }
        jsonObject.put("Items", array);
        return jsonObject;
    }
}
