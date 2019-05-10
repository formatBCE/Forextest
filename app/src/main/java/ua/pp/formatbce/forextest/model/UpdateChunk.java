package ua.pp.formatbce.forextest.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

public class UpdateChunk {
    public final long time;
    public final float bid;
    public final float bidSize;
    public final float ask;
    public final float askSize;
    public final float dailyChange;
    public final float dailyChangePercent;
    public final float lastPrice;
    public final float volume;
    public final float high;
    public final float low;

    private UpdateChunk(
            long time,
            float bid,
            float bidSize,
            float ask,
            float askSize,
            float dailyChange,
            float dailyChangePercent,
            float lastPrice,
            float volume,
            float high,
            float low) {
        this.time = time;
        this.bid = bid;
        this.bidSize = bidSize;
        this.ask = ask;
        this.askSize = askSize;
        this.dailyChange = dailyChange;
        this.dailyChangePercent = dailyChangePercent;
        this.lastPrice = lastPrice;
        this.volume = volume;
        this.high = high;
        this.low = low;
    }

    @NonNull
    public static UpdateChunk from(JSONArray array) throws JSONException {
        return new UpdateChunk(
                System.currentTimeMillis(),
                (float) array.getDouble(1),
                (float) array.getDouble(2),
                (float) array.getDouble(3),
                (float) array.getDouble(4),
                (float) array.getDouble(5),
                (float) array.getDouble(6),
                (float) array.getDouble(7),
                (float) array.getDouble(8),
                (float) array.getDouble(9),
                (float) array.getDouble(10)
        );
    }

    public UpdateChunk copyWithCurrentTime() {
        return new UpdateChunk(
                System.currentTimeMillis(),
                bid,
                bidSize,
                ask,
                askSize,
                dailyChange,
                dailyChangePercent,
                lastPrice,
                volume,
                high,
                low
        );
    }
}
