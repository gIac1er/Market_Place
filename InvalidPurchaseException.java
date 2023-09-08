/**
 * InvalidPurchaseException Class
 * Custom exception thrown when an invalid purchase is made by a customer
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */
public class InvalidPurchaseException extends Exception {
    public InvalidPurchaseException(String msg) {
        super(msg);
    }
}
