package com.khalil.DRACS.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Map;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {
    private final Context context;
    private final FirestoreModel model;
    private int expandedPosition = -1;
    private static final String TAG = "ExpandableAdapter";

    public ExpandableAdapter(Context context, FirestoreModel model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expandable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, FirestoreModel.Section> sections = model.getSections();
        String sectionKey = (String) sections.keySet().toArray()[position];
        FirestoreModel.Section section = sections.get(sectionKey);

        // Debug logging
        Log.d(TAG, "Processing section: " + sectionKey);
        Log.d(TAG, "Total dashes: " + section.getDashes().size());
        Log.d(TAG, "Total clickable words: " + section.getClickableWords().size());

        holder.title.setText("▼ " + sectionKey);

        // Create a SpannableString for the content
        StringBuilder contentBuilder = new StringBuilder();
        for (String dash : section.getDashes()) {
            if (dash != null && !dash.isEmpty()) {
                contentBuilder.append("- ").append(dash).append("\n");
            }
        }

        // Debug logging for content
        Log.d(TAG, "Content length: " + contentBuilder.length());
        Log.d(TAG, "Content preview: " + (contentBuilder.length() > 0 ? 
            contentBuilder.substring(0, Math.min(100, contentBuilder.length())) : "Empty"));

        SpannableString spannableString = new SpannableString(contentBuilder.toString());

        // Set ClickableSpan for each clickable word
        for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
            String searchText = cw.getText();
            if (searchText == null || searchText.isEmpty()) {
                Log.d(TAG, "Skipping empty clickable word");
                continue;
            }

            Log.d(TAG, "Looking for word: '" + searchText + "'");
            int startIndex = 0;
            int foundCount = 0;
            
            // Search for all occurrences of the clickable word
            while (true) {
                startIndex = contentBuilder.indexOf(searchText, startIndex);
                if (startIndex == -1) {
                    if (foundCount == 0) {
                        Log.d(TAG, "Word not found: '" + searchText + "'");
                    }
                    break;
                }
                
                int endIndex = startIndex + searchText.length();
                
                // Check if this is a whole word (not part of a larger word)
                boolean isWholeWord = true;
                if (startIndex > 0) {
                    char prevChar = contentBuilder.charAt(startIndex - 1);
                    isWholeWord = !Character.isLetterOrDigit(prevChar);
                }
                if (endIndex < contentBuilder.length()) {
                    char nextChar = contentBuilder.charAt(endIndex);
                    isWholeWord = isWholeWord && !Character.isLetterOrDigit(nextChar);
                }
                
                if (isWholeWord) {
                    foundCount++;
                    // Show debug Toast for found clickable word
                    Toast.makeText(context, 
                        "Found clickable word: '" + searchText + 
                        "' at position: " + startIndex + 
                        " in section: " + sectionKey, 
                        Toast.LENGTH_SHORT).show();

                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            if (cw.getOnClickListener() != null) {
                                // Show debug Toast for click
                                Toast.makeText(context, 
                                    "Clicked word: '" + searchText + 
                                    "' with action: " + cw.getActionType() + 
                                    " and value: " + cw.getActionValue(), 
                                    Toast.LENGTH_SHORT).show();
                                
                                cw.getOnClickListener().onClick(widget);
                            }
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            String colorHex = cw.getColor();
                            try {
                                colorHex = colorHex.replace("#", "");
                                int color = Color.parseColor("#" + colorHex);
                                ds.setColor(color);
                            } catch (Exception e) {
                                ds.setColor(ContextCompat.getColor(context, R.color.Emerald_Green_800));
                            }
                            ds.setUnderlineText(true);
                        }
                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                
                startIndex = endIndex;
            }
        }

        // Set color for colored lines
        if (section.getColoredLines() != null) {
            for (FirestoreModel.ColoredLine cl : section.getColoredLines()) {
                int startIndex = contentBuilder.indexOf(cl.getText());
                int endIndex = startIndex + cl.getText().length();
                if (startIndex != -1) {
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            // No action needed for colored lines
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            String colorHex = cl.getColor();
                            try {
                                // Remove # if present and parse the hex color
                                colorHex = colorHex.replace("#", "");
                                int color = Color.parseColor("#" + colorHex);
                                ds.setColor(color);
                            } catch (Exception e) {
                                // Fallback to default color if hex parsing fails
                                ds.setColor(ContextCompat.getColor(context, R.color.green));
                            }
                            ds.setStyle(TextPaint.Style.FILL);
                        }
                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        // Set content
        holder.intro.setText(section.getIntroduction());
        holder.lists.setText(spannableString);
        holder.conclu.setText(section.getConclusion());

        // Enable clickable links
        holder.lists.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle expansion
        final boolean isExpanded = position == expandedPosition;
        holder.expandableLayout.setExpanded(isExpanded, false);
        holder.intro.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.lists.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.conclu.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.title.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return model.getSections().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView intro;
        TextView lists;
        TextView conclu;
        ExpandableLayout expandableLayout;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            intro = itemView.findViewById(R.id.intro);
            lists = itemView.findViewById(R.id.lists);
            conclu = itemView.findViewById(R.id.conclusion);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }
}
