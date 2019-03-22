package com.plivo.plivoaddressbook.screens.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.plivo.plivoaddressbook.BaseActivity;
import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.dagger2.DaggerViewComponent;
import com.plivo.plivoaddressbook.dagger2.ViewContext;
import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.utils.AlarmUtils;
import com.plivo.plivoaddressbook.utils.AlertUtils;
import com.plivo.plivoaddressbook.utils.Constants;
import com.plivo.plivoaddressbook.utils.NetworkUtils;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    AlertUtils alertUtils;

    @Inject
    AlarmUtils alarmUtils;

    @Inject
    NetworkUtils networkUtils;

    @BindView(R.id.username)
    AutoCompleteTextView usernameView;

    @BindView(R.id.password)
    EditText passwordView;

    @BindView(R.id.login_form)
    View progressView;

    @BindView(R.id.login_progress)
    View progressBar;

    @BindView(R.id.email_sign_in_button)
    Button loginButton;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerViewComponent.builder().viewContext(new ViewContext(this)).build().inject(this);
        viewModel = ViewModelProviders.
                of(this).get(LoginViewModel.class);

        viewModel.loginObserver().observe(this, success -> {
            if (success) {
                alarmUtils.setRepeatingAlarm();
                showProgressBar(false);
                dialScreen();
            } else {
                alertUtils.showToast("Logout");
                showLoginForm();
            }
            runOnUiThread(() -> {
                if (loginButton != null) {
                    loginButton.setEnabled(true);
                }
            });
        });

        if (viewModel.isUserLoggedIn()) {
            viewModel.reLogin();
        } else {
            showLoginForm();
        }
    }

    private void showLoginForm() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        usernameView.setText("sanity180521094254");
        passwordView.setText("12345");
        usernameView.requestFocus();
    }

    @OnClick(R.id.email_sign_in_button)
    public void onClickSignIn() {
        login();
    }

    @OnEditorAction(R.id.password)
    public boolean onEditorAction(int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
            login();
            return true;
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void login() {

        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            usernameView.setError(getString(R.string.error_invalid_email));
            focusView = usernameView;
            cancel = true;
        }

        // check nwk
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard();

            // retrieve FCM token & then login
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.d(TAG, newToken);
                viewModel.login(usernameView.getText().toString(), passwordView.getText().toString(), newToken);
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void dialScreen() {
        Log.d(TAG, "launch dialScreen()");
        startActivity(new Intent(this, DialActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(Constants.INCOMING_CALL_FROM_PUSH, getIntent().getBooleanExtra(Constants.INCOMING_CALL_FROM_PUSH, false))
        );
        finish();
    }

    private void showProgressBar(boolean show) {
        if (progressBar == null) return;

        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return !username.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

