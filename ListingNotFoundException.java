/**
 * ListingNotFoundException Class
 * Custom exception thrown when a target listing is not found
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */
public class ListingNotFoundException extends Exception {
    public ListingNotFoundException(String msg) {
        super(msg);
    }
}
