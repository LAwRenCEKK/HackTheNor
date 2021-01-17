package com.htn.movez.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.htn.movez.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CountDownFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CountDownFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CountDownFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CountDownFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CountDownFragment newInstance(String param1, String param2) {
        CountDownFragment fragment = new CountDownFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CountDownFragment newInstance() {
        CountDownFragment fragment = new CountDownFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_count_down, container, false);
        TextView hour = v.findViewById(R.id.hour);
        TextView minute = v.findViewById(R.id.minute);
        TextView second = v.findViewById(R.id.second);
        hour.setText("20");
        minute.setText("20");
        second.setText("20");

        return inflater.inflate(R.layout.fragment_count_down, container, false);
    }

//    public void countDown(TextView h,TextView m ,TextView s){
////        TextView hour = getView().findViewById(R.id.hour);
////        TextView minute = getView().findViewById(R.id.minute);
////        TextView second = getView().findViewById(R.id.second);
//
//        h.setText("34");
//        s.setText("34");
//        m.setText("34");
//
//    }
}