package pocketgrocer;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestQuery {
    @Test
    public void testGroupData() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        System.out.println(query.groupNameAndGroupMates("sgsevier").toString());
        query.closeConnection();
    }

    @Test
    public void testGetItems() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        System.out.println(query.getUserItems("cooper").toString());
        query.closeConnection();
    }

    // first adding a new user then checking if it exists or not
    @Test
    public void checkAddUser1() throws Exception {
        QueryTest query = new QueryTest();
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
        QueryTest query = new QueryTest();
        query.prepareStatements();
        String userName = "sgsevier";
        String firstName = "Sophia";
        String lastName = "S";
        String password = "pass2";
        assertFalse(query.addUser(userName, firstName, lastName, password));
    }

    @Test
    public void checkLogin() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.checkLogin("sgsevier", "pass2"));
        assertFalse(query.checkLogin("sgsevier", "passwordHI"));
    }

    @Test
    /**
     *
     */
    public void checkUserExists() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.userExists("sgsevier"));
        assertFalse(query.userExists("cooper"));
        assertFalse(query.userExists("sgsevier1111111"));
        query.closeConnection();
    }

    @Test
    public void updateGroupName() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.updateGroupName("sgsevier", "group4745-1"));
        query.closeConnection();
    }

    @Test
    public void checkGroupName() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.checkGroupExists("group4745-1"));
        query.closeConnection();
    }

    @Test
    public void isMemberInGroup() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.isMemberInGroup("sgsevier"));
    }

    @Test
    public void check_get_id() throws Exception{
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertEquals(12, query.getID());
        query.closeConnection();
    }

    @Test
    public void insertObject() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
//        LocalDate todayLocalDate = LocalDate.now( ZoneId.of( "America/Montreal" ) );
//        java.sql.Date sqlDate = java.sql.Date.valueOf(todayLocalDate);
        String date = "2021-05-10";
        query.addItem("bagel", "sgsevier", 0, "pantry", 0, date, 2);
        query.closeConnection();
    }

    @Test
    public void getObjects() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        JSONObject rs = query.getUserItems("sgsevier");
        System.out.println(rs);
        query.closeConnection();
    }

    // checkGroupExists(String groupname)

}
