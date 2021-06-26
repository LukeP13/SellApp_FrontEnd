package org.udg.pds.todoandroid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Rate;

import java.util.ArrayList;
import java.util.List;

import static org.udg.pds.todoandroid.util.Global.DATE_ONLY_FORMAT;
import static org.udg.pds.todoandroid.util.Global.FULL_TIME_DATE_FORMAT;

public class RateAdapter extends RecyclerView.Adapter<RateViewHolder> {
    List<Rate> list = new ArrayList<>();
    Context context;

    public RateAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_list_item, parent, false);


        RateViewHolder holder = new RateViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RateViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.date.setText(DATE_ONLY_FORMAT.format(list.get(position).getDate()));
        // Set Image holder.image.setImage
        holder.rating.setRating(list.get(position).getRate());
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getIdRate();
    }

    public Rate getItem(int position) {
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
    public void insert(int position, Rate r) {
        list.add(position, r);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing the Data object
    public void remove(Rate r) {
        int position = list.indexOf(r);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void add(Rate r) {
        list.add(r);
        this.notifyItemInserted(list.size() - 1);
    }

    public void clear() {
        int size = list.size();
        list.clear();
        this.notifyItemRangeRemoved(0, size);
    }

}
