package com.example.recurring_o_city;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * displays a popup of whatever habit needs to be done
 */
public class HabitEventFragment extends Fragment {

    private ArrayList<HabitEvent> habitEventList;


    public HabitEventFragment() {
        // Required empty public constructor
    }

    /**
     * @param list
     * @return Fragment
     */
    public static HabitEventFragment newInstance(ArrayList<HabitEvent> list) {
        HabitEventFragment fragment = new HabitEventFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("HABIT_EVENT", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        habitEventList = (ArrayList<HabitEvent>) getArguments().getSerializable(
                "HABIT_EVENT");

    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_event, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.habit_event_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ItemAdapter myAdapter = new ItemAdapter((ArrayList<Habit>) null, "event");
        recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            /**
             * @param position
             */
            @Override
            public void onItemClick(int position) {
                HabitEvent selectedHabitEvent = (HabitEvent) habitEventList.get(position);
                ViewHabitEventFragment habitEventFrag = new ViewHabitEventFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.habit_event_frame, habitEventFrag.newInstance(selectedHabitEvent))
                        .addToBackStack(null).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}