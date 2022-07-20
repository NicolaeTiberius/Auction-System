package frontend;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;
import api.API;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import utility.GroupUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import shared.AuctionItem;
import shared.ClientData;

public class Frontend extends UnicastRemoteObject implements API, MembershipListener {

    public final String SERVER_NAME = "Auctionserver";
    public final int REGISTRY_PORT = 1099;

    private JChannel groupChannel;
    private RpcDispatcher dispatcher;

    private final int DISPATCHER_TIMEOUT = 1000;

    public Frontend() throws RemoteException {
        // Connect to the group (channel)
        this.groupChannel = GroupUtils.connect();
        if (this.groupChannel == null) {
            System.exit(1); // error to be printed by the 'connect' function
        }

        // Bind this server instance to the RMI Registry
        this.bind(this.SERVER_NAME);

        // Make this instance of Frontend a dispatcher in the channel (group)
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);
        this.dispatcher.setMembershipListener(this);

    }

    private void bind(String serverName) {
        try {
            Registry registry = LocateRegistry.createRegistry(this.REGISTRY_PORT);
            registry.rebind(serverName, this);
            System.out.println("✅    rmi server running...");
        } catch (Exception e) {
            System.err.println("🆘    exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Object voting(List list) {
        int responses = 0;
        int count = 1;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) == list.get(responses)) {
                count++;
            } else {
                count--;
            }
            if (count == 0) {
                responses = 1;
                count = 1;
            }
        }
        return list.get(responses);
    }

    @Override
    public AuctionItem getSpec(int itemID, int clientID) throws RemoteException {
        try {
            // List<AuctionItem> list = new ArrayList<>();
            RspList<AuctionItem> responses = this.dispatcher.callRemoteMethods(null,
                    "getSpecBackend",
                    new Object[] { itemID, clientID }, new Class[] { int.class, int.class },
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("Received %d responses: getSpec\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }
            // return the Auction Id by iterating through the list of responses.
            // list = (List) responses;
            // // do the voting of the list
            // voting(list);

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Hashtable<ClientData, AuctionItem> AuctionWinner() throws RemoteException {

        // List<Hashtable<ClientData, AuctionItem>> list = new ArrayList<>();

        Hashtable<ClientData, AuctionItem> Winner = new Hashtable<>();
        try {
            RspList<Hashtable<ClientData, AuctionItem>> responses = this.dispatcher.callRemoteMethods(null,
                    "AuctionWinnerBackend",
                    new Object[] {}, new Class[] {},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("Received %d responses: return Auction Winner\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }
            // return the winner by iterating through the list of responses.

            // list = responses.getResults();
            // voting(list);
            Winner = responses.getFirst();
            return Winner;

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void AuctionClose(int itemId, String status) throws RemoteException {
        try {
            RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "AuctionCloseBackend",
                    new Object[] { itemId, status }, new Class[] { int.class, String.class },
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("Received %d responses: Close Auction\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }
            // return a response
            responses.getResults();

            System.out.printf("Auction status: %s", status);
        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }

    }

    @Override
    public Hashtable<Integer, AuctionItem> currentHighestBids() throws RemoteException {

        Hashtable<Integer, AuctionItem> HighestBids = new Hashtable<>();
        try {
            RspList<Hashtable<Integer, AuctionItem>> responses = this.dispatcher.callRemoteMethods(null,
                    "currentHighestBidsBackend", new Object[] {}, new Class[] {},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("Received %d responses:  HighestBids\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }
            // return the highest bids by iterating through the list.
            HighestBids = responses.getFirst();
            return HighestBids;

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int createAuction(int itemId, String title, String desc, int Price, int reserve_price)
            throws RemoteException {

        try {
            RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "createAuctionBackend",
                    new Object[] { itemId, title, desc, Price, reserve_price },
                    new Class[] { int.class, String.class, String.class, int.class, int.class },
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("Received %d responses: createAuction\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }

            // Return an item by iterating through them all

            responses.getFirst();
            return itemId;

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }
        return itemId;

    }

    @Override
    public void bidAuction(ClientData bidder, int itemID, int Reserveprice, int price) throws RemoteException {
        try {
            RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "bidAuctionBackend",
                    new Object[] { bidder, itemID, Reserveprice, price },
                    new Class[] { ClientData.class, int.class, int.class, int.class },
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("Received %d responses: bidAuction\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }
            responses.getResults();
            System.out.println(
                    "A new Client: " + bidder + " ...bid the price: " + price + " on ItemID: " + itemID + "\n");

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }

    }

    @Override
    public String getAuctionItem() throws RemoteException {

        String result = "";

        try {
            RspList<ArrayList<AuctionItem>> responses = this.dispatcher.callRemoteMethods(null, "getAuctionItemList",
                    new Object[] {}, new Class[] {},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("Received %d responses:  getAuction\n", responses.size());
            if (responses.isEmpty()) {
                System.out.println("0 Responses received so far. ");
            }

            for (ArrayList<AuctionItem> itemList : responses.getResults()) {
                result += itemList;

            }
            return result;

            // System.out.println("The current values in itemList is:" + itemList);

        } catch (Exception e) {
            System.err.println("dispatcher exception");
            e.printStackTrace();
        }
        return null;
    }

    public void viewAccepted(View newView) {
        System.out.printf("👀    jgroups view changed\n✨    new view: %s\n", newView.toString());
    }

    public void suspect(Address suspectedMember) {
        System.out.printf("👀    jgroups view suspected member crash: %s\n", suspectedMember.toString());
    }

    public void block() {
        System.out.printf("👀    jgroups view block indicator\n");
    }

    public void unblock() {
        System.out.printf("👀    jgroups view unblock indicator\n");
    }

    public static void main(String args[]) {
        try {
            new Frontend();
        } catch (RemoteException e) {
            System.err.println("🆘    remote exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

}
