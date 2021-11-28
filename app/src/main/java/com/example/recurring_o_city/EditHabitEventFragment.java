package com.example.recurring_o_city;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EditHabitEventFragment extends DialogFragment {

    private TextView eventTitle;
    private EditText eventComment;
    private EditText eventLocation;
    private ImageButton mapButton;
    private ImageButton photoButton;
    private ImageView imageView;
    private byte[] bytes;
    private String event_comment, event_image;
    private EditHabitEventFragment.EditHabitEventFragmentListener listener;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    CollectionReference collectionReference;
    DocumentReference editHabitEvent;


    public EditHabitEventFragment(){

    }

    public static EditHabitEventFragment newInstance(String event_title, String event_datedone, String UserId) {
        Bundle args = new Bundle();

        args.putString("event_datedone", event_datedone);
        // String acts as key for retrieving Firebase document
        args.putString("event_title", event_title);

        // Get location to show in edit box?
//        args.putString("event_location", newHabitEvent.getEventLoc());

        args.putString("User_Id", UserId);

        EditHabitEventFragment fragment = new EditHabitEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface EditHabitEventFragmentListener{
        void onEditEventSavePressed(String newComment, String img);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditHabitEventFragment.EditHabitEventFragmentListener) getParentFragment();
        } catch (RuntimeException e) {
            // The activity doesn't implement the interface, throw exception
            throw new RuntimeException(context.toString()
                    + " must implement EditHabitEventFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_habit_event_fragment, null);

        eventTitle = view.findViewById(R.id.editevent_title);
        eventComment = view.findViewById(R.id.editevent_comment);
        eventLocation = view.findViewById(R.id.editevent_location);
        mapButton = view.findViewById(R.id.editevent_map);
        photoButton = view.findViewById(R.id.editevent_photo);
        imageView = view.findViewById(R.id.editevent_image);

        // Set the collection reference from the Firebase.
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Habit Events");


        // Get reference to the selected document
        collectionReference
                .whereEqualTo("User Id", getArguments().getString("User_Id"))
                .whereEqualTo("Title", getArguments().getString("event_title"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                // String to date and back to string to extract yyyy/MM/dd/HH/mm
                                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                                String date1_string = getArguments().getString("event_datedone");
                                Date date1;
                                try {
                                    date1 = format.parse(date1_string);
                                    date1_string = format.format(date1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                // Date to string
                                Date date2 = document.getTimestamp("DateCreated").toDate();
                                String date2_string = format.format(date2);

                                // Fetch data if date created is the same (2 string above are the same)
                                if (date1_string.equals(date2_string)) {
                                    // Set the habit event details
                                    eventTitle.setText(document.getString("Title"));
                                    eventComment.setText(document.getString("Comment"));

                                    event_image = document.getString("Photograph");
                                    if (event_image != null) {
                                        byte[] bytes = Base64.getDecoder().decode(event_image);
                                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    }
                                    // Get reference to the selected document
                                    editHabitEvent = document.getReference();
                                    break;
                                }
                            }
                        }
                    }
                });


        // When click on the image button, go to take photo
        // Code to take photo implements here

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    imageView.setImageBitmap(bitmap);

                    // Compress into array of bytes.
                    int size = bitmap.getWidth() * bitmap.getHeight();
                    ByteArrayOutputStream out = new ByteArrayOutputStream(size);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    bytes = out.toByteArray();
                }
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                }
            }
        });

        // When click on map image, open map activity
        // Code to implements map here


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MapsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer_layout, fragment)
                        .addToBackStack(null).commit();
            }
        });
        // Package the unique creation date of habit event along with intent.
        // Make sure the habit and user matches.
        //collectionReference
        //        .whereEqualTo("User Id", getArguments().getString("User_Id"))
        //        .whereEqualTo("Title", getArguments().getString("event_title"))

        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Habit Event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    // Get input from user, can be null as optional
                    String comment = eventComment.getText().toString();

                    // Get the image (byte array) and convert to list to store on firebase
                    String img = "";
                    if (bytes != null){
                        img = Base64.getEncoder().encodeToString(bytes);
                    }
                    else {
                        if (event_image != null) {
                            img = event_image;
                        }
                    }


                    // Get the location

                    // Update to database
                    editHabitEvent.update("Comment", comment);
                    editHabitEvent.update("Photograph", img);


                    // When user clicks save button, add these details to habit event
                    listener.onEditEventSavePressed(comment, img);
                }).create();

    }

}
