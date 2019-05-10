package ua.pp.formatbce.forextest.misc;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;

import ua.pp.formatbce.forextest.model.UpdateChunk;

public enum DataKind {
    BID(android.R.color.darker_gray) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.bid);
        }
    },
    BID_SIZE(android.R.color.black) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.bidSize);
        }
    },
    ASK(android.R.color.holo_orange_light) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.ask);
        }
    },
    ASK_SIZE(android.R.color.holo_purple) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.askSize);
        }
    },
    DAILY_CHANGE(android.R.color.holo_red_light) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.dailyChange);
        }
    },
    DAILY_CHANGE_PERCENT(android.R.color.holo_blue_bright) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.dailyChangePercent);
        }
    },
    LAST_PRICE(android.R.color.holo_blue_dark) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.lastPrice);
        }
    },
    VOLUME(android.R.color.holo_orange_dark) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.volume);
        }
    },
    HIGH(android.R.color.holo_red_dark) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.high);
        }
    },
    LOW(android.R.color.holo_green_dark) {
        @NonNull
        @Override
        public Entry getEntryFrom(UpdateChunk chunk, long startTime) {
            return new Entry(chunk.time - startTime, chunk.low);
        }
    };

    @ColorRes
    private final int color;

    DataKind(@ColorRes int color) {
        this.color = color;
    }

    @NonNull
    public abstract Entry getEntryFrom(UpdateChunk chunk, long startTime);

    @ColorRes
    public int getColorRes() {
        return color;
    }
}
