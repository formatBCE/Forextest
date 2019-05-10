package ua.pp.formatbce.forextest.model;

import com.google.gson.annotations.SerializedName;

public class SubscriptionMessage {

    @SerializedName("event")
    private String event;
    @SerializedName("channel")
    private String channel;
    @SerializedName("pair")
    private String type;
    @SerializedName("chanId")
    private Integer channelId;

    public SubscriptionMessage() {

    }

    public SubscriptionMessage(String type) {
        this.type = type;
        this.event = "subscribe";
        this.channel = "ticker";
    }

    public String getType() {
        return type;
    }

    public Integer getChannelId() {
        return channelId;
    }
}
