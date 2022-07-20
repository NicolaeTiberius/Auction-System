package shared;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import api.API;

import shared.ClientData;
import frontend.Frontend;
import backend.Backend;

public class AuctionItem implements Serializable {

    public int itemId;
    public String itemTitle;
    public String itemDescription;
    public int clientId;
    public int itemPrice;
    public String status;
    public ClientData bidder;
    public int bidPrice;

    public HashMap<String, ClientData> biddersMap;
    public String Status;

    public String toString() {
        return "" +
                " Auction Item ID: " + getId() + " " +
                ",Auction Item Title: " + getTitle() + "  " +
                ", Auction Item Price: " + getPrice() + "" +
                ", Auction ClientID: " + getClientID() + "" +
                "\n";
    }

    public AuctionItem(int ID, String Title, String Description, int Price) throws RemoteException {
        this.itemId = ID;
        this.itemTitle = Title;
        this.itemDescription = Description;
        this.itemPrice = Price;
        status = "open";
        clientId++;
    }

    public void setAuctionStatus(int ID, String status) {
        Status = status;
    }

    public void closeAuction(int AuctionID) throws RemoteException {
        status = "closed";

    }

    public void setAuctionItemBid(ClientData bidder, int price) {
        biddersMap.put(bidder.getName(), bidder); // adds the bidder name to the list of bidders.
        bidPrice = price;

    }

    public String getStatus() throws RemoteException {
        return status;
    }

    public int getId() {
        return this.itemId;
    }

    public String getTitle() {
        return this.itemTitle;
    }

    public String getDesc() {
        return this.itemDescription;
    }

    public int getPrice() {
        return this.itemPrice;
    }

    public void setPrice(int price) {
        this.itemPrice = price;
    }

    public int getClientID() {
        return clientId;
    }

}
