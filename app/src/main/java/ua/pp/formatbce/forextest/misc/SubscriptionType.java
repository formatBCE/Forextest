package ua.pp.formatbce.forextest.misc;

import com.google.gson.Gson;

import ua.pp.formatbce.forextest.model.SubscriptionMessage;

public enum SubscriptionType {
    BTCUSD, BTCEUR;

    public String getSubscriptionMessage() {
        return new Gson().toJson(new SubscriptionMessage(this.name()));
    }
}
