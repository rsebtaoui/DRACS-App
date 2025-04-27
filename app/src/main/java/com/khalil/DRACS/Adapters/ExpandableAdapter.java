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

import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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
        List<Map.Entry<String, FirestoreModel.Section>> sectionEntries = new ArrayList<>(sections.entrySet());
        sectionEntries.sort(Comparator.comparingInt(a -> a.getValue().getOrder()));
        Map.Entry<String, FirestoreModel.Section> entry = sectionEntries.get(position);
        FirestoreModel.Section section = entry.getValue();

        holder.title.setText("▼ " + entry.getKey());

        // 1. Build FULL content: intro + dashes + conclusion
        StringBuilder fullContentBuilder = new StringBuilder();
        if (section.getIntroduction() != null) {
            fullContentBuilder.append(section.getIntroduction()).append("\n\n");
        }
        for (String dash : section.getDashes()) {
            if (dash != null && !dash.isEmpty()) {
                fullContentBuilder.append("- ").append(dash).append("\n");
            }
        }
        if (section.getConclusion() != null) {
            fullContentBuilder.append("\n").append(section.getConclusion());
        }

        SpannableString spannableString = new SpannableString(fullContentBuilder.toString());

        // 2. Apply ClickableWords
        for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
            String searchText = cw.getText();
            if (searchText == null || searchText.isEmpty()) continue;

            int startIndex = 0;
            while ((startIndex = fullContentBuilder.indexOf(searchText, startIndex)) != -1) {
                int endIndex = startIndex + searchText.length();

                // Optional: Check for full-word match (if you want)
                boolean isWholeWord = true;
                if (startIndex > 0) isWholeWord &= !Character.isLetterOrDigit(fullContentBuilder.charAt(startIndex - 1));
                if (endIndex < fullContentBuilder.length()) isWholeWord &= !Character.isLetterOrDigit(fullContentBuilder.charAt(endIndex));
                if (!isWholeWord) {
                    startIndex = endIndex;
                    continue;
                }

                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (cw.getOnClickListener() != null) {
                            cw.getOnClickListener().onClick(widget);
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        try {
                            int color = Color.parseColor(cw.getColor());
                            ds.setColor(color);
                        } catch (Exception e) {
                            ds.setColor(ContextCompat.getColor(context, R.color.Emerald_Green_800));
                        }
                        ds.setUnderlineText(true);
                    }
                }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                startIndex = endIndex;
            }
        }

        // 3. Apply ColoredLines
        if (section.getColoredLines() != null) {
            for (FirestoreModel.ColoredLine cl : section.getColoredLines()) {
                String searchText = cl.getText();
                if (searchText == null || searchText.isEmpty()) continue;

                int startIndex = 0;
                while ((startIndex = fullContentBuilder.indexOf(searchText, startIndex)) != -1) {
                    int endIndex = startIndex + searchText.length();

                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            // No click action needed
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            try {
                                int color = Color.parseColor(cl.getColor());
                                ds.setColor(color);
                            } catch (Exception e) {
                                ds.setColor(ContextCompat.getColor(context, R.color.green));
                            }
                            ds.setUnderlineText(false);
                        }
                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    startIndex = endIndex;
                }
            }
        }

        // 4. Set final content
        holder.lists.setText(spannableString);
        holder.lists.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle expansion
        final boolean isExpanded = position == expandedPosition;
        holder.expandableLayout.setExpanded(isExpanded, false);
        holder.lists.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.title.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return model.getSections().size(); // No change needed here, as the count remains the same
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView introduction;
        TextView lists;
        TextView conclusion;
        ExpandableLayout expandableLayout;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            introduction = itemView.findViewById(R.id.intro);
            lists = itemView.findViewById(R.id.lists);
            conclusion = itemView.findViewById(R.id.conclusion);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }
}
