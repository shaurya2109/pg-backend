package pocketgrocer;

import static org.junit.Assert.*;

public class TestQuery {
    public static void main(String[] args) {
        String username = "sgsevier";
        String firstName = "Sophia";
        String lastName = "Sevier";
        String password = "passwordHi";

        Query query = new Query();
        assertTrue(query.userExists(username));
    }
}
