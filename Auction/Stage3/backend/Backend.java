package backend;

import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;

import utility.GroupUtils;

import shared.AuctionItem;

import java.rmi.RemoteException;
import java.util.*;

import shared.ClientData;

public class Backend {

    private JChannel groupChannel;
    private RpcDispatcher dispatcher;
    private int requestCount;

    private ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>(); // a list of all auction Items created by
                                                                            // Seller
    // client.

    private Hashtable<Integer, AuctionItem> AllItems = new Hashtable<>(); // this table displays the IDs of all
                                                                          // auctionItems
    private Hashtable<ClientData, AuctionItem> AllWinners = new Hashtable<>(); // this table will hold all the winners
                                                                               // of items
    private int idCounter;
    private int clientCounter;

    public Backend() {
        this.requestCount = 0;

        // Connect to the group (channel)
        this.groupChannel = GroupUtils.connect();
        if (this.groupChannel == null) {
            System.exit(1); // error to be printed by the 'connect' function
        }

        // Make this instance of Backend a dispatcher in the channel (group)
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);
    }

    public Hashtable<ClientData, AuctionItem> AuctionWinnerBackend() {
        this.requestCount++;
        System.out.println(AllWinners);

        return AllWinners;

    }

    // This retrives the current Auction items by checking the ArrayList
    public int createAuctionBackend(int itemId, String title, String desc, int Price, int reserve_price)
            throws RemoteException {
        AuctionItem item = new AuctionItem(itemId, title, desc, Price);
        this.requestCount++;
        itemList.add(item); //
        AllItems.put(itemId, item); // adds item with ID to hashtable to determine the winner
        System.out.println("item added, the current list is" + itemList);
        idCounter++; // makes it go from 0 to 1 at the start and increase after etc.
        clientCounter++;
        return idCounter;
    }

    // Here you can bid for an Auction by providing the details then it will check
    // who is the winner of the Auction or update the highest bid price each time
    // someone bids in the HashTable
    // This method should be able to determine who is the WINNER of the auction and
    // display it in either SERVER or SELLER so they know who WON.
    public void bidAuctionBackend(ClientData bidder, int itemID, int reservePrice, int Price) {
        this.requestCount++;
        ClientData Bidder = bidder;
        int AuctionID = itemID;
        int bidPrice = Price;

        AuctionItem AuctionDetails = AllItems.get(AuctionID); // get the current auction using the Auction ID.
        int Auctionprice = AuctionDetails.getPrice();

        if (Auctionprice > bidPrice) {
            System.out.println(
                    "A new Client " + bidder + "bid the price " + bidPrice + " which is not a price higher than: "
                            + Auctionprice + "\n");

        } else {
            AuctionDetails.setPrice(bidPrice);
            AuctionDetails.bidder = Bidder;
            String BidderName = Bidder.getName();
            int BidderID = Bidder.getId();
            AllItems.put(itemID, AuctionDetails); // adds item with ID to hashtable to determine the winner
            // itemList.add(AuctionDetails); // adds an item to the list array of auction
            // items

            // Adds the current winner into the All Winners so it can display it.
            AllWinners.put(bidder, AuctionDetails);

            System.out
                    .println("Bidder: " + BidderName + " CLientID: " + BidderID + " has successfully bid " + bidPrice
                            + " for Auction "
                            + AuctionID +
                            " and that is the highest standard bid right now.");

        }

    }

    public void AuctionCloseBackend(int itemId, String status) {
        this.requestCount++;
        if (status.equals("closed")) {
            System.out.println("Auction " + itemId + " has been closed and the winner has been announced.");

        } else {
            System.out.println("Auction " + itemId + " remains open\n");
        }
    }

    // This retrives the current Auction items by checking the ArrayList
    public ArrayList<AuctionItem> getAuctionItemList() {
        this.requestCount++;
        ArrayList<AuctionItem> itemlist = new ArrayList<AuctionItem>();
        if (itemList.isEmpty()) {
            System.out.println("There are currently no auctions. ");
        } else {
            System.out.println("\nThe current auctions biddings are: \n");
            for (int i = 0; i < itemlist.size(); i++) {
                itemlist.get(i).getId();
                itemlist.get(i).getTitle();
                itemlist.get(i).getPrice();
                itemlist.get(i).getDesc();
            }
        }
        return itemList;
    }

    // This checks the current auction biddings in the list with their IDs.
    // Not sure if it shows the highest item yet.
    public Hashtable<Integer, AuctionItem> currentHighestBidsBackend() {
        this.requestCount++;
        System.out.println("Current Highest Auction Biddings:\n");
        for (AuctionItem item : AllItems.values()) {
            System.out.println("Highest bidding item is: " + item);

        }
        return AllItems;

    }

    // This method retrives an auction item ID by checking if it is in the itemlist
    // Array first.
    public AuctionItem getSpecBackend(int itemId, int clientId) {
        this.requestCount++;
        System.out.println("Auction Item created, the id is " + itemId);
        // iterates through a list and checks if it matches the itemID.
        for (int i = 0; i < itemList.size(); i++) {
            if ((itemList.get(i)).getId() == itemId) {
                return itemList.get(i);

            }
        }
        return null;
    }

    public static void main(String args[]) {
        new Backend();
    }

}
