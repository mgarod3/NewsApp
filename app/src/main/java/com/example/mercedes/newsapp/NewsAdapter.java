package com.example.mercedes.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<Article> {
    ViewHolder viewHolder;
    Article currentArticle;

    /**
     * Create a new {@link NewsAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param news    is the list of {@link Article}s to be displayed.
     */
    public NewsAdapter(Activity context, List<Article> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Find the article at the given position in the list of books
        currentArticle = getItem(position);

        //Get the title from the current article and set it into the corresponding TextView
        viewHolder.articleTitle.setText(currentArticle.getTitle());
        //Get the author name from the current article and set it into the corresponding TextView
        viewHolder.articleContributor.setText(currentArticle.getContributor());
        //Get the section to which the current article belongs
        String section = currentArticle.getSection();
        //Get the date when the current article was published
        String date = currentArticle.getDate();
        /*
        * Format date
        **/
        date = date.substring(0, 10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObject = new Date();
        try {
            dateObject = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Format the date string (i.e. "Mar 3, 1984")
        date = formatDate(dateObject);
        String str1 = date.substring(0, 1).toUpperCase();
        date = str1 + date.substring(1);
        //Combine section and date into one string variable
        section = section + " - " + date;
        //Set section and date into the corresponding TextView
        viewHolder.articleSecDate.setText(section);

        return convertView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
}
