package ua.pp.formatbce.forextest.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ua.pp.formatbce.forextest.R;
import ua.pp.formatbce.forextest.model.Subscription;
import ua.pp.formatbce.forextest.view.item.ItemViewActivity;

public class MainActivity extends MvpAppCompatActivity implements IMainView {

    @InjectPresenter
    MainPresenter presenter;
    private RecyclerView recyclerView;
    private TextView tvNoContent;

    @ProvidePresenter
    public MainPresenter providePresenter() {
        return new MainPresenter(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> presenter.attemptToAddSubscription());
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvNoContent = findViewById(R.id.tv_no_content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final boolean watchingInBackground = presenter.isWatchingInBackground();
        menu.findItem(R.id.action_watch).setVisible(!watchingInBackground);
        menu.findItem(R.id.action_unwatch).setVisible(watchingInBackground);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_watch) {
            presenter.watchInBackground();
            return true;
        } else if (id == R.id.action_unwatch) {
            presenter.unwatchInBackground();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onWatchStatusChanged() {
        invalidateOptionsMenu();
    }

    @Override
    public void openDetailsFor(Subscription subscription) {
        Intent intent = new Intent(this, ItemViewActivity.class);
        intent.putExtra(ItemViewActivity.EXTRA_TYPE, subscription.getType().name());
        startActivity(intent);
    }

    @Override
    public void setAdapter(MainAdapter adapter) {
        recyclerView.setAdapter(adapter);
        adapter.setUpdateListener(this::refreshContentVisibility);
        refreshContentVisibility();
    }

    private void refreshContentVisibility() {
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int itemCount = adapter == null ? 0 : adapter.getItemCount();
        tvNoContent.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(itemCount > 0 ? View.VISIBLE : View.GONE);
    }
}
