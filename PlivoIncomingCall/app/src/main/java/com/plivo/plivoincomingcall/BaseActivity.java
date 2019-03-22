package com.plivo.plivoincomingcall;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.OngoingCallFragment;

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
        if (getCurrentFragment() != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getCurrentFragment())
                    .commitAllowingStateLoss();
        }
    }

    protected void removeCurrentCallFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        BaseFragment callFragment = null;
        for (Fragment f : fragments) {
            if (f instanceof OngoingCallFragment || f instanceof IncomingCallFragment)
            callFragment = (BaseFragment) f;
        }

        if (callFragment != null) {
            callFragment.updateUi(currentCall);
            getSupportFragmentManager().beginTransaction()
                    .remove(callFragment)
                    .commitAllowingStateLoss();
        }
    }

    protected BaseFragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
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
