package com.plivo.plivoincomingcall;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected void showFragment(BaseFragment fragment) {
        runOnUiThread(() -> {
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getName())
                .commit();
                fragment.updateUi();
        });
    }

//    public void removeFragment(BaseFragment fragment) {
//        runOnUiThread(() ->
//                getSupportFragmentManager().beginTransaction()
//                        .remove(fragment)
//                        .commit());
//    }

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager()
//                    .popBackStackImmediate(getSupportFragmentManager().getBackStackEntryAt(0).getName(), 0);
//        } else {
//            super.onBackPressed();
//        }
//    }
}
