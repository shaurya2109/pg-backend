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
    public void checkGroupExists() throws Exception {
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

    @Test
    /**
     * checks if item with itemID 12 exists
     */
    public void checkItemExists() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        assertTrue(query.checkItem(12));
        query.closeConnection();
    }

    @Test
    /**
     * Check item is deleted properly
     * @throws Exception
     */
    public void checkDeleteItem() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        String date = "2021-05-18";
        query.addItem("bread", "sgsevier", 1, "bakery", 1, date, 2);
        int id = query.getID();
        query.deleteItem(id);
        assertFalse(query.checkItem(id));
    }

    @Test
    /**
     * Check if item shared changed correctly
     */
    public void checkChangeShared() throws Exception {
        QueryTest query = new QueryTest();
        query.prepareStatements();
        String date = "2021-05-28";
        query.addItem("apple", "sgsevier", 0, "fruit", 1, date, 2);
        assertTrue(query.changeShared(query.getID(), 0));
        query.deleteItem(query.getID());
    }
}
