package com.edu.tutorialaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edu.tutorialaction.entity.Reserve;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class ReservesAdapter extends BaseAdapter {

    private final ArrayList<Reserve> reserves;
    private final Context context;

    public ReservesAdapter(Context context) {
        this.context = context;
        this.reserves = new ArrayList<>();
    }

    public void addReserves(List<Reserve> reserves) {
        this.reserves.addAll(reserves);
        notifyDataSetChanged();
    }

    public void addReserve(Reserve reserve) {
        reserves.add(reserve);
        notifyDataSetChanged();
    }

    public boolean removeReserve(Reserve reserve) {
        boolean removed = reserves.remove(reserve);
        notifyDataSetChanged();
        return removed;
    }

    public void clearReserves() {
        reserves.clear();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return reserves.size();
    }

    @Override
    public Reserve getItem(int position) {
        return reserves.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_reserve, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.fullname)).setText(reserves.get(position).getFirstname() + " " + reserves.get(position).getLastname());

        return convertView;
    }
}
