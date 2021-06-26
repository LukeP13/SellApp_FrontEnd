package org.udg.pds.todoandroid.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView price;
    View view;

    ProductViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        name = itemView.findViewById(R.id.productName);
        price = itemView.findViewById(R.id.productPrice);
    }
}
