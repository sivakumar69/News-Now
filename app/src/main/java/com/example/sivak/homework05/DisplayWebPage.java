package com.example.sivak.homework05;

import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DisplayWebPage extends AppCompatActivity {

    WebView displayPage;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_web_page);

        displayPage = findViewById(R.id.display_page);
        String URL = getIntent().getExtras().getString(ShowNews.WEB_URL);
        displayPage.getSettings().setJavaScriptEnabled(true);
        displayPage.getSettings().setDomStorageEnabled(true);
        displayPage.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageCommitVisible(WebView view, String url) {
                        //Log.d("WebView/l", "onPageCommitVisible.......");
                        progressDialog.dismiss();
                        super.onPageCommitVisible(view, url);
                    }

                }
        );

        showProgress(getString(R.string.loading_page));
        displayPage.loadUrl(URL);

    }

    public void showProgress(String title) {
        LinearLayout linearLayout = getLinearLayout();

        ProgressBar progress = new ProgressBar(this);
        TextView message = new TextView(this);
        message.setText(title);
        linearLayout.addView(progress);
        linearLayout.addView(message);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(linearLayout);
        progressDialog = dialogBuilder.create();
        progressDialog.show();
    }

    public LinearLayout getLinearLayout(){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(40, 40, 40, 40);
        linearLayout.setGravity(Gravity.CENTER);

        return linearLayout;
    }
}
