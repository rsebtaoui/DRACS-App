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
    private final Map<String, FirestoreModel.Section> sections;
    private String expandedSectionId = null;
    private static final String TAG = "ExpandableAdapter";
    private RecyclerView recyclerView;

    public ExpandableAdapter(Context context, Map<String, FirestoreModel.Section> sections) {
        this.context = context;
        this.sections = sections;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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
        List<Map.Entry<String, FirestoreModel.Section>> sectionEntries = new ArrayList<>(sections.entrySet());
        sectionEntries.sort(Comparator.comparingInt(a -> a.getValue().getOrder()));
        Map.Entry<String, FirestoreModel.Section> sectionEntry = sectionEntries.get(position);
        FirestoreModel.Section section = sectionEntry.getValue();

        holder.title.setText("▼ " + sectionEntry.getKey());

        // 1. Build FULL content: intro + dashes + conclusion
        StringBuilder fullContentBuilder = new StringBuilder();
        if (section.getIntroduction() != null) {
            fullContentBuilder.append(section.getIntroduction()).append("\n\n");
        }

        if (section.getDashes() != null) {
            for (String dash : section.getDashes()) {
                fullContentBuilder.append("• ").append(dash).append("\n");
            }
            fullContentBuilder.append("\n");
        }

        if (section.getConclusion() != null) {
            fullContentBuilder.append(section.getConclusion());
        }

        // 2. Create SpannableString for clickable words
        SpannableString spannableString = new SpannableString(fullContentBuilder.toString());
        int startIndex = 0;

        // 3. Add clickable spans for clickable words
        if (section.getClickableWords() != null) {
            for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
                int endIndex = spannableString.toString().indexOf(cw.getText(), startIndex);
                if (endIndex != -1) {
                    ClickableSpan clickableSpan = new ClickableSpan() {
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
                    };
                    spannableString.setSpan(clickableSpan, endIndex, endIndex + cw.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startIndex = endIndex + cw.getText().length();
                }
            }
        }

        // 4. Set final content
        holder.lists.setText(spannableString);
        holder.lists.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle expansion
        final boolean isExpanded = sectionEntry.getKey().equals(expandedSectionId);
        holder.expandableLayout.setExpanded(isExpanded, false);
        holder.lists.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.title.setOnClickListener(v -> {
            expandedSectionId = isExpanded ? null : sectionEntry.getKey();
            notifyDataSetChanged();
        });
    }

    public void expandSection(String sectionId) {
        if (sectionId != null) {
            expandedSectionId = sectionId;
            // Find the position of the section to update it
            int position = 0;
            for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
                if (entry.getKey().equals(sectionId)) {
                    // Force the expandable layout to expand
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder != null) {
                        holder.expandableLayout.setExpanded(true, true);
                        holder.lists.setVisibility(View.VISIBLE);
                    }
                    notifyItemChanged(position);
                    break;
                }
                position++;
            }
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
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
