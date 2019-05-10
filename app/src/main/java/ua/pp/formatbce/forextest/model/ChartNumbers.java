package ua.pp.formatbce.forextest.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import ua.pp.formatbce.forextest.misc.DataKind;
import ua.pp.formatbce.forextest.misc.EventDataUpdate;
import ua.pp.formatbce.forextest.misc.ISimpleListener;
import ua.pp.formatbce.forextest.misc.SubscriptionType;

public class ChartNumbers {

    @NonNull
    private final SubscriptionType type;
    @NonNull
    private final DataKind kind;
    @NonNull
    private final LineDataSet dataSet;
    private ISimpleListener listener;
    private long startTime;
    private long maxTime;

    public ChartNumbers(@NonNull SubscriptionType type, @NonNull DataKind kind, Context context) {
        this.type = type;
        this.kind = kind;
        dataSet = new LineDataSet(new ArrayList<>(), kind.name());
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setLineWidth(4f);
        dataSet.setCircleRadius(2f);
        dataSet.setColor(ContextCompat.getColor(context, kind.getColorRes()));
        EventBus.getDefault().register(this);
    }

    public void add(UpdateChunk chunk) {
        if (dataSet.getEntryCount() == 0) {
            startTime = chunk.time;
        }
        maxTime = chunk.time;
        dataSet.addEntry(kind.getEntryFrom(chunk, startTime));
    }

    public LineData getLineData() {
        return new LineData(dataSet);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getMaxTime() {
        return maxTime - startTime;
    }

    public void setDataUpdateListener(ISimpleListener listener) {
        this.listener = listener;
    }

    public void unsubscribe() {
        EventBus.getDefault().unregister(this);
        listener = null;
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 17 + kind.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChartNumbers)) {
            return false;
        }
        final ChartNumbers that = (ChartNumbers) obj;
        return that.type == type && that.kind == kind;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataUpdate(EventDataUpdate dataUpdate) {
        if (dataUpdate.type == type) {
            add(dataUpdate.chunk);
            if (listener != null) {
                listener.call();
            }
        }
    }
}
