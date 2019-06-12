package com.example.medispenser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedListAdapter extends RecyclerView.Adapter<MedListAdapter.MedItemViewHolder>  {
    private LayoutInflater mInflater;
    private ArrayList mDataset;
    private ArrayList mDataObjects;
    private Activity activity;
    private Class activityTarget;
    private String patiendId;
    private String machineId;

    public MedListAdapter(Context context, ArrayList data, ArrayList objects, Activity act, Class target, String patientid, String machineid) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataObjects = objects;
        this.activity = act;
        this.activityTarget = target;
        this.patiendId = patientid;
        this.machineId = machineid;
    }

    public class MedItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final MedListAdapter medAdapter;
        public final Button btn;
        public static final int DELETE_MED = 4;

        public MedItemViewHolder(@NonNull View itemView, MedListAdapter adapter) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnMachine);
            this.medAdapter = adapter;
            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            Intent intent = new Intent(activity,activityTarget);
            Map<String, Object> medItem = new HashMap<>();
            medItem = (Map<String, Object>)mDataObjects.get(pos);
            intent.putExtra("patientId", patiendId);
            intent.putExtra("machineId", machineId);
            intent.putExtra("medItem", (Serializable) medItem);
            activity.startActivityForResult(intent, DELETE_MED);
        }
    }

    @NonNull
    @Override
    public MedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mItemView = mInflater.inflate(R.layout.machine_list_item, parent, false);
        return new MedListAdapter.MedItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MedItemViewHolder holder, int i) {
        String name = mDataset.get(i).toString();
        holder.btn.setText(name);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
