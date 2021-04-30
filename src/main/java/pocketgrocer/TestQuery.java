package pocketgrocer;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestQuery {
    public static void main(String[] args) throws Exception {
        String username = "sgsevier";
        String firstName = "Sophia";
        String lastName = "Sevier";
        String password = "passwordHi";

        Query query = new Query();
        query.prepareStatements();
        assertTrue(query.userExists(username));
        assertFalse(query.userExists("cooper"));
        query.closeConnection();
    }
}
