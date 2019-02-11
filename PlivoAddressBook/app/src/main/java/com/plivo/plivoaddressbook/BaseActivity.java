package com.plivo.plivoaddressbook;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoaddressbook.screens.dial.calls.OngoingCallFragment;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BaseActivity extends AppCompatActivity {

    protected Call currentCall;

    protected void showFragment(ViewGroup container, BaseFragment fragment) {
        runOnUiThread(() -> {
            if (container != null) {
                container.setVisibility(View.VISIBLE);
                Log.d(".anil", "showFragment: " + fragment.getName());
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

    protected void removeCurrentCallFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        BaseFragment callFragment = null;
        for (Fragment f : fragments) {
            if (f instanceof OngoingCallFragment || f instanceof IncomingCallFragment)
            callFragment = (BaseFragment) f;
        }
        Log.d(".anil", "removeCurrentCallFragment(): " + callFragment);
        getSupportFragmentManager().beginTransaction()
                .remove(callFragment)
                .commitAllowingStateLoss();
    }

    protected BaseFragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            Log.d(".anil", "getCurrentFragment(): " + fragments.get(fragments.size()-1));
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
