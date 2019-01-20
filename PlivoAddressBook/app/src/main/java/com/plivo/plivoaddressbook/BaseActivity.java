package com.plivo.plivoaddressbook;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.model.Call;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BaseActivity extends AppCompatActivity {

    protected Call currentCall;

    protected void showFragment(ViewGroup container, BaseFragment fragment) {
        runOnUiThread(() -> {
            if (container != null) {
                container.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(container.getId(), fragment, fragment.getName())
                        .commit();
                fragment.updateUi(currentCall);
            }
        });
    }

    protected void removeCurrentFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(getCurrentFragment())
                .commitAllowingStateLoss();
    }

    protected BaseFragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            return (BaseFragment) fragments.get(fragments.size()-1);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
