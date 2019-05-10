package ua.pp.formatbce.forextest.view.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ua.pp.formatbce.forextest.R;
import ua.pp.formatbce.forextest.model.ChartNumbers;

class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    ChartAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    private final List<ChartNumbers> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chart_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fillWith(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void fill(Collection<ChartNumbers> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    void clear() {
        for (ChartNumbers item : items) {
            item.unsubscribe();
        }
        items.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final LineChart chart;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = (LineChart) itemView;
            chart.getDescription().setEnabled(false);
        }

        private void fillWith(ChartNumbers item) {
            item.setDataUpdateListener(() -> itemView.post(() -> notifyItemChanged(items.indexOf(item))));
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new MyXAxisValueFormatter(item.getStartTime()));
            xAxis.setAxisMaximum(item.getMaxTime());
            xAxis.setAxisMinimum(0);
            chart.setData(item.getLineData());
            chart.notifyDataSetChanged();
        }
    }
}
