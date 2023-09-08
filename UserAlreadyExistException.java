/**
 * UserAlreadyExistException Class
 * Custom exception thrown if a username is already taken when creating a seller/customer account
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */
public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}
