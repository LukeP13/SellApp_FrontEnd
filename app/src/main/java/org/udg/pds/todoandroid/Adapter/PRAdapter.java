package org.udg.pds.todoandroid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class PRAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    List<Product> list = new ArrayList<>();
    Context context;

    public PRAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product_list_item, parent, false);
        ProductViewHolder holder = new ProductViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.price.setText(list.get(position).getPrice().toString());
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getIdProduct();
    }

    public Product getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView
    public void insert(int position, Product p) {
        list.add(position, p);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing the Data object
    public void remove(Product p) {
        int position = list.indexOf(p);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void add(Product p) {
        list.add(p);
        this.notifyItemInserted(list.size() - 1);
    }

    public void clear() {
        int size = list.size();
        list.clear();
        this.notifyItemRangeRemoved(0, size);
    }

}
