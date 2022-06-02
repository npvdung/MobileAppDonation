package com.example.models;

public class Donation {
    public String _id;
    public String method;
    public int amount;
    public int upvotes;

    public Donation() {
        this.amount = 0;
        this.method = "";
        this.upvotes = 0;
    }

    public Donation(String method, int amount, int upvotes) {
        this.amount = amount;
        this.method = method;
        this.upvotes = upvotes;
    }

    public String toString() {
        return this._id + "," + this.amount + ", " + this.method + ", " + upvotes;
    }
}
