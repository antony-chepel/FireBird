package com.app.thefirebirdoffreedom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.AutofillManager;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private SharedPreferences preferences = null;
    private FirebaseFirestore db;
    private String ONESIGNAL = "";
    private String APPSLFYER = "";
    private String DEEPLINK = "";
    private String cam_file_data = null;
    private ValueCallback<Uri> file_data;
    private ValueCallback<Uri[]> file_path;
    private static String file_type = "*/*";
    private boolean multiple_files = true;
    private boolean loadingFinished = true;
    private boolean redirect = false;
    WebView MainWebView;
    private ImageView SplashView;
    private final static int file_req_code = 1;
    private ProgressBar loadingBar;
    private RelativeLayout loadingPanel;
    Handler handler = new Handler();
    private String adid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        adid = UUID.randomUUID().toString();
        preferences.edit().putString("google_id", adid).apply();
        initScreen();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        db = FirebaseFirestore.getInstance();
        DocumentReference onesignal_key = db.collection("services").document("keys");
        onesignal_key.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot keys = task.getResult();
                if (keys != null && keys.exists()) {
                    Map<String, Object> key_value = keys.getData();
                    if (key_value != null) {
                        ONESIGNAL = (String) key_value.get("onesignal");
                        APPSLFYER = (String) key_value.get("appsflyer");

                        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

                            @Override
                            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                                for (String attrName : conversionData.keySet()) {
                                    Log.d("LOG_TAG", "APPSFLYER: " + conversionData);

                                    if(conversionData.get("af_status") == null) {
                                        AppLinkData.fetchDeferredAppLinkData(MainActivity.this,
                                                new AppLinkData.CompletionHandler() {
                                                    @Override
                                                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                                                        if (appLinkData != null) {
                                                            Bundle DEEPLINK = appLinkData.getArgumentBundle();
                                                            Log.i("DEBUG_FACEBOOK_SDK", DEEPLINK.toString());
                                                            String campaing_name = ""+ DEEPLINK;

                                                            if(campaing_name.contains("_"))
                                                            {

                                                                String[] separated = campaing_name.split("_");
                                                                Log.d("LOG_TAG", "Facebook data: attribute: " + separated.length);

                                                                if(separated.length > 0)
                                                                    preferences.edit().putString("sub1", separated[0]).apply();

                                                                if(separated.length > 1)
                                                                    preferences.edit().putString("sub2", separated[1]).apply();

                                                                if(separated.length > 2)
                                                                    preferences.edit().putString("sub3", separated[2]).apply();

                                                                if(separated.length > 3)
                                                                    preferences.edit().putString("sub4", separated[3]).apply();

                                                                if(separated.length > 4)
                                                                    preferences.edit().putString("sub5", separated[4]).apply();

                                                                if(separated.length > 5)
                                                                    preferences.edit().putString("sub6", separated[5]).apply();
                                                            }
                                                            else
                                                            {
                                                                preferences.edit().putString("sub1", campaing_name).apply();
                                                            }

                                                            String appsFlyerId = "noAppsFlyerID";

                                                            preferences.edit().putString("campaign", ""+campaing_name).apply();
                                                            preferences.edit().putString("appsflyer_uid", appsFlyerId).apply();
                                                            preferences.edit().putString("pack", "com.app.thefirebirdoffreedom").apply();

                                                        } else {
                                                            Log.i("DEBUG_FACEBOOK_SDK", "AppLinkData is Null");
                                                        }
                                                    }
                                                }
                                        );
                                    } else {

                                        String campaing_name = ""+conversionData.get("campaign");

                                        if(campaing_name.contains("_"))
                                        {

                                            String[] separated = campaing_name.split("_");
                                            Log.d("LOG_TAG", "Appsflyer data: attribute: " + separated.length);

                                            if(separated.length > 0)
                                                preferences.edit().putString("sub1", separated[0]).apply();

                                            if(separated.length > 1)
                                                preferences.edit().putString("sub2", separated[1]).apply();

                                            if(separated.length > 2)
                                                preferences.edit().putString("sub3", separated[2]).apply();

                                            if(separated.length > 3)
                                                preferences.edit().putString("sub4", separated[3]).apply();

                                            if(separated.length > 4)
                                                preferences.edit().putString("sub5", separated[4]).apply();

                                            if(separated.length > 5)
                                                preferences.edit().putString("sub6", separated[5]).apply();
                                        }
                                        else
                                        {
                                            preferences.edit().putString("sub1", campaing_name).apply();
                                        }

                                        String appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(MainActivity.this);

                                        preferences.edit().putString("af_status", ""+conversionData.get("af_status")).apply();
                                        preferences.edit().putString("af_channel", ""+conversionData.get("af_channel")).apply();
                                        preferences.edit().putString("appsflyer_pid", ""+conversionData.get("pid")).apply();
                                        preferences.edit().putString("af_ad", ""+conversionData.get("af_ad")).apply();
                                        preferences.edit().putString("media_source", ""+conversionData.get("media_source")).apply();
                                        preferences.edit().putString("campaign", ""+campaing_name).apply();
                                        preferences.edit().putString("sub7", ""+conversionData.get("adgroup_id")).apply();
                                        preferences.edit().putString("sub8", ""+conversionData.get("ad_id")).apply();
                                        preferences.edit().putString("appsflyer_uid", appsFlyerId).apply();
                                        preferences.edit().putString("pack", "com.app.thefirebirdoffreedom").apply();
                                        preferences.edit().putString("t1", ""+conversionData.get("PIXEL_ID")).apply();
                                        preferences.edit().putString("t2", ""+conversionData.get("af_adset")).apply();
                                        preferences.edit().putString("af_adset", ""+conversionData.get("af_adset")).apply();
                                        preferences.edit().putString("t3", ""+conversionData.get("af_ad")).apply();
                                        preferences.edit().putString("t4", ""+conversionData.get("af_click_lookback")).apply();
                                        preferences.edit().putString("af_dp", ""+conversionData.get("af_dp")).apply();
                                        preferences.edit().putString("af_force_deeplink", ""+conversionData.get("af_force_deeplink")).apply();
                                        preferences.edit().putString("af_web_dp", ""+conversionData.get("af_web_dp")).apply();
                                        preferences.edit().putString("deep_link_value", ""+conversionData.get("deep_link_value")).apply();
                                        preferences.edit().putString("deep_link_sub1", ""+conversionData.get("deep_link_sub1")).apply();

                                    }
                                }

                                String starter = preferences.getString("af_status", null);



                                if(starter != null || DEEPLINK != null)
                                    LoadMe();

                            }

                            @Override
                            public void onConversionDataFail(String s) {}

                            @Override
                            public void onAppOpenAttribution(Map<String, String> map) {}

                            @Override
                            public void onAttributionFailure(String s) {}

                        };

                        AppsFlyerLib.getInstance().init(APPSLFYER, conversionListener, this);
                        AppsFlyerLib.getInstance().start(this);

                    }
                }
            }
        });
    }

    public boolean file_permission(){
        if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            return false;
        }else{
            return true;
        }
    }

    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

    public void initScreen() {
        setContentView(R.layout.activity_main);

        MainWebView = findViewById(R.id.MainWebView);
        SplashView = findViewById(R.id.SplashView);
        SplashView.setVisibility(View.VISIBLE);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(MainWebView, true);
        CookieManager.allowFileSchemeCookies();
        CookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.flush();

        MainWebView.getSettings().setJavaScriptEnabled(true);
        MainWebView.getSettings().setAllowFileAccess(true);
        MainWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        MainWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        MainWebView.getSettings().setSaveFormData(true);
        MainWebView.getSettings().setDomStorageEnabled(true);
        MainWebView.getSettings().setAllowContentAccess(true);
        MainWebView.getSettings().setAppCacheEnabled(true);
        MainWebView.getSettings().setDomStorageEnabled(true);
        MainWebView.getSettings().setUseWideViewPort(true);

        MainWebView.setWebViewClient(client);

        MainWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if(file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback;
                    Intent takePictureIntent = null;

                    boolean includePhoto = false;

                    paramCheck:
                    for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
                        String[] splitTypes = acceptTypes.split(", ?+");
                        for (String acceptType : splitTypes) {
                            switch (acceptType) {
                                case "*/*":
                                    includePhoto = true;
                                    break paramCheck;
                                case "image/*":
                                    includePhoto = true;
                                    break;
                            }
                        }
                    }

                    if (fileChooserParams.getAcceptTypes().length == 0) {
                        includePhoto = true;
                    }

                    if (includePhoto) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = create_image();
                                takePictureIntent.putExtra("PhotoPath", cam_file_data);
                            } catch (IOException ex) {
                                Log.i(TAG, "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                cam_file_data = null;
                                takePictureIntent = null;
                            }
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(file_type);
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }

                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    }  else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, file_req_code);
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainWebView.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager afm = getApplicationContext().getSystemService(AutofillManager.class);
            if (afm != null && afm.isEnabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    UserData data = afm.getUserData();
                }
            }
        }

        loadingBar = findViewById(R.id.loadingBar);
        loadingPanel = findViewById(R.id.loadingPanel);
        showHideLoadingBar(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (MainWebView.canGoBack()) {
                        MainWebView.goBack();
                    } else {
                        preferences.edit().putString("LAST_LOADED_URL", MainWebView.getUrl()).apply();
                        LoadMe();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause(){
        super.onPause();
        preferences.edit().putString("LAST_LOADED_URL", MainWebView.getUrl()).apply();
    }

    public void showHideLoadingBar(boolean flag) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (loadingBar == null) {
                    loadingBar = findViewById(R.id.loadingBar);
                }
                if (loadingPanel == null) {
                    loadingPanel = findViewById(R.id.loadingPanel);
                }
                if (flag) {
                    loadingBar.setVisibility(View.VISIBLE);
                    loadingPanel.setVisibility(View.VISIBLE);
                } else {
                    loadingBar.setVisibility(View.INVISIBLE);
                    loadingPanel.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void loadUrl(String url, String caller) {
        String saved_url = preferences.getString("LAST_LOADED_URL", null);

        Uri current_uri = Uri.parse(url);
        Uri.Builder uri = current_uri.buildUpon();

        if(current_uri.getQueryParameter("sub1") == null)
            uri.appendQueryParameter("sub1",preferences.getString("sub1", null));

        if(current_uri.getQueryParameter("sub2") == null)
            uri.appendQueryParameter("sub2",preferences.getString("sub2", null));

        if(current_uri.getQueryParameter("sub3") == null)
            uri.appendQueryParameter("sub3",preferences.getString("sub3", null));

        if(current_uri.getQueryParameter("sub4") == null)
            uri.appendQueryParameter("sub4",preferences.getString("sub4", null));

        if(current_uri.getQueryParameter("sub5") == null)
            uri.appendQueryParameter("sub5",preferences.getString("sub5", null));

        if(current_uri.getQueryParameter("sub6") == null)
            uri.appendQueryParameter("sub6",preferences.getString("sub6", null));

        if(current_uri.getQueryParameter("sub7") == null)
            uri.appendQueryParameter("sub7",preferences.getString("sub7", null));

        if(current_uri.getQueryParameter("sub8") == null)
            uri.appendQueryParameter("sub8",preferences.getString("sub8", null));

        if(current_uri.getQueryParameter("appsflyer_uid") == null)
            uri.appendQueryParameter("appsflyer_uid",preferences.getString("appsflyer_uid", null));

        if(current_uri.getQueryParameter("google_id") == null)
            uri.appendQueryParameter("google_id",preferences.getString("google_id", null));

        if(current_uri.getQueryParameter("pack") == null)
            uri.appendQueryParameter("pack",preferences.getString("pack", null));

        if(current_uri.getQueryParameter("t1") == null)
            uri.appendQueryParameter("t1",preferences.getString("t1", null));

        if(current_uri.getQueryParameter("t2") == null)
            uri.appendQueryParameter("t2",preferences.getString("t2", null));

        if(current_uri.getQueryParameter("t3") == null)
            uri.appendQueryParameter("t3",preferences.getString("t3", null));

        if(current_uri.getQueryParameter("t4") == null)
            uri.appendQueryParameter("t4",preferences.getString("t4", null));

        if(current_uri.getQueryParameter("campaign") == null)
            uri.appendQueryParameter("campaign",preferences.getString("campaign", null));

        if(current_uri.getQueryParameter("af_adset") == null)
            uri.appendQueryParameter("af_adset",preferences.getString("af_adset", null));

        if(current_uri.getQueryParameter("af_channel") == null)
            uri.appendQueryParameter("af_channel",preferences.getString("af_channel", null));

        if(current_uri.getQueryParameter("media_source") == null)
            uri.appendQueryParameter("media_source",preferences.getString("media_source", null));

        if(current_uri.getQueryParameter("af_ad") == null)
            uri.appendQueryParameter("af_ad",preferences.getString("af_ad", null));

        if(current_uri.getQueryParameter("appsflyer_pid") == null)
            uri.appendQueryParameter("appsflyer_pid",preferences.getString("appsflyer_pid", null));

        if(current_uri.getQueryParameter("af_dp") == null)
            uri.appendQueryParameter("af_dp",preferences.getString("af_dp", null));

        if(current_uri.getQueryParameter("af_force_deeplink") == null)
            uri.appendQueryParameter("af_force_deeplink",preferences.getString("af_force_deeplink", null));

        if(current_uri.getQueryParameter("af_web_dp") == null)
            uri.appendQueryParameter("af_web_dp",preferences.getString("af_web_dp", null));

        if(current_uri.getQueryParameter("deep_link_value") == null)
            uri.appendQueryParameter("deep_link_value",preferences.getString("deep_link_value", null));

        if(current_uri.getQueryParameter("deep_link_sub1") == null)
            uri.appendQueryParameter("deep_link_sub1",preferences.getString("deep_link_sub1", null));




        String current_page = MainWebView.getUrl();

        if (saved_url != null) {
            if(current_page == null) {
                MainWebView.loadUrl(saved_url);
                preferences.edit().putString("LAST_LOADED_URL", null).apply();
                Log.d("LOG_TAG", String.format("LOGHERE: (saved) %s", saved_url));
            }
        } else {
            MainWebView.loadUrl(uri.toString());
            Log.d("LOG_TAG", String.format("LOGHERE: %s", uri));
        }
    }

    private WebViewClient client = new WebViewClient() {

        private final long timeout = 10;

        private final Handler timeoutHandler = new Handler();

        private Runnable timeoutRunnable;


        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            loadOneSignal();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.i(TAG, String.format("WebViewClient.onReceivedError: %s, url: %s, request.getUrl: %s", error.getDescription(), view.getOriginalUrl(), request.getUrl()));

                if (error.getErrorCode() == 404 || error.getErrorCode() == 500 || error.getErrorCode() == 502 || error.getErrorCode() == 403  || error.getErrorCode() == 402){
                    preferences.edit().putString("LAST_LOADED_URL", null).apply();
                    LoadMe();
                }
                showHideLoadingBar(false);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            SplashView.setVisibility(View.GONE);
            loadingFinished = true;
            timeoutHandler.removeCallbacks(timeoutRunnable);
            showHideLoadingBar(false);
            String cookies = CookieManager.getInstance().getCookie(url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showHideLoadingBar(true);
        }
    };

    public  void LoadMe()
    {
        String comp_name = preferences.getString("campaign",null);
        String collectionName = "default";
        if(!comp_name.equals("null")) {
            collectionName = comp_name;
        }

        DocumentReference docRef = db.collection("controller").document("default");
        String collectionLast = collectionName;
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Map<String, Object> urls = document.getData();
                    if (urls != null) {
                        String retrieved_url = (String) urls.get(collectionLast);

                        if (retrieved_url != null) {
                            loadUrl(retrieved_url, "");
                        }
                        else
                        {
                            String load_def = (String) urls.get("default");
                            loadUrl(load_def, "");
                        }
                    }
                }
            }
        });
    }

    public void loadOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL);
    }

    @Override
    public void onBackPressed()
    {
        WebView webView = MainWebView;
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            preferences.edit().putString("LAST_LOADED_URL", MainWebView.getUrl()).apply();
            LoadMe();
        }
    }
}