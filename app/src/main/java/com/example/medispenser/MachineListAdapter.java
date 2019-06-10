package com.example.medispenser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


public class MachineListAdapter
        extends RecyclerView.Adapter<MachineListAdapter.MachineItemViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList mDataset;

    public  MachineListAdapter(Context context, ArrayList data) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = data;

    }

    class MachineItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final Button btn;
        final MachineListAdapter machineAdapter;


        public MachineItemViewHolder(View itemView, MachineListAdapter adapter) {
            super(itemView);
            this.btn = itemView.findViewById(R.id.btnMachine);
            this.machineAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            int mPosition = getLayoutPosition();
            System.out.println("Button number " + mPosition + " clicked!");
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
