package org.udg.pds.todoandroid.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;

public class RateViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView description;
    TextView date;
    RatingBar rating;
    ImageView image;
    View view;

    RateViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        name = itemView.findViewById(R.id.userName);
        date = itemView.findViewById(R.id.rateDate);
        description = itemView.findViewById(R.id.rateDescription);
        rating = itemView.findViewById(R.id.rateBar);
        image = itemView.findViewById(R.id.avatarImage);
    }
}
