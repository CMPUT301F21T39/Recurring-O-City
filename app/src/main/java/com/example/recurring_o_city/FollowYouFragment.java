package com.example.recurring_o_city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FollowYouFragment extends Fragment {

    private ArrayList<User> follower;
    public FollowYouFragment() {
        // Required empty public constructor
    }


    public static FollowYouFragment newInstance(ArrayList<User> follower) {
        FollowYouFragment fragment = new FollowYouFragment();
        Bundle args = new Bundle();
        args.putSerializable("Follower", follower);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_follow_you, container, false);
    }
}