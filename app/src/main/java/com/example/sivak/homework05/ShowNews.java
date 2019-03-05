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
import java.util.List;

public class ShowNews extends AppCompatActivity {

    ListView newsListView;
    public List<News> newsList;

    AlertDialog progressDialog;

    public static final String WEB_URL = "WEB_URL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);

        Source source = (Source) getIntent().getExtras().getSerializable(MainActivity.SOURCE_OBJECT);
        setTitle(source.name);

        newsListView = findViewById(R.id.news_list);

        if(isConnected()){
            showProgress(getString(R.string.loading_stories));
            new GetNewsList().execute(getNewsFromSource(source.id));
        }
        else{
            Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowNews.this, DisplayWebPage.class);
                News news = new News();
                news = newsList.get(position);
                intent.putExtra(WEB_URL, news.url);
                if(isConnected())
                    startActivity(intent);
                else
                    Toast.makeText(ShowNews.this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    class GetNewsList extends AsyncTask<String, Void, ArrayList<News>> {
        @Override
        protected ArrayList<News> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            try {
                ArrayList<News> newsList = new ArrayList<News>();
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                String json = "";
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    json = IOUtil.toString(connection.getInputStream(), MainActivity.ENCODE_TYPE);
                    JSONObject root = null;
                    try {
                        root = new JSONObject(json);
                        JSONArray articles = root.getJSONArray("articles");
                        for (int i=0; i<articles.length();i++){
                            JSONObject newsJson = articles.getJSONObject(i);
                            News news = new News();
                            news.author = newsJson.getString("author");
                            news.title = newsJson.getString("title");
                            news.url = newsJson.getString("url");
                            news.urlToImage = newsJson.getString("urlToImage");
                            news.publishedAt = newsJson.getString("publishedAt");
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
        protected void onPostExecute(ArrayList<News> newsLists) {
            progressDialog.dismiss();
            if (newsLists != null) {
                if (newsLists.size() > 0) {
                    newsList = newsLists;
                    NewsAdapter newsAdapter = new NewsAdapter(ShowNews.this, R.layout.show_news_layout, newsList);
                    newsListView.setAdapter(newsAdapter);
                } else if (newsLists.size() == 0)
                    Toast.makeText(ShowNews.this, R.string.error_con_timeout,
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getNewsFromSource(String sourceID){
        return "https://newsapi.org/v2/top-headlines?sources="+sourceID+
                "&apiKey="+MainActivity.API_KEY;
    }

    public boolean isConnected () {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected())
            return false;

        return true;
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
