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
        // System.out.println(query.addUser("test1", "fN", "lN", "pass"));
        // System.out.println(query.isMemberInGroup("test1"));
        System.out.println(query.updateGroupName("sgsevier", "group4745"));
        // System.out.println(query.isMemberInGroup("test1"));
        // System.out.println(query.updateGroupName("test1", ""));
        query.closeConnection();
    }
}
