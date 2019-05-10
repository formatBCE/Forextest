package ua.pp.formatbce.forextest.view.item;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ua.pp.formatbce.forextest.R;
import ua.pp.formatbce.forextest.misc.SubscriptionType;

public class ItemViewActivity extends MvpAppCompatActivity implements IItemView {

    public static final String EXTRA_TYPE = "subscription_type";
    @InjectPresenter
    ItemViewPresenter presenter;
    private RecyclerView recyclerView;

    @ProvidePresenter
    public ItemViewPresenter providePresenter() {
        return new ItemViewPresenter(getApplicationContext(), SubscriptionType.valueOf(getIntent().getStringExtra(EXTRA_TYPE)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TYPE));
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

}
