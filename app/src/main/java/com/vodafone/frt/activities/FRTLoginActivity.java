package com.vodafone.frt.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.LanguageAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTButtonTrebuchetMS;
import com.vodafone.frt.fonts.FRTEditTextTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRRequestModelLogin;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.Config;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by vishal
 */
public class FRTLoginActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        FRTAsyncCommon.FetchDataCallBack {
    boolean check = false;
    private FRTButtonTrebuchetMS loginButton;
    private final FRTLoginActivity frtLoginActivity = this;
    private FRTConstants frtConstants;
    private FRTUtility frtUtility;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTEditTextTrebuchetMS userEdit, passEdit;
    private ProgressDialog progressDialog;
    @SuppressWarnings("deprecation")
    private FRTSharePrefUtil frtSharePrefUtil;
    Intent intent;
    private ImageView applanguage;
    private ListView languagelist;
    private String uname, upass, lang;
    private FRTWEBAPI frtWEBAPI;
    private String fcmToken;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog pDialog;
    private byte[] userNameByte;
    private byte[] userPasswordByte;
    private byte[] userNameSHA512;
    private byte[] userPassSHA512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(FRTLoginActivity.this);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtlogin);

        callBackSetUp();

        // activityStartupSetup();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.d(this.getClass().getName(), "FCM_MESAGE" + message);
                }
            }
        };
        displayFirebaseRegId();
        appPermissions();

        if (intent.hasExtra("keyLogoutMesage") && !TextUtils.isEmpty(intent.getStringExtra("keyLogoutMesage")))
            frtUtility.setSnackBar(intent.getStringExtra("keyLogoutMesage"), loginButton);

    }

    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtLoginActivity;
        frtCallBackForIdFind = frtLoginActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtLoginActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initAllViews() {
        loginButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.LoginButton);
        userEdit = (FRTEditTextTrebuchetMS) frtCallBackForIdFind.view(R.id.AccountEditText);
        passEdit = (FRTEditTextTrebuchetMS) frtCallBackForIdFind.view(R.id.PasswordEditText);
        applanguage = (ImageView) frtCallBackForIdFind.view(R.id.applanguage);
