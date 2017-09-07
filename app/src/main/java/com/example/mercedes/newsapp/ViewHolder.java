package com.example.mercedes.newsapp;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public class ViewHolder {
    TextView articleSecDate;
    TextView articleTitle;
    TextView articleContributor;
    TextView articleSection;

    public ViewHolder(@NonNull View view) {
        this.articleSecDate = (TextView) view.findViewById(R.id.article_section_and_date);
        this.articleTitle = (TextView) view.findViewById(R.id.article_title);
        this.articleContributor = (TextView) view.findViewById(R.id.article_contributor);
    }
}
