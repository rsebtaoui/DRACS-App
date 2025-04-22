// PS.java (updated)
package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.Models.Item;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PS extends Fragment {
    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_p_s, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.psrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data from Firestore
        fetchDataFromFirestore();

        return view;
    }

    private void fetchDataFromFirestore() {
        db.collection("pages").document("RNA")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FirestoreModel model = document.toObject(FirestoreModel.class);
                            if (model != null) {
                                List<Item> items = convertFirestoreToItems(model);
                                adapter = new ExpandableAdapter(getContext(), items);
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<Item> convertFirestoreToItems(FirestoreModel model) {
        List<Item> items = new ArrayList<>();
        Map<String, FirestoreModel.Section> sections = model.getSections();

        for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
            String sectionTitle = entry.getKey();
            FirestoreModel.Section section = entry.getValue();

            // Convert clickable words
            List<Item.ClickableWord> clickableWords = new ArrayList<>();
            if (section.getClickableWords() != null) {
                for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
                    clickableWords.add(new Item.ClickableWord(cw.getText(), v -> {
                        handleClickableWordAction(cw.getActionType(), cw.getActionValue());
                    }));
                }
            }

            // Convert colored lines
            List<Item.Coloredlines> coloredLines = new ArrayList<>();
            if (section.getColoredLines() != null) {
                for (FirestoreModel.ColoredLine cl : section.getColoredLines()) {
                    coloredLines.add(new Item.Coloredlines(cl.getText()));
                }
            }

            // Convert dashes to a single string
            StringBuilder dashesBuilder = new StringBuilder();
            if (section.getDashes() != null) {
                for (String dash : section.getDashes()) {
                    dashesBuilder.append("- ").append(dash).append("\n");
                }
            }

            // Create the Item
            items.add(new Item(
                    "▼ " + sectionTitle,
                    section.getIntroduction() != null ? section.getIntroduction() : "",
                    dashesBuilder.toString(),
                    section.getConclusion() != null ? section.getConclusion() : "",
                    clickableWords,
                    coloredLines
            ));
        }

        return items;
    }

    private void handleClickableWordAction(String actionType, String actionValue) {
        switch (actionType) {
            case "download":
                FileUtils.copyFileFromAssets(getContext(), actionValue);
                break;
            case "map":
                String[] coords = actionValue.split(",");
                if (coords.length == 2) {
                    double lat = Double.parseDouble(coords[0]);
                    double lng = Double.parseDouble(coords[1]);
                    FileUtils.openGoogleMaps(getContext(), lat, lng, "");
                }
                break;
            case "youtube":
                // Handle YouTube video opening
                break;
            // Add other action types as needed
        }
    }

    // ... rest of your existing PS fragment code ...
}