//        FrtFaIcon frtFaIcon=new FrtFaIcon();
//        applanguage.setTypeface(frtFaIcon.getTypeface(frtLoginActivity));
        FRTTextviewTrebuchetMS versionTextView = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.versionTextViwe);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        frtConstants = new FRTConstants();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtLoginActivity);
        progressDialog = new ProgressDialog(FRTLoginActivity.this);
        frtUtility.settingStatusBarColor(frtLoginActivity, R.color.colorPrimary);
        frtUtility.hideSoftKeyboard(userEdit, frtLoginActivity);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtLoginActivity);
        frtWEBAPI = new FRTWEBAPI();
        versionTextView.setText(frtUtility.getAppVersion());
        intent = getIntent();
        if (frtConnectivityReceiver.isConnected(frtLoginActivity)) {
            if (frtSharePrefUtil.getBoolean(frtConstants.IS_LOGGED_IN)) {
                validateUser(loginButton);
            }
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.LoginLayout));
        }
        // Log.d(this.getClass().getName(),"NETWORK_TYPE" + CommonUtils.getNetworkType(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        frtUtility.hideSoftKeyboard(userEdit, frtLoginActivity);
        passEdit.setText("");
        userEdit.setText("");
        passEdit.setCursorVisible(false);
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        passEdit.setOnEditorActionListener(onFinishEditon);
        userEdit.setOnEditorActionListener(onNextEditon);
        loginButton.setOnClickListener(cqLoginClick);
        userEdit.setOnClickListener(onClickuseredit);
        passEdit.setOnClickListener(onClickpassedit);
        applanguage.setOnClickListener(onClickChnageLanguage);
    }

    private final View.OnClickListener onClickChnageLanguage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog dialog = new Dialog(frtLoginActivity);
            dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_language);
            final FRTTextviewTrebuchetMS change = dialog.findViewById(R.id.yes);
            FRTTextviewTrebuchetMS notchange = dialog.findViewById(R.id.no);
            FRTTextviewTrebuchetMS hindiTextView = dialog.findViewById(R.id.hindiTextView);
            FRTTextviewTrebuchetMS englishTextView = dialog.findViewById(R.id.englishTextView);
            frtUtility.logInfo(frtLoginActivity,"clicked on --> Chnage Language ",frtConstants);
            englishTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            languagelist = dialog.findViewById(R.id.listlang);
            LanguageAdapter langAdpt = new LanguageAdapter(frtLoginActivity);
            langAdpt.setDataSet(Arrays.asList(new String[]{"English", "Hindi"}));
            languagelist.setAdapter(langAdpt);
            frtSharePrefUtil.setString(getString(R.string.langstring), lang);
            languagelist.setSelection(0);

            final String currentPhoneLanguage = getResources().getConfiguration().locale.getLanguage();// app specific language
            //languagelist.setBackgroundColor(0);
            if (currentPhoneLanguage.equalsIgnoreCase("en")){
               //languagelist.setSelector(R.color.colorPrimary);

                //languagelist.setSelection(0);

                languagelist = dialog.findViewById(R.id.listlang);
                langAdpt.setDataSet(Arrays.asList(new String[]{"English (Selected)", "Hindi"}));
                languagelist.setAdapter(langAdpt);
                frtSharePrefUtil.setString(getString(R.string.langstring), lang);
                languagelist.setSelection(0);
         }
            else {
                //languagelist.setSelection(1);
              //  languagelist.setBackgroundColor(getResources().getColor(R.color.white));

                langAdpt.setDataSet(Arrays.asList(new String[]{"English ", "Hindi (Selected)"}));
                languagelist.setAdapter(langAdpt);
                frtSharePrefUtil.setString(getString(R.string.langstring), lang);
                languagelist.setSelection(0);
              }

            final Dialog finalDialog = dialog;
            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(lang!=null) {
                        if (!lang.equals(currentPhoneLanguage)) {
                            frtUtility.setLocale(frtLoginActivity, lang, frtLoginActivity, true);
                            frtUtility.setSnackBar(getString(R.string.langchangesuccess), loginButton);
                            frtUtility.logInfo(frtLoginActivity,"changed language to "+lang,frtConstants);
                            finalDialog.dismiss();
                        } else {
                            frtUtility.setSnackBar(getString(R.string.langchangefailure), loginButton);
                        }
                    }
                    else{
                        frtUtility.setSnackBar(getString(R.string.selectlanguage), loginButton);
                    }
                }
            });
            notchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalDialog.dismiss();
                }
            });
            languagelist.setOnItemClickListener(onClickLanguage);
            dialog.show();
        }
    };
    private AdapterView.OnItemClickListener onClickLanguage = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {


            switch (pos) {
                case 0:
                    lang = "en";
                    break;
                case 1:
                    lang = "hi";
                    break;
            }
            view.setSelected(true);
            frtSharePrefUtil.setString(getString(R.string.langstring), lang);
        }
    };
    private final View.OnClickListener onClickuseredit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userEdit.setFocusable(true);
            userEdit.setFocusableInTouchMode(true);
            frtUtility.showSoftKeyboard(userEdit, frtLoginActivity);
        }
    };
    private final View.OnClickListener onClickpassedit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            passEdit.setCursorVisible(true);
            passEdit.setFocusable(true);
            passEdit.setFocusableInTouchMode(true);
            frtUtility.showSoftKeyboard(passEdit, frtLoginActivity);
        }
    };

    private final TextView.OnEditorActionListener onFinishEditon = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (frtConnectivityReceiver.isConnected(frtLoginActivity)) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    frtUtility.hideSoftKeyboard(passEdit, frtLoginActivity);
                    validateUser(v);
                }
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), v);
            }
            return true;
        }
    };
    private final TextView.OnEditorActionListener onNextEditon = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                passEdit.setFocusable(true);
                passEdit.setFocusableInTouchMode(true);
                passEdit.setCursorVisible(true);
                frtUtility.showSoftKeyboard(passEdit, frtLoginActivity);
            }
            return true;
        }
    };
    private final View.OnClickListener cqLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtLoginActivity)) {
                validateUser(view);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        fcmToken = pref.getString("regId", null);

        Log.e("TAG", "Firebase token id: " + fcmToken);
    }

    private void validateUser(View view) {
        uname = userEdit.getText().toString().trim();
        upass = passEdit.getText().toString().trim();
        userNameByte = uname.getBytes();
        // userPasswordByte = upass.getBytes();
        if (TextUtils.isEmpty(uname) && TextUtils.isEmpty(upass)) {
            frtUtility.setSnackBar(getString(R.string.usernamepasserror), view);
        } else if (TextUtils.isEmpty(uname)) {
            frtUtility.setSnackBar(getString(R.string.usernameerror), view);
        } else if (TextUtils.isEmpty(upass)) {
            frtUtility.setSnackBar(getString(R.string.passworderror), view);
        } else {
            frtUtility.logInfo(frtLoginActivity, "Clicked on --> Login", frtConstants);
            startNavigationActivity();
        }
    }

    @SuppressWarnings("deprecation")
    private void startNavigationActivity() {
        try {
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        frtSharePrefUtil.setString(frtConstants.USERNAME_KEY, uname);
        @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestModelLogin> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpLoginModel());
        frtAsyncCommon.setContext(frtLoginActivity);
        frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.LOGIN), "login");
        frtAsyncCommon.setFetchDataCallBack(frtLoginActivity);
    }

    private PTRRequestModelLogin setUpLoginModel() {

        String string = frtUtility.getAppVersion();
        String[] parts = string.split(" ");
        String part2 = parts[1];

        PTRRequestModelLogin cqRequestModelLogin = new PTRRequestModelLogin();
        cqRequestModelLogin.setUsername(frtUtility.getBase64FromString(uname));
        cqRequestModelLogin.setPassword(frtUtility.getBase64FromString(upass));
        cqRequestModelLogin.setGrant_type(getString(R.string.grant_type));
        cqRequestModelLogin.setOsName(getString(R.string.osname));
        cqRequestModelLogin.setOsVersion(frtUtility.getSdkBuildVersionNumber());
        cqRequestModelLogin.setMacAddress(frtUtility.getMacAddress());
        cqRequestModelLogin.setFcmkey(fcmToken);
        cqRequestModelLogin.setOsType("Android");
        cqRequestModelLogin.setAppVersion("v"+part2/*"v1.2"*/);
        return cqRequestModelLogin;
    }

    @Override
    public void getAsyncData(String response, String type) {
        Log.d(this.getClass().getName(), "LOGIN_RESPONSE" + response);
        if (response != null) {
            if (type.equals("login")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("error").equalsIgnoreCase("REQUEST_DENIED")) {
                        frtUtility.setSnackBar(jsonObject.optString("error_description"), loginButton);
                    }
                    else {
                        frtSharePrefUtil.setString(getString(R.string.token_key), jsonObject.optString(getString(R.string.token_key)));
                        frtSharePrefUtil.setString(getString(R.string.tokentype_key), jsonObject.optString(getString(R.string.tokentype_key)));
                        frtSharePrefUtil.setString(getString(R.string.tokenexpiretime_key), String.valueOf(jsonObject.optInt(getString(R.string.tokenexpiretime_key), 0)));
                        frtSharePrefUtil.setString(getString(R.string.refresh_token_key), jsonObject.optString(getString(R.string.refresh_token_key)));
                        frtSharePrefUtil.setString(getString(R.string.globalsettings_key), jsonObject.optString(getString(R.string.globalsettings_key)));
                        frtSharePrefUtil.setString("currenttime", frtUtility.getCurrentTime());
                        JSONObject jsonObject1 = new JSONObject(jsonObject.optString(getString(R.string.globalsettings_key)));
                        JSONArray jsonArray = jsonObject1.optJSONArray("globalSetting");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                            if (i == 0)
                                try {
                                    frtSharePrefUtil.setString(getString(R.string.tracktime), Long.valueOf(jsonObject2.optString("value")) * 1000 + "");
                                } catch (Exception ignored) {
                                    Log.e(frtLoginActivity.getClass().getName(), ignored.toString());
                                }
                            if (i == 1)
                                try {
                                    frtSharePrefUtil.setString(getString(R.string.Bufferlimit), Long.valueOf(jsonObject2.optString("value")) + "");
                                } catch (Exception ignored) {
                                    Log.e(frtLoginActivity.getClass().getName(), ignored.toString());
                                }
                            if (i == 2)
                                try {
                                    frtSharePrefUtil.setString(getString(R.string.uploaddocumentsize), Long.valueOf(jsonObject2.optString("value")) + "");
                                } catch (Exception ignored) {
                                    Log.e(frtLoginActivity.getClass().getName(), ignored.toString());
                                }
                        }
                        if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                            frtSharePrefUtil.setBoolean(frtConstants.IS_LOGGED_IN, true);
                            Intent intent = new Intent(frtLoginActivity, PTRNavigationScreenActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                            frtSharePrefUtil.setBoolean("isLogout", false);
                        }
                    /*if (!jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK") && jsonObject.op) {
                    }*/
                        else {
                            // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), loginButton);
                            frtUtility.setSnackBar(response, loginButton);
                        }
                    }
                    //  progressDialog.dismiss();
                    pDialog.dismiss();
                } catch (JSONException ignored) {
                    Log.e(frtLoginActivity.getClass().getName(), ignored.toString());
                }
            }
        }/*else if (response.equalsIgnoreCase("null")){
            progressDialog.dismiss();
            frtUtility.setSnackBar(getString(R.string.userpasserror), loginButton);
        }*/ else {
            pDialog.dismiss();
            // progressDialog.dismiss();
            frtUtility.setSnackBar(getString(R.string.userpasserror), loginButton);
        }
    }

    @Override
    public void onBackPressed() {
        frtUtility.logInfo(frtLoginActivity, "Clicked --> Back", frtConstants);
        frtUtility.exitApp();
    }


    private void activityStartupSetup() {
        if (check) {
            callBackSetUp();
            check = false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pDialog.dismiss();
    }

    /**
     * This method is used for authorising necessary permissions for initializing google map android
     */
    @SuppressLint("InlinedApi")
    private void appPermissions() {
        frtConstants = new FRTConstants();
        if (ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtLoginActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(frtLoginActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.CAMERA}, frtConstants.LOCATION_PERMISSION);
        } /*else if () {
            ActivityCompat.requestPermissions(frtLoginActivity, new String[]{Manifest.permission.CAMERA}, frtConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }*/ else {
            // initializeScreen();
            // Toast.makeText(frtLoginActivity, "Please accept permission", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Activity callback method
     *
     * @param requestCode  permission request code
     * @param permissions  permissions
     * @param grantResults grant results array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (requestCode == frtConstants.LOCATION_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    check = true;
                } /*else {
                    //frtUtility.setSnackBar(getString(R.string.sufficient_permissions), loginButton);
                   // finish();
                }*/
            }
        } catch (Exception ignored) {
        }
    }

}