package pocketgrocer;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.sql.Timestamp;
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

    // Password hashing parameter constants
    private static final int HASH_STRENGTH = 65536;
    private static final int KEY_LENGTH = 128;

    //Users table columns: userName, firstName, lastName, password, groupName

    //canned queries
    private static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?,?)";
    private PreparedStatement insertUser;


    private static final String DELETE_USER = "DELETE FROM USERS WHERE userName = (?)";
    private PreparedStatement deleteUser;


    private static final String CHECK_USER = "SELECT COUNT(*) FROM USERS WHERE userName = (?)";
    private PreparedStatement checkUser;


    private static final String CHECK_GROUP = "SELECT COUNT(*) FROM USERS WHERE groupName = (?)";
    private PreparedStatement checkGroup;

    private static final String SEARCH_USER = "SELECT * FROM USERS WHERE userName = (?)";
    private PreparedStatement searchUser;

    private static final String ADD_ITEM = "INSERT INTO INVENTORY VALUES (?,?,?,?,?,?,?)";
    private PreparedStatement addItem;

    private static final String DELETE_ITEM = "DELETE FROM INVENTORY WHERE itemID = (?)";
    private PreparedStatement deleteItem;

    private static final String GET_ID = "SELECT * FROM Counter";
    private PreparedStatement get_counter;

    private static final String UPDATE_ID = "UPDATE Counter set count = (?)";
    private PreparedStatement update_id;


    //adds a member to a group
    //this can also be used when a user is removed from a group because we set their groupName back to ""
    //if we want to use this statement for both it will need a more general name like UPDATE_MEMBER_GROUP
    private static final String ADD_MEMBER = "UPDATE USERS set groupName = (?) WHERE userName = (?)";
    private PreparedStatement addMember;

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

    /**
     * Updates the groupName of a member
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

//    /**
//     * Adds a member to a group
//     * @param itemName
//     * @param userName
//     * @param shared
//     * @param category
//     * @param quantity
//     * @param storage
//     * @param date
//     * @param groupName
//     * @return a list of lists of item entries
//     */


    // get the group name for the person that the item is being added
    // counter variable in a table
    // auto increment implementation




    public boolean addItem(String itemName, String userName, int shared, String category,
                        int storage, Date expiration){
        try {
            System.out.println(1);
            int itemID = getID() + 1;
            System.out.println(1);
            addItem.setInt(1, itemID);
            addItem.setString(2, itemName);
            addItem.setString(3, userName);
            addItem.setInt(4, shared);
            addItem.setString(5, category);
            addItem.setInt(6, storage);
            addItem.setDate(7, expiration);
            System.out.println(1);
            addItem.execute();
            System.out.println(1);
            Update_ID(itemID);
            System.out.println(1);
            return true;
        } catch (SQLException error){
            System.out.println(error);
            return false;
        }
    }

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

    public boolean Update_ID(int id){
        try {
            update_id.setInt(1, id);
            update_id.execute();
            return true;
        } catch (SQLException error){
            error.printStackTrace();
            return false;
        }
    }

    public boolean delete_item(int itemID){
        try {
            //We don't need to check if the user exists in the table since the request is coming straight from
            deleteItem.setInt(1, itemID);
            deleteItem.execute();
            return true;

        } catch(SQLException error){
            System.out.println(error);
            return false;
        }
    }
}

/*
query.userExists(username);
query.addUser(username, firstName, lastName, password);
query.checkLogin(username, password);
query.inGroup(username); // checks if user is already in a group or not
*/



// add item to the inventory datapoint