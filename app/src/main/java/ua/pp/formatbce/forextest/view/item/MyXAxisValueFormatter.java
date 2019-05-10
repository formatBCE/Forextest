package ua.pp.formatbce.forextest.view.item;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MyXAxisValueFormatter extends ValueFormatter {

    private final long startTime;

    MyXAxisValueFormatter(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date((long) value + startTime));
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

}
