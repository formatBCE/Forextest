package ua.pp.formatbce.forextest.view.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.formatbce.forextest.R;
import ua.pp.formatbce.forextest.misc.IItemListener;
import ua.pp.formatbce.forextest.misc.ISimpleListener;
import ua.pp.formatbce.forextest.misc.SubscriptionType;
import ua.pp.formatbce.forextest.model.Subscription;
import ua.pp.formatbce.forextest.model.UpdateChunk;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    @NonNull
    private final List<Subscription> items = new ArrayList<>();
    @NonNull
    private final IItemListener<Subscription> clickListener;
    @NonNull
    private final IItemListener<Subscription> cancelListener;
    @Nullable
    private ISimpleListener updateListener;

    MainAdapter(@NonNull IItemListener<Subscription> clickListener, @NonNull IItemListener<Subscription> cancelListener) {
        this.clickListener = clickListener;
        this.cancelListener = cancelListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subscr_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Subscription item = items.get(i);
        viewHolder.fillWith(item, clickListener, cancelListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void addItem(SubscriptionType type) {
        final Subscription item = new Subscription(type);
        if (!items.contains(item)) {
            item.subscribe();
            items.add(item);
            notifyItemInserted(items.size() - 1);
            if (updateListener != null) {
                updateListener.call();
            }
        }
    }

    void removeItem(SubscriptionType type) {
        Subscription item = new Subscription(type);
        final int index = items.indexOf(item);
        if (index >= 0) {
            item = items.get(index);
            item.unsubscribe();
            items.remove(index);
            notifyItemRemoved(index);
            if (updateListener != null) {
                updateListener.call();
            }
        }
    }

    void setUpdateListener(@Nullable ISimpleListener listener) {
        updateListener = listener;
    }

    Subscription getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Button btnCancel;
        private final TextView title;
        private final TextView bid;
        private final TextView bidSize;
        private final TextView ask;
        private final TextView askSize;
        private final TextView dailyCharge;
        private final TextView dailyChargePerc;
        private final TextView lastPrice;
        private final TextView volume;
        private final TextView high;
        private final TextView low;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            title = itemView.findViewById(R.id.tv_title);
            bid = itemView.findViewById(R.id.tv_bid);
            bidSize = itemView.findViewById(R.id.tv_bid_size);
            ask = itemView.findViewById(R.id.tv_ask);
            askSize = itemView.findViewById(R.id.tv_ask_size);
            dailyCharge = itemView.findViewById(R.id.tv_daily_charge);
            dailyChargePerc = itemView.findViewById(R.id.tv_daily_charge_perc);
            lastPrice = itemView.findViewById(R.id.tv_last_price);
            volume = itemView.findViewById(R.id.tv_volume);
            high = itemView.findViewById(R.id.tv_high);
            low = itemView.findViewById(R.id.tv_low);
        }

        private void fillWith(
                @NonNull Subscription item,
                @NonNull IItemListener<Subscription> clickListener,
                @NonNull IItemListener<Subscription> cancelListener) {
            itemView.setOnClickListener(v -> clickListener.call(item));
            btnCancel.setOnClickListener(v -> cancelListener.call(item));
            title.setText(item.getType().name());
            item.setDataUpdateListener(() -> fillFields(item.getLatestData()));
            fillFields(item.getLatestData());
        }

        private void fillFields(@Nullable UpdateChunk data) {
            bid.setText(data == null ? "-" : String.valueOf(data.bid));
            bidSize.setText(data == null ? "-" : String.valueOf(data.bidSize));
            ask.setText(data == null ? "-" : String.valueOf(data.ask));
            askSize.setText(data == null ? "-" : String.valueOf(data.askSize));
            dailyCharge.setText(data == null ? "-" : String.valueOf(data.dailyChange));
            dailyChargePerc.setText(data == null ? "-" : String.valueOf(data.dailyChangePercent));
            lastPrice.setText(data == null ? "-" : String.valueOf(data.lastPrice));
            volume.setText(data == null ? "-" : String.valueOf(data.volume));
            high.setText(data == null ? "-" : String.valueOf(data.high));
            low.setText(data == null ? "-" : String.valueOf(data.low));
        }
    }
}
