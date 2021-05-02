package pocketgrocer;

public class InventoryItem {
    int itemID;
    String itemName;
    String userName;
    int shared;
    int quantity;
    int storage;
    String date;

    public InventoryItem (int itemID, String itemName, String userName, int shared,
                          int quantity, int storage, String date, String groupName) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.userName = userName;
        this.shared = shared;
        this.quantity = quantity;
        this.storage = storage;
        this.date = date;
    }
}
