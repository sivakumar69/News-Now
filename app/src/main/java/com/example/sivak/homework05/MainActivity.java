package com.example.sivak.homework05;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView sourcesView;
    AlertDialog progressDialog;

    public static final String API_KEY = "9727428f14ec4c9f9858af1a39590dbb";
    public static final String SOURCE_OBJECT = "SOURCE_OBJECT";
    public static final String ENCODE_TYPE = "UTF-8";
    ArrayList<Source> sourcesList = new ArrayList<Source>();
    ArrayAdapter<Source> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourcesView = findViewById(R.id.sources_list);

        if(isConnected()){
            showProgress(getString(R.string.loading_sources));
            new GetSourcesList().execute(getSourcesURL());
        }
        else{
            Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }

        sourcesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowNews.class);
                intent.putExtra(SOURCE_OBJECT, sourcesList.get(position));
                startActivity(intent);
            }
        });

    }

    public String getSourcesURL(){
        return "https://newsapi.org/v2/sources?apiKey="+API_KEY;
    }



    public boolean isConnected () {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected())
            return false;

        return true;
    }

    class GetSourcesList extends AsyncTask<String, Void, ArrayList<Source>> {
        @Override
        protected ArrayList<Source> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            try {
                ArrayList<Source> newsList = new ArrayList<Source>();
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                String json = "";
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    json = IOUtil.toString(connection.getInputStream(), ENCODE_TYPE);
                    JSONObject root = null;
                    try {
                        root = new JSONObject(json);
                        JSONArray articles = root.getJSONArray("sources");
                        for (int i=0; i<articles.length();i++){
                            JSONObject newsJson = articles.getJSONObject(i);
                            Source news = new Source();
                            news.id = newsJson.getString("id");
                            news.name = newsJson.getString("name");
                            newsList.add(news);
                        }
                        return newsList;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    return newsList;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }finally {
                connection.disconnect();
            }

            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Source> sourceList) {
            progressDialog.dismiss();
            if (sourceList != null) {
                if (sourceList.size() > 0) {
                    sourcesList = sourceList;
                    arrayAdapter = new ArrayAdapter<Source>(MainActivity.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1, sourcesList);
                    sourcesView.setAdapter(arrayAdapter);
                } else if (sourceList.size() == 0)
                    Toast.makeText(MainActivity.this, R.string.error_con_timeout,
                            Toast.LENGTH_SHORT).show();
            }
        }
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
