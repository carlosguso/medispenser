package com.example.medispenser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


public class MachineListAdapter
        extends RecyclerView.Adapter<MachineListAdapter.MachineItemViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList mDataset;
    private ArrayList mDataIds;
    //private Activity activity;

    public  MachineListAdapter(Context context, ArrayList data, ArrayList ids) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;
        this.mDataIds = ids;
        //this.activity = act;

    }

    class MachineItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final Button btn;
        final MachineListAdapter machineAdapter;


        public MachineItemViewHolder(View itemView, MachineListAdapter adapter) {
            super(itemView);
            this.btn = itemView.findViewById(R.id.btnMachine);
            this.machineAdapter = adapter;
            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println("MACHINE ADAPTER CLICKED");
            int pos = getLayoutPosition();
            System.out.println(mDataIds.get(pos));
            Intent intent = new Intent(v.getContext(), MachineSettingsActivity.class);
            intent.putExtra("machineId", (String)mDataIds.get(pos));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
            //activity.finish();
        }
    }

    @NonNull
    @Override
    public MachineListAdapter.MachineItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mItemView = mInflater.inflate(R.layout.machine_list_item, parent, false);
        return new MachineListAdapter.MachineItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineListAdapter.MachineItemViewHolder holder, int position) {
        String machine = (String)this.mDataset.get(position);
        holder.btn.setText(machine);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
