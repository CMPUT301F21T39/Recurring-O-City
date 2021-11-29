package com.example.recurring_o_city;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FollowYouFragment extends Fragment {

    private ArrayList<String> follower;
    private ImageButton backButton;
    private ListView userList;
    private UserAdapter userAdapter;
    public FollowYouFragment() {
        // Required empty public constructor
    }


    public static FollowYouFragment newInstance(ArrayList<String> follower) {
        FollowYouFragment fragment = new FollowYouFragment();
        Bundle args = new Bundle();
        args.putSerializable("Follower", follower);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        follower = (ArrayList<String>) getArguments().getSerializable("Follower");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_follow_you, container, false);
        backButton = view.findViewById(R.id.send_back_button);

        // Custom adapter
        userList = view.findViewById(R.id.listview);
        userAdapter = new UserAdapter(getContext(), follower);
        userList.setAdapter(userAdapter);

        // When click back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}