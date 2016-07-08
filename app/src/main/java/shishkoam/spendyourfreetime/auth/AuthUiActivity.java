/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shishkoam.spendyourfreetime.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import shishkoam.spendyourfreetime.R;


public class AuthUiActivity extends Activity {

    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";

    private static final String GOOGLE_TOS_URL =
            "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL =
            "https://www.firebase.com/terms/terms-of-service.html";

    private static final int RC_SIGN_IN = 100;
    RadioButton mUseDefaultTheme;
    RadioButton mUseGreenTheme;
    RadioButton mUsePurpleTheme;
    CheckBox mUseEmailProvider;
    CheckBox mUseGoogleProvider;
    CheckBox mUseFacebookProvider;
    RadioButton mUseGoogleTos;
    RadioButton mUseFirebaseTos;
    Button mSignIn;
    View mRootView;
    RadioButton mFirebaseLogo;
    RadioButton mGoogleLogo;
    RadioButton mNoLogo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(SignedInActivity.createIntent(this));
            finish();
        }

        setContentView(R.layout.auth_ui_layout);
        mUseDefaultTheme = (RadioButton) findViewById(R.id.default_theme);
        mUseGreenTheme = (RadioButton) findViewById(R.id.green_theme);
        mUsePurpleTheme = (RadioButton) findViewById(R.id.purple_theme);
        mUseEmailProvider = (CheckBox) findViewById(R.id.email_provider);
        mUseGoogleProvider = (CheckBox) findViewById(R.id.google_provider);
        mUseFacebookProvider = (CheckBox) findViewById(R.id.facebook_provider);
        mUseGoogleTos = (RadioButton) findViewById(R.id.google_tos);
        mUseFirebaseTos = (RadioButton) findViewById(R.id.firebase_tos);
        mSignIn = (Button) findViewById(R.id.sign_in);
        mRootView = findViewById(android.R.id.content);
        mFirebaseLogo = (RadioButton) findViewById(R.id.firebase_logo);
        mGoogleLogo = (RadioButton) findViewById(R.id.google_logo);
        mNoLogo = (RadioButton) findViewById(R.id.no_logo);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });
        if (!isGoogleConfigured()) {
            mUseGoogleProvider.setChecked(false);
            mUseGoogleProvider.setEnabled(false);
            mUseGoogleProvider.setText(R.string.google_label_missing_config);
        }

        if (!isFacebookConfigured()) {
            mUseFacebookProvider.setChecked(false);
            mUseFacebookProvider.setEnabled(false);
            mUseFacebookProvider.setText(R.string.facebook_label_missing_config);
        }

        if (!isGoogleConfigured() || !isFacebookConfigured()) {
            showSnackbar(R.string.configuration_required);
        }
    }

    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getSelectedLogo())
                        .setProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .build(),
                RC_SIGN_IN);
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

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(SignedInActivity.createIntent(this));
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        if (mUseDefaultTheme.isChecked()) {
            return AuthUI.getDefaultTheme();
        }

        if (mUsePurpleTheme.isChecked()) {
            return R.style.PurpleTheme;
        }

        return R.style.GreenTheme;
    }

    @MainThread
    @DrawableRes
    private int getSelectedLogo() {
        if (mFirebaseLogo.isChecked()) {
            return R.drawable.firebase_auth_120dp;
        } else if (mGoogleLogo.isChecked()) {
            return R.drawable.logo_googleg_color_144dp;
        }
        return AuthUI.NO_LOGO;
    }

    @MainThread
    private String[] getSelectedProviders() {
        ArrayList<String> selectedProviders = new ArrayList<>();

        if (mUseEmailProvider.isChecked()) {
            selectedProviders.add(AuthUI.EMAIL_PROVIDER);
        }

        if (mUseFacebookProvider.isChecked()) {
            selectedProviders.add(AuthUI.FACEBOOK_PROVIDER);
        }

        if (mUseGoogleProvider.isChecked()) {
            selectedProviders.add(AuthUI.GOOGLE_PROVIDER);
        }

        return selectedProviders.toArray(new String[selectedProviders.size()]);
    }

    @MainThread
    private String getSelectedTosUrl() {
        if (mUseGoogleTos.isChecked()) {
            return GOOGLE_TOS_URL;
        }

        return FIREBASE_TOS_URL;
    }

    @MainThread
    private boolean isGoogleConfigured() {
        return !UNCHANGED_CONFIG_VALUE.equals(
                getResources().getString(R.string.default_web_client_id));
    }

    @MainThread
    private boolean isFacebookConfigured() {
        return !UNCHANGED_CONFIG_VALUE.equals(
                getResources().getString(R.string.facebook_application_id));
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }
}
