package pocketgrocer;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestQuery {
    String username = "sgsevier";
    String firstName = "Sophia";
    String lastName = "Sevier";
    String password = "passwordHi";

    @Test
    public void checkUserExists() throws Exception {
        Query query = new Query();
        query.prepareStatements();
        assertTrue(query.userExists("sgsevier"));
        assertFalse(query.userExists("sgsevier1111111"));
        query.closeConnection();
    }

    // first adding a new user then checking if it exists or not
    @Test
    public void checkAddUser1() throws Exception {
        Query query = new Query();
        query.prepareStatements();
        String userName = "tushar411";
        String firstName = "Tushar";
        String lastName = "Poddar";
        String password = "pass2";
        query.addUser(userName, firstName, lastName, password);
        assertTrue(query.userExists("tushar411"));
        query.deleteUser(userName);
        assertFalse(query.userExists("tushar411"));
    }

    // trying to add a user that already exists
    @Test
    public void checkAddUser2() throws Exception {
        Query query = new Query();
        query.prepareStatements();
        String userName = "sgsevier";
        String firstName = "Sophia";
        String lastName = "S";
        String password = "pass2";
        assertFalse(query.addUser(userName, firstName, lastName, password));
    }

    @Test
    public void checkGroupName() throws Exception {
        Query query = new Query();
        query.prepareStatements();
        assertTrue(query.checkGroupExists("group4745"));
        query.closeConnection();
    }


    @Test
    public void updateGroupName() throws Exception {
        Query query = new Query();
        query.prepareStatements();
        assertTrue(query.updateGroupName("sgsevier", "group4745-1"));
        query.closeConnection();
    }

    // checkLogin(String userName, String pass)
    // checkGroupExists(String groupname)
    //isMemberInGroup(String userName)
    // updateGroupName(String userName, String groupName)

}
