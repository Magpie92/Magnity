package com.magpiehoon.magnity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.magpiehoon.magnity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by magpiehoon on 2017. 5. 28..
 */

public class AuthMagUi extends AppCompatActivity {
    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE";
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final int RC_SIGN_IN = 100;

    @BindView(R.id.root)
    View mRootView;

    @BindView(R.id.google_provider_radio)
    RadioButton mGoogleProvider;

    @BindView(R.id.email_provider_radio)
    RadioButton mEmailProvider;

    @BindView(R.id.sign_in)
    Button mSingIn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(SignedInActivity.createIntent(this, null));
            finish();
        }

        setContentView(R.layout.auth_ui_mag);
        ButterKnife.bind(this);

        if (!isGoogleConfigured()) {
            mGoogleProvider.setChecked(false);
            mGoogleProvider.setEnabled(false);
            mGoogleProvider.setText(R.string.google_label_missing_config);
        }

    }

    @OnClick(R.id.sign_in)
    public void signIn(View view) {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(AuthUI.getDefaultTheme())
                .setLogo(AuthUI.NO_LOGO)
                .setProviders(getSelectedProvider())
                .setTosUrl(FIREBASE_TOS_URL)
                .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (resultCode == ResultCodes.OK) {
            startActivity(SignedInActivity.createIntent(this, response));
            finish();
            return;
        } else {
            if (response == null) {
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }
            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }
        showSnackbar(R.string.unknown_sign_in_response);

    }

    private List<AuthUI.IdpConfig> getSelectedProvider() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        if (mGoogleProvider.isChecked()) {
            selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        }
        if (mEmailProvider.isChecked()) {
            selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
        }
        return selectedProviders;
    }

    private boolean isGoogleConfigured() {
        return !UNCHANGED_CONFIG_VALUE.equals(
                getString(R.string.default_web_client_id));
    }

    private void showSnackbar(int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthMagUi.class);
        return in;
    }


}
