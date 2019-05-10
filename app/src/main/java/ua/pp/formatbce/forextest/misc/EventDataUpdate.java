package ua.pp.formatbce.forextest.misc;

import android.support.annotation.NonNull;

import ua.pp.formatbce.forextest.model.UpdateChunk;

public class EventDataUpdate {
    @NonNull
    public final SubscriptionType type;
    @NonNull
    public final UpdateChunk chunk;

    public EventDataUpdate(@NonNull SubscriptionType type, @NonNull UpdateChunk chunk) {
        this.type = type;
        this.chunk = chunk;
    }
}
