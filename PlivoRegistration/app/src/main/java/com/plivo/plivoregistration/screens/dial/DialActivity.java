package com.plivo.plivoregistration.screens.dial;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.plivo.plivoregistration.R;
import com.plivo.plivoregistration.dagger2.DaggerViewComponent;
import com.plivo.plivoregistration.dagger2.ViewContext;
import com.plivo.plivoregistration.utils.AlertUtils;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DialActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    AlertUtils alertUtils;

    DialViewModel dialViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        ButterKnife.bind(this);
        dialViewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        DaggerViewComponent.builder().viewContext(new ViewContext(this)).build().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return true;
        }
    }

    private void logout() {
        dialViewModel.logout().observe(this, object -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        alertUtils.showAlertTwoButton("Logout",
                "Do you want to logout?",
                getString(android.R.string.yes),
                (dialog, which) -> {
                    // positive button click
                    logout();
                },
                getString(android.R.string.cancel),
                null
        );
    }
}
