package com.example.medispenser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachinesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachinesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachinesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //My attributes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList data;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "MachinesFragment";
    public static final int TEXT_REQUEST = 2;

    public MachinesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachinesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachinesFragment newInstance(String param1, String param2) {
        MachinesFragment fragment = new MachinesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_machines, container, false);
        data = new ArrayList();
        // Get a handle to the RecyclerView.
        mRecyclerView = v.findViewById(R.id.recycler_view_machines);
        getListItems(currentUser.getUid());
        // Inflate the layout for this fragment
        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Funncion que trae los nombres de las maquinas
    public void getListItems(String uid) {
        CollectionReference machinesRef = db.collection("machines");
        Query query = machinesRef.whereArrayContains("users", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList names = new ArrayList();
                    ArrayList ids = new ArrayList();
                    System.out.println("Query successful, items: " + task.getResult().size());
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> machine = document.getData();
                        names.add((String)machine.get("name"));
                        ids.add((String)machine.get("id"));
                    }
                    data = names;
                    // Create an adapter and supply the data to be displayed.
                    mAdapter = new MachineListAdapter(getActivity().getApplicationContext(),data, ids, MachinesFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                    // Give the RecyclerView a default layout manager.
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                }else {
                    System.out.println("Query failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Regreso el viejon, requestCode: " + requestCode);
        if (requestCode == TEXT_REQUEST) {
            System.out.println("El requestCode es correcto y el result_code es: " + resultCode);
            if (resultCode == RESULT_OK) {
                System.out.println("Regreso el viejon OK");
                getListItems(currentUser.getUid());
            }
        }

    }

}
