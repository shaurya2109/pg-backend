package pocketgrocer;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Query {
    // DB Connection
    private Connection conn;

    private static final Logger log;

    // Password hashing parameter constants
    private static final int HASH_STRENGTH = 65536;
    private static final int KEY_LENGTH = 128;

    //Users table columns: userName, firstName, lastName, password, groupID
    //Groups table columns: userName, firstName, lastName, password, groupID

    //canned queries
    private static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?,?)";
    private PreparedStatement insertUser;


    private static final String DELETE_USER = "DELETE FROM USERS WHERE userName = (?)";
    private PreparedStatement deleteUser;


    private static final String CHECK_USER = "SELECT COUNT(*) FROM USERS WHERE userName = (?)";
    private PreparedStatement checkUser;


    private static final String CHECK_GROUP = "SELECT COUNT(*) FROM USERS WHERE groupName = (?)";
    private PreparedStatement checkGroup;

    // private static final String CHECK_MEMBER = "SELECT COUNT(*) FROM USERS WHERE groupName = (?)";
    // private PreparedStatement checkMember;

    private static final String SEARCH_USER = "SELECT * FROM USERS WHERE userName = (?)";
    private PreparedStatement searchUser;

    //adds a member to a group
    private static final String ADD_MEMBER = "UPDATE USERS set groupID = (?) WHERE userName = (?)";
    private PreparedStatement addMember;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(Query.class.getName());
    }

    /*
     * prepare all the SQL statements in this method.
     */
    public void prepareStatements() throws SQLException {
        insertUser =  conn.prepareStatement(INSERT_USER);
        deleteUser =  conn.prepareStatement(DELETE_USER);
        checkUser =  conn.prepareStatement(CHECK_USER);
        searchUser =  conn.prepareStatement(SEARCH_USER);

        checkGroup =  conn.prepareStatement(CHECK_GROUP);
        // checkMember = conn.prepareStatement(CHECK_MEMBER);
        addMember =  conn.prepareStatement(ADD_MEMBER);

    }

    public static void main(String[] args) throws Exception {
        log.info("Loading application properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/java/pocketgrocer/resources/application.properties"));

        log.info("Connecting to the database");
        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
        log.info("Database connection test: " + connection.getCatalog());

        log.info("Create database schema");
        Scanner scanner = new Scanner(new FileInputStream("src/main/java/pocketgrocer/resources/schema.sql"));
        Statement statement = connection.createStatement();
        while (scanner.hasNextLine()) {
            statement.execute(scanner.nextLine());
        }

        log.info("Closing database connection");
        connection.close();
    }

    // function to close the connection to a SQL database
    public void closeConnection() throws SQLException {
        conn.close();
    }


    /**
     * checks if a user with the given username already exists in the database
     * @param username
     * @return whether or not the user already exists
     */
    public boolean userExists(String username){
        try {
            checkUser.setString(1, username);
            return checkUser.execute();
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
    public boolean checkLogin(String username, String password){
        try {
            username = username.toLowerCase();
            password =  password.toLowerCase();
            searchUser.setString(1, username);
            ResultSet userSet = searchUser.executeQuery();

            while(userSet.next()){
                String getUser = userSet.getString("userName");
                String getPass = userSet.getString("password");
                if(getUser.equals(username) && getPass.equals("password")){
                    return true;
                }
            }
            //TODO: if we return false then we want the person making the request to know if the username/password didn't match
            //or if there was a general error that occured
            return false;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Checks whether or not this groupname already exists
     * @param username of the person creating the group
     * @param groupname of the group being created
     * @return true if the groupname exists, false otherwise
     */
//    public boolean checkGroup(String groupname){
//        try {
//            groupname =  groupname.toLowerCase();
//            checkGroup.setString(1, groupname);
//            int groupResult = checkGroup.execute();
//            //returns true if there already is a groupname with the same name
//            return groupResult >= 1;
//
//        } catch (SQLException error){
//            return false;
//        }
//    }

    /**
     * Checks whether or not a member is already in a group
     * @param username of the person wanting to create or add themselves to a group
     * @return true if the user is already in a group, false otherwise
     */
    public boolean checkMember(String username){
        try {
            // groupname = groupname.toLowerCase();
            searchUser.setString(1, username);
            ResultSet userSet = searchUser.executeQuery();
            while(userSet.next()) {
                String getGroup = userSet.getString("groupID");
                //if the group entry for the user is blank, they can now be added to a group
                if(getGroup.equals("")){
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (SQLException error){
            return false;
        }
    }

    /**
     * Adds a member to a group
     * @param username of the person being added
     * @param groupname of the group
     * @return true if member is successfully added to the group, false otherwise
     */
    public boolean AddMemberToGroup(String username, String groupname){
        try {
            username = username.toLowerCase();
            groupname =  groupname.toLowerCase();
            addMember.setString(1, groupname);
            addMember.setString(2, username);
            addMember.execute();
            return true;
        } catch (SQLException error){
            return false;
        }
    }

//       /**
//    * Example utility function that uses prepared statements
//    */
//   private int checkFlightCapacity(int fid) throws SQLException {
//     checkFlightCapacityStatement.clearParameters();
//     checkFlightCapacityStatement.setInt(1, fid);
//     ResultSet results = checkFlightCapacityStatement.executeQuery();
//     results.next();
//     int capacity = results.getInt("capacity");
//     results.close();

//     return capacity;
//   }

}

/*
query.userExists(username);
query.addUser(username, firstName, lastName, password);
query.checkLogin(username, password);
query.inGroup(username); // checks if user is already in a group or not
*/