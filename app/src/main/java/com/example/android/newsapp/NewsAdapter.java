package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Maks on 21/07/2017.
 */

class NewsAdapter extends ArrayAdapter {

    // Public constructor
    NewsAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    class ViewHolder {
        TextView titleView;
        TextView sectionView;
        ViewHolder(View v){
            titleView = v.findViewById(R.id.item_title);
            sectionView = v.findViewById(R.id.item_section);
        }
    }

    //Returns a list item view about the story at the given position in the list of stories
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if there is an existing list
        View listItemView = convertView;
        ViewHolder holder = null;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }else{
            holder = (ViewHolder) listItemView.getTag();
        }

        // Find the book at the given position in the list of books
        Story currentStory = (Story) getItem(position);

        //Bind and set title
        holder.titleView.setText(currentStory.getTitle());
        //Bind and set section
        holder.sectionView.setText(currentStory.getSection());

        //Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
