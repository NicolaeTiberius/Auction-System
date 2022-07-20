package shared;

import shared.AuctionItem;

import java.io.Serializable;
import frontend.Frontend;
import backend.Backend;
import api.API;

public class ClientData implements Serializable {

    public final String clientName;
    public final String clientEmail;
    public final int clientID;

    public String toString() {
        return "" +
                " Client Name: " + getName() + " " +
                ",Client Email: " + getEmail() + "  " +
                ", Client ID: " + getId() + "" +
                "\n";
    }

    public ClientData(String name, String email, int ID) {
        clientName = name;
        clientEmail = email;
        clientID = ID;
    }

    public String getName() {
        return clientName;
    }

    public String getEmail() {
        return clientEmail;
    }

    public int getId() {
        return clientID;
    }

    // add a method to possibly be used in other classes like retriveing only the
    // name or email or id

}