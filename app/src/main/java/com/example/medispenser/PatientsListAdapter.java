package com.example.medispenser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class PatientsListAdapter  extends RecyclerView.Adapter<PatientsListAdapter.PatientItemViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList mDataset;
    private  ArrayList mDataIds;

    public PatientsListAdapter(Context context, ArrayList data, ArrayList ids) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataIds = ids;
    }

    class PatientItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final Button btn;
        final PatientsListAdapter patientAdapter;

        public PatientItemViewHolder(@NonNull View itemView, PatientsListAdapter adapter) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnMachine);
            patientAdapter = adapter;
            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println("PATIENT ADAPTER CLICKED");
            int pos = getLayoutPosition();
            System.out.println(mDataIds.get(pos));
        }
    }

    @NonNull
    @Override
    public PatientsListAdapter.PatientItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mItemView = mInflater.inflate(R.layout.machine_list_item, parent, false);
        return new PatientsListAdapter.PatientItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientItemViewHolder holder, int i) {
        String machine = (String)this.mDataset.get(i);
        holder.btn.setText(machine);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
