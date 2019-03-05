package com.example.sivak.homework05;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    Context context;
    ArrayList<News> objects;


    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = (ArrayList<News>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHandler viewHandler;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.show_news_layout, parent, false);
            viewHandler = new ViewHandler();
            viewHandler.author = convertView.findViewById(R.id.author);
            viewHandler.publishedAt = convertView.findViewById(R.id.publish_date);
            viewHandler.title = convertView.findViewById(R.id.title);
            viewHandler.image = convertView.findViewById(R.id.display_image);
            convertView.setTag(viewHandler);
        }

        viewHandler = (ViewHandler) convertView.getTag();
        News news = objects.get(position);
        TextView author = viewHandler.author;
        TextView title = viewHandler.title;
        ImageView image = viewHandler.image;
        TextView publishedAt = viewHandler.publishedAt;

        if(!news.author.equals("null") && !news.author.equals("") && news.author != null)
            author.setText(news.author);
        else
            author.setText("");
        title.setText(news.title);
        String date = news.publishedAt.substring(0,news.publishedAt.indexOf('T'));
        publishedAt.setText(date);
        if(!news.urlToImage.equals("null") && !news.urlToImage.equals("") && news.urlToImage != null)
            Picasso.get().load(news.urlToImage).into(image);
        else
            image.setImageResource(R.drawable.ic_launcher_foreground);

        return convertView;
    }

    class ViewHandler{
        TextView author;
        TextView title;
        ImageView image;
        TextView publishedAt;
    }
}
