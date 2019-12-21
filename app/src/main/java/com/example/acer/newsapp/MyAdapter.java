package com.example.acer.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<articleinfo> {


    public MyAdapter(Context c, ArrayList<articleinfo> array) {
        super(c, 0, array);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.template, parent, false);
        }
        articleinfo info = getItem(position);

        TextView section = (TextView) listItemView.findViewById(R.id.Section);
        section.setText(String.valueOf(info.getaSection()));

        TextView Date = (TextView) listItemView.findViewById(R.id.Date);
        Date.setText(String.valueOf(info.getaDate()));

        TextView ArticleTitle = (TextView) listItemView.findViewById(R.id.articletitle);
        ArticleTitle.setText(String.valueOf(info.getaTitle()));

 TextView Autor = (TextView) listItemView.findViewById(R.id.author);
        Autor.setText(String.valueOf(info.getaauthor()));


        return listItemView;
    }
}