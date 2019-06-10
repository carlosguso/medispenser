package com.example.medispenser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;


import java.util.ArrayList;
import java.util.Map;

public class LastMedAdapter extends
        RecyclerView.Adapter<LastMedAdapter.ItemViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList mDataset = new ArrayList();


    public LastMedAdapter(Context context, ArrayList data) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView txtName;
        public final TextView txtDate;
        public final TextView txtMachine;
        final LastMedAdapter medAdapter;

        public ItemViewHolder(View itemView, LastMedAdapter adapter ) {
            super(itemView);
            this.txtName = itemView.findViewById(R.id.lastMedName);
            this.txtDate = itemView.findViewById(R.id.lastMedDateTime);
            this.txtMachine = itemView.findViewById(R.id.lastMedMachine);
            this.medAdapter = adapter;
        }

    }

    @NonNull
    @Override
    public LastMedAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mItemView = mInflater.inflate(R.layout.last_meds_item, parent, false);
        return new ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LastMedAdapter.ItemViewHolder holder, int position) {
        Map<String, Object> data = (Map<String, Object>)mDataset.get(position);
        Timestamp t = (Timestamp)data.get("date");
        String mName = (String)data.get("name");
        String mDate = t.toDate().toString();
        String mMachine = (String)data.get("machine");
        holder.txtName.setText(mName);
        holder.txtDate.setText(mDate);
        holder.txtMachine.setText(mMachine);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
