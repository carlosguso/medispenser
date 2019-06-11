package com.example.medispenser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class PatientsListAdapter  extends RecyclerView.Adapter<PatientsListAdapter.PatientItemViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList mDataset;
    private ArrayList mDataIds;
    private Fragment fragment;
    private Activity activity;
    private Class activityTarget;

    public PatientsListAdapter(Context context, ArrayList data, ArrayList ids, Fragment fr) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataIds = ids;
        this.fragment = fr;
        this.activity = null;
        this.activityTarget = null;
    }

    public PatientsListAdapter(Context context, ArrayList data, ArrayList ids, Activity act) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataIds = ids;
        this.fragment = null;
        this.activity = act;
        this.activityTarget = null;
    }


    public PatientsListAdapter(Context context, ArrayList data, ArrayList ids, Activity act, Class target) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataIds = ids;
        this.fragment = null;
        this.activity = act;
        this.activityTarget = target;
    }



    class PatientItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final Button btn;
        final PatientsListAdapter patientAdapter;
        public static final int PATIENT_REQUEST = 2;

        public PatientItemViewHolder(@NonNull View itemView, PatientsListAdapter adapter) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnMachine);
            patientAdapter = adapter;
            btn.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(fragment != null) {
                System.out.println("PATIENT ADAPTER CLICKED");
                int pos = getLayoutPosition();
                System.out.println(mDataIds.get(pos));
                System.out.println(fragment.getClass().toString());
                Intent intent = new Intent(v.getContext(), PatientsListActivity.class);
                intent.putExtra("machineId", (String)mDataIds.get(pos));
                fragment.startActivityForResult(intent, PATIENT_REQUEST);
            } else if(activity != null) {
                System.out.println("Patient clicked!");

                if(activityTarget != null) {
                    System.out.println(activityTarget.toString());
                }
            }

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
