package com.med.fast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.med.fast.customviews.CustomFontTextView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;

/**
 * Created by Mobile Programmer 1 on 6/13/2017.
 */

@SuppressLint("SetJavaScriptEnabled")
public class FastWebViewActivity extends FastBaseActivity {
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    //Display Layout
    DisplayMetrics displayMetrics = new DisplayMetrics();
    // Utilities
    String userId;
    //Deklarasi
    @BindView(R.id.webview_web)
    WebView webView;
    @BindView(R.id.webview_toolbar_title)
    CustomFontTextView titleToolbar;
    @BindView(R.id.webview_percentage_text)
    CustomFontTextView percentText;
    @BindView(R.id.webview_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.webview_progress_wrapper)
    RelativeLayout progressBarWrapper;
    @BindView(R.id.webview_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    //Deklarasi Disconnected Layout
    @BindView(R.id.webview_disconnected_layout)
    RelativeLayout layoutDisconnected;
    @BindView(R.id.webview_disconnected_retry_btn)
    ImageButton btnCobaKembali;
    String url;
    Toast toast;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_webview_layout);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //Declare Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //SharePreference
        userId = SharedPreferenceUtilities.getUserId(this);
        //Declare Variable
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.getSettings().setUserAgentString(getString(R.string.chrome_browser));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(true);
            }
        });

        if (getIntent() != null) {
            url = Utils.processWebViewURL(getIntent().getStringExtra(IntentNames.WEBVIEW_URL));
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {

                // Finish Load Page
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (URLUtil.isValidUrl(url)) {
                        if (!url.equals(Constants.WEB_ADDRESS_BLANK)) {
                            titleToolbar.setText(view.getTitle());
                        }
                    }
                }

                // Check Connection Error
                @SuppressWarnings("deprecation")
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (errorCode == ERROR_HOST_LOOKUP
                            || errorCode == ERROR_TIMEOUT) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    } else if (errorCode == ERROR_BAD_URL) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_webview_404), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    }
                    titleToolbar.setText(view.getTitle());
                    view.loadUrl(Constants.WEB_ADDRESS_BLANK);
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

                // Check Connection Error
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if (error.getErrorCode() == ERROR_TIMEOUT
                            || error.getErrorCode() == ERROR_HOST_LOOKUP) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    } else if (error.getErrorCode() == ERROR_BAD_URL) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_webview_404), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    } else if (error.getErrorCode() == ERROR_FILE_NOT_FOUND) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_webview_404), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    }
                    titleToolbar.setText(view.getTitle());
                    view.loadUrl(Constants.WEB_ADDRESS_BLANK);
                    super.onReceivedError(view, request, error);
                }

                // Check Connection Error
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onReceivedHttpError(WebView view,
                                                WebResourceRequest request, WebResourceResponse errorResponse) {
                    if (errorResponse.getStatusCode() == ERROR_TIMEOUT
                            || errorResponse.getStatusCode() == ERROR_HOST_LOOKUP) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    } else if (errorResponse.getStatusCode() == ERROR_BAD_URL) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_webview_404), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    } else if (errorResponse.getStatusCode() == ERROR_FILE_NOT_FOUND) {
                        Toast.makeText(FastWebViewActivity.this, getString(R.string.error_webview_404), Toast.LENGTH_SHORT).show();
                        setLayoutDisconnected(true);
                    }
                    titleToolbar.setText(view.getTitle());
                    view.loadUrl(Constants.WEB_ADDRESS_BLANK);
                    super.onReceivedHttpError(view, request, errorResponse);
                }
            });
        }

        //Set Progress Bar Load URL
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Start Progress
                progressBarWrapper.setVisibility(View.VISIBLE);
                percentText.setText(String.valueOf(progress));
                percentText.append("%");
                //Start End Progress
                if (progress == 100) {
                    percentText.setText(String.valueOf(progress));
                    percentText.append("%");
                    progressBarWrapper.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(FastWebViewActivity.this.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile().image;
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e("lala", "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });

        btnCobaKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    if (webView.getUrl().equals(Constants.WEB_ADDRESS_BLANK)) {
                        webView.goBack();
                    } else {
                        webView.loadUrl(webView.getUrl());
                        setLayoutDisconnected(false);
                    }
                } else {
                    refresh(false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void refresh(boolean setIsLoading) {
        if (Utils.checkConnection(this)) {
            setLayoutDisconnected(false);
            if (getIntent() != null) {
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
                swipeRefreshLayout.setRefreshing(setIsLoading);
            }
        } else {
            titleToolbar.setText(getString(R.string.error_connection));
            setLayoutDisconnected(true);
            swipeRefreshLayout.setRefreshing(false);
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setLayoutDisconnected(Boolean isVisible) {
        if (isVisible) {
            webView.setVisibility(View.GONE);
            layoutDisconnected.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.VISIBLE);
            layoutDisconnected.setVisibility(View.GONE);
        }
    }
}
