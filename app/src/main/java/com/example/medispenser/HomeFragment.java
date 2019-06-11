package com.example.medispenser;

import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /* Imports */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList data;
    TextView wel;

    String [] myDataset = {"hello", "my", "friend", "what", "is", "hello", "my", "friend", "what", "is", "hello", "my", "friend", "what", "is", "hello", "my", "friend", "what", "is"};

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        wel = v.findViewById(R.id.txtWelcome);
        // Get a handle to the RecyclerView.
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        // Create an adapter and supply the data to be displayed.
        //mAdapter = new WordListAdapter(getActivity().getApplicationContext(), myDataset);
        //getListItems(currentUser.getUid());
        getUserData();
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

    //Funncion que trae los datos de lastMeds
    public void getListItems(String uid) {
        System.out.println("GetListItems");
        CollectionReference machinesRef = db.collection("machines");
        Query query = machinesRef.whereArrayContains("users", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList meds = new ArrayList();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> machine = document.getData();
                        ArrayList lastmeds = (ArrayList) machine.get("lastMeds");
                        for(int i = 0; i < lastmeds.size(); i++) {
                            Map<String, Object> lastmedObject = (Map<String, Object>)lastmeds.get(i);
                            if(!lastmedObject.isEmpty()) {
                                lastmedObject.put("machine", (String)machine.get("name"));
                                meds.add(lastmedObject);
                            }

                        }
                    }
                    setList(meds);
                }else {
                    System.out.println("Query failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    //Funcion que ordena los datos y los guarda en el atributo.
    public void setList(ArrayList meds) {
        ArrayList orderedMeds =  new ArrayList();
        while(!meds.isEmpty()) {
            int index = 0;
            Map<String, Object> dataMain = (Map<String, Object>)meds.get(index);
            Timestamp timeMain = (Timestamp)dataMain.get("date");
            for(int i = index + 1; i < meds.size(); i++) {
                Map<String, Object> data = (Map<String, Object>)meds.get(i);
                Timestamp t = (Timestamp)data.get("date");
                if(t.compareTo(timeMain) >= 0) {
                    dataMain = data;
                }
            }
            orderedMeds.add(dataMain);
            meds.remove(dataMain);
        }
        this.data = orderedMeds;
        mAdapter = new LastMedAdapter(getActivity().getApplicationContext(), this.data);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    public void getUserData() {
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user = document.getData();
                        String uid = (String)user.get("uid");
                        String gender = (String)user.get("gender");
                        if(gender.equals("Masculino")) {
                            wel.setText("Bienvenido");
                        } else {
                            wel.setText("Bienvenida");
                        }
                        getListItems(uid);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    System.out.println("User document failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

