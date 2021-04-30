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

    //canned queries
    private static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?)";
    private PreparedStatement insertUser;


    private static final String DELETE_USER = "DELETE FROM USERS WHERE username = (?)";
    private PreparedStatement deleteUser;


    private static final String CHECK_USER = "SELECT COUNT(*) FROM USERS WHERE username = (?)";
    private PreparedStatement checkUser;


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

    }

    public static void main(String[] args) throws Exception {
        log.info("Loading application properties");
        Properties properties = new Properties();
        properties.load(Query.class.getClassLoader().getResourceAsStream("application.properties"));

        log.info("Connecting to the database");
        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
        log.info("Database connection test: " + connection.getCatalog());

        log.info("Create database schema");
        Scanner scanner = new Scanner(Query.class.getClassLoader().getResourceAsStream("schema.sql"));
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
//            int userExists = insertUser.execute();
//            return userExists == 1;
            // TODO: check w soph
            return insertUser.execute();
        } catch(SQLException error) {
            return false;
        }
    }

    /**
     * Adds a user into the USERS table
     * @param username
     * @param firstName
     * @param lastName
     * @param password
     * @return whether or not the user already exists
     */
    public boolean addUser(String username, String firstName, String lastName, String password){
        try {
            username = username.toLowerCase();
            firstName = firstName.toLowerCase();
            lastName = lastName.toLowerCase();
            password = password.toLowerCase();

            insertUser.setString(1, username);
            insertUser.setString(1, firstName);
            insertUser.setString(1, lastName);
            insertUser.setString(1, password);
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


        } catch(SQLException error) {
            return false;
        }

    }

    /**
     * checks if a user with the given username already exists in the database
     * @param username
     * @return whether or not the user was deleted
     */
    public boolean deleteUser(String username) {
        try {
            //We don't need to check if the usre exists in the table since the request is coming straight from
            deleteUser.setString(1, username);
            deleteUser.execute();
            return true;

        } catch(SQLException error) {
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