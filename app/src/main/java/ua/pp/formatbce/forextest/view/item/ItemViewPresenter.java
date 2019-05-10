package ua.pp.formatbce.forextest.view.item;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import ua.pp.formatbce.forextest.misc.DataKind;
import ua.pp.formatbce.forextest.misc.SubscriptionType;
import ua.pp.formatbce.forextest.model.ChartNumbers;
import ua.pp.formatbce.forextest.model.UpdateChunk;
import ua.pp.formatbce.forextest.service.MainService;

@InjectViewState
public class ItemViewPresenter extends MvpPresenter<IItemView> {

    @NonNull
    private final Context appContext;
    private final SubscriptionType type;
    private ChartAdapter adapter;
    @NonNull
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fillAdapterWith(((MainService.Binder) service).getService().getDataFor(type));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public ItemViewPresenter(@NonNull Context appContext, SubscriptionType type) {
        this.appContext = appContext;
        this.type = type;
        adapter = new ChartAdapter();
        appContext.bindService(new Intent(appContext, MainService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void fillAdapterWith(@NonNull List<UpdateChunk> data) {
        List<ChartNumbers> items = new ArrayList<>();
        for (DataKind kind : DataKind.values()) {
            items.add(new ChartNumbers(type, kind, appContext));
        }
        for (UpdateChunk chunk : data) {
            for (ChartNumbers item : items) {
                item.add(chunk);
            }
        }
        adapter.fill(items);

    }

    @Override
    public void attachView(IItemView view) {
        super.attachView(view);
        view.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        appContext.unbindService(connection);
        adapter.clear();
        super.onDestroy();
    }

}
