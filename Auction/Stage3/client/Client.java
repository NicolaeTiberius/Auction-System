package client;

import java.io.Serializable;
import java.lang.ref.Cleaner;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;

import backend.Backend;
import frontend.Frontend;

import java.util.Scanner;
import api.API;

import shared.AuctionItem;
import shared.ClientData;

public class Client implements Serializable {

    private final String SERVER_NAME = "Auctionserver";
    public final int REGISTRY_PORT = 1099;

    public Client() {

        try {
            Registry registry = LocateRegistry.getRegistry();
            API server = (API) registry.lookup(this.SERVER_NAME);
            Scanner scanner = new Scanner(System.in);
            // INPUT TO CHOOSE Seller Class or Buyer Class
            System.out.println("Would you like to Buy an Item: b or Sell an Item: s ");
            String clientType = scanner.nextLine();

            // if the Option is a Buyer
            if (clientType.equals("b")) {
                // first display all of the highest bids on all Auctions
                String itemList = server.getAuctionItem();
                System.out.println("The current Auctions are" + itemList + "\n");

                Scanner createAuction = new Scanner(System.in);
                System.out.println("-Enter the details of the current Auction that you want to bid for: \n");
                System.out.println("Enter Auction ID: ");
                int Id = createAuction.nextInt();
                System.out.println("Enter your client ID: ");
                int ClientID = createAuction.nextInt();
                System.out.println("Enter client Name : ");
                String clientName = createAuction.next();
                System.out.println("Enter client Email: ");
                String clientEmail = createAuction.next();
                System.out.println("Enter bidding price: ");
                int price = createAuction.nextInt();
                System.out.println("Enter reserve price: ");
                int Reserveprice = createAuction.nextInt();
                // Creates the data of the bidder into the file and is able to send it
                ClientData bidder = new ClientData(clientName, clientEmail, ClientID);
                createAuction.close();
                // 1 is item ID, 20 is price for testing but can use a function instead
                server.bidAuction(bidder, Id, Reserveprice, price);// bids using the details provided
                System.out.println(
                        "Bidding has been added successfully to Auction ID: " + Id
                                + "\nClient details:\n Name:  " + clientName + "         Client email: " + clientEmail
                                + "\nAuction ID: " + Id + ", Price bid: " + price
                                + "\n\nPlease check the Server for extra details\n");

            } // END OF BUYER IF

            // SELLER CLIENT
            else if (clientType.equals("s")) {
                // Seller Client
                int idClose;
                Boolean firstStatus = true;
                while (firstStatus) {
                    System.out.println("\nWould you like to close an Auction : yes or no \n");
                    String decisionClose = scanner.next();
                    switch (decisionClose) {
                        case "yes":
                            firstStatus = true;
                            break;
                        case "no":
                            firstStatus = false;
                            break;
                        case "y":
                            firstStatus = true;
                            break;

                        case "n":
                            firstStatus = false;
                            break;

                        case "Yes":
                            firstStatus = true;
                            break;

                        case "No":
                            firstStatus = false;
                            break;

                    }
                    break;
                }
                if (firstStatus) {
                    System.out.println("\nEnter Auction ID to close: \n");
                    idClose = scanner.nextInt();
                    server.AuctionClose(idClose, "closed");
                    System.out.println("\nAuction " + idClose + " has been closed. \n");
                    Hashtable<ClientData, AuctionItem> Winner = server.AuctionWinner();
                    System.out.println("The winner of the Auction was: \n" + Winner);

                } else {
                    // Sell an item data input

                    // TEST DATA

                    // REAL DATA
                    System.out.println("Enter client ID: ");
                    int clientID = scanner.nextInt();
                    System.out.println("Enter selling Item name: ");
                    String title = scanner.next();
                    System.out.println("\nEnter selling Item ID: ");
                    int itemId = scanner.nextInt();
                    System.out.println("Enter selling Item Price: ");
                    int price = scanner.nextInt();
                    System.out.println("Enter selling Item Reserve Price: ");
                    int reservePrice = scanner.nextInt();
                    System.out.println("Enter selling Item Description: ");
                    String desc = scanner.nextLine();
                    // Creates an auction to sell using the details provided.
                    int AuctionID = server.createAuction(itemId, title, desc, price, reservePrice);

                    // server puts the item as created with the ID
                    server.getSpec(AuctionID, clientID);

                    System.out.println("The item name is: " + title);
                    System.out.println("\nItem ID is: " + AuctionID);
                    System.out.println("\nItem reserve Price is: " + reservePrice);
                    System.out.println("\nItem Price is: " + price);
                    System.out.println("\nItem Description is: " + desc);
                    System.out
                            .println("\nItem " + title + "         Item ID: " + itemId
                                    + " has been put up for Auction\n");

                    // Closing the Auction
                    Boolean status = true;
                    while (status) {
                        System.out.println("\nWould you like to close an Auction : yes or no \n");
                        String decision = scanner.next();
                        switch (decision) {
                            case "yes":
                                status = true;
                                break;
                            case "no":
                                status = false;
                                break;
                            case "y":
                                status = true;
                                break;

                            case "n":
                                status = false;
                                break;

                            case "Yes":
                                status = true;
                                break;

                            case "No":
                                status = false;
                                break;

                        }
                        break;
                    }
                    if (status) {
                        System.out.println("\nEnter Auction ID to close: \n");
                        AuctionID = scanner.nextInt();
                        server.AuctionClose(AuctionID, "closed");
                        System.out.println("\nAuction " + AuctionID + " has been closed. \n");
                        Hashtable<ClientData, AuctionItem> Winner = server.AuctionWinner();
                        System.out.println("The winner of the Auction was: \n" + Winner);

                    } else {
                        server.AuctionClose(AuctionID, "open");
                        System.out.println(
                                "\nAuction " + AuctionID + " remains open for biddings. \nThere is no winner yet. \n");

                    }

                    // once the auction is closed depending on the client's request it will print
                    // the message of the highest bidder .
                    // if this auction id matches the seller then it will close it, and display the
                    // highest bidder after.

                    scanner.close();
                    System.exit(0);
                } // END OF IF SELLER CLIENT

            } // END OF TRY LOOP
        } catch (Exception e) {
            System.err.println("🆘 exception:");
            e.printStackTrace();
            e.getCause();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Client: Buyer / Seller");
        }

        new Client();
    }
}