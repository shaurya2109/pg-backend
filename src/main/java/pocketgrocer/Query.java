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

    private static final String ADD_ITEM = "INSERT INTO INVENTORY VALUES (?,?,?,?,?,?,?,?,?)";
    private PreparedStatement addItem;


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
     * @param username
     * @return whether or not the user already exists
     */
    public boolean userExists(String username) {
        try {
            checkUser.setString(1, username);
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
     * @param username
     * @param firstName
     * @param lastName
     * @param password
     * @return whether or not the user was successfully added
     */
    public boolean addUser(String username, String firstName, String lastName, String password){
        try {
            username = username.toLowerCase();
            firstName = firstName.toLowerCase();
            lastName = lastName.toLowerCase();
            password = password.toLowerCase();

            insertUser.setString(1, username);
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
     * @param username
     * @return whether or not the user was successfully deleted
     */
    public boolean deleteUser(String username){
        try {
            //We don't need to check if the user exists in the table since the request is coming straight from
            deleteUser.setString(1, username);
            deleteUser.execute();
            return true;

        } catch(SQLException error){
            return false;
        }
    }

    /**
     * Logs in a User
     * @param username
     * @return whether or not the user was successfully logged in
     */
    public boolean checkLogin(String username, String pass) throws SQLException {
            username = username.toLowerCase();
            pass =  pass.toLowerCase();
            searchUser.setString(1, username);
            ResultSet userSet = searchUser.executeQuery();

            while(userSet.next()){
                String getUser = userSet.getString("userName");
                String getPass = userSet.getString("password");
                if(getUser.equals(username) && getPass.equals(pass)) {
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
     * Adds a member to a group
     * @param userName of the person being added
     * @param groupName of the group
     * @return true if member is successfully added to the group, false otherwise
     */
    public boolean addMemberToGroup(String userName, String groupName){
        try {
            userName = userName.toLowerCase();
            groupName = groupName.toLowerCase();
            addMember.setString(1, groupName);
            addMember.setString(2, userName);
            addMember.executeQuery();
            return true;
        } catch (SQLException error){
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
//    public void addItem(String itemName, String userName, int shared, String category, int quantity, String date, int storage, String groupName){
//        try {
//            //create a list
//
//            //for the # of items (quantity)
//
//            //generate a unique ID using the updating int in an array for the item name
//
//            return;
//        } catch (SQLException error){
//            return;
//        }
//    }


}

/*
query.userExists(username);
query.addUser(username, firstName, lastName, password);
query.checkLogin(username, password);
query.inGroup(username); // checks if user is already in a group or not
*/