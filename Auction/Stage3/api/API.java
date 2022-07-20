package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.AuctionItem;
import shared.ClientData;

import java.util.Hashtable;

public interface API extends Remote {

    // public AuctionItem getSpec(int itemId, int clientId) throws RemoteException;

    public int createAuction(int itemId, String title, String desc, int Price, int reserve_price)
            throws RemoteException;

    public void bidAuction(ClientData bidder, int itemID, int Reserveprice, int price) throws RemoteException;

    public Hashtable<Integer, AuctionItem> currentHighestBids() throws RemoteException;

    public AuctionItem getSpec(int itemID, int clientID) throws RemoteException;

    public String getAuctionItem() throws RemoteException;

    public void AuctionClose(int ID, String status) throws RemoteException;

    public Hashtable<ClientData, AuctionItem> AuctionWinner() throws RemoteException;

}
