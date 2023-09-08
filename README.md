Project 5 for Purdue's CS180 Course

Features
- A marketplace that allows sellers and buyers to create and use their accounts to perform transactions!
- Sellers can:
    - Create/remove/modify their listings
    - Check their sales by store (data is sortable)
- Buyers can:
    - Add/remove items from cart
    - Check out all items from cart
    - Check out an existing sellers listing
    - Sort a seller's listing by quantity, price, sales
    - Search for a seller's listing by name, description
    - Plus other ways to view listings

- The marketplace runs on a simple GUI chain
- Multiple client (buyer/seller) can run concurrently, (most) features are auto-updated
    - If features are out of sync, clients have an option that simulate "updating" similar to a web browser


Instruction on how to compile and use each classes 
  - Run the server class first. Server will run on "localhost", port 4242.
  - Run the client class next, client will auto-connect to "localhost", port 4242.
  - Have fun using the Market Place!


Instruction on each class
  
  - Client: Main GUI interface the marketplace will run on, auto connect to "localhost" and port number 4242, make sure Server is running before running Client! 
  
  - Server: Main backend class, receives request from Client and sends request to the respective subclass, then sends data back to Client. 
  Runs on "localhost" and port number 4242
 
  - Seller: Handles backend seller files and contains useful methods that edits and returns information about the seller file, notably contains calculations when a purchase occurs to determine whether the calculation is valid or not. 
  
  - Customer: Handles backend customer files and contains useful methods that edits and returns information about the seller file, notably have the ability to save a user's shopping cart over sessions.
  - Listing: Listing object class, consists of two constructs that constructs listing objects for both seller's listings and buyer's listings in cart + purchased listings. This class majorly assist both Customer, Seller, and Main class
  
  - CustomerHistory: Object class to assist Seller class's customer purchased log output
  
  - SalesHistory: Object class to assist Seller class's sales log output
 
  - UserAlreadyExistException: Custom exception class that occurs when a user already exist
  
  - InvalidPurchaseException: Custom exception class that occurs when a invalid purchase is made
  
  - ListingNotFoundException: Custom exceptions class that occurs when a target listing is not found