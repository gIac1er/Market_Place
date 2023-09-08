import java.io.Serializable;

/**
 * CustomerHistory Class
 * Used to create customer history for seller class
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/29/23
 */

public class CustomerHistory implements Serializable {
    private String store;
    private String buyer;
    private int unitsBought;

    //constructor
    public CustomerHistory(String store, String buyer, int unitsBought) {
        this.store = store;
        this.buyer = buyer;
        this.unitsBought = unitsBought;
    }

    //customerHistory
    public CustomerHistory(String store, int unitsBought) {
        this.store = store;
        this.unitsBought = unitsBought;
    }

    //retreive store
    public String getStore() {
        return store;
    }

    //set store
    public void setStore(String store) {
        this.store = store;
    }

    //get the buyer
    public String getBuyer() {
        return buyer;
    }

    //set the buyer
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    //get units bought
    public int getUnitsBought() {
        return unitsBought;
    }

    //set units bought
    public void setUnitsBought(int unitsBought) {
        this.unitsBought = unitsBought;
    }

    //a to string method
    @Override
    public String toString() {
        return String.format("Store:%s, Buyer:%s, Units_Bought:%d", store, buyer, unitsBought);
    }

    //print store by sold
    public String printStoreBySold() {
        return String.format("Store:%s, Sales_Made:%d", store, unitsBought);
    }
}



