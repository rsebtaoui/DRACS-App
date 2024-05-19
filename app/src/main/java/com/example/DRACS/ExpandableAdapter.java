package com.example.DRACS;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {
    private List<Item> items;
    private int expandedPosition = -1;
    private Context context;

    public ExpandableAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expandable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.title.setText(item.getTitle());

        // Create a SpannableString for the content
        SpannableString spannableString = new SpannableString(item.getlists());

        // Set ClickableSpan for each clickable word
        for (Item.ClickableWord clickableWord : item.getClickableWords()) {
            int startIndex = item.getlists().indexOf(clickableWord.getWord());
            int endIndex = startIndex + clickableWord.getWord().length();
            if (startIndex != -1) {
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        clickableWord.getOnClickListener().onClick(widget);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(context,R.color.Emerald_Green_700)); // Set your custom color here
                        ds.setUnderlineText(false); // Optional: disable underline
                    }
                }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        holder.intro.setText(item.getintro());
        holder.lists.setText(spannableString);
        holder.conclu.setText(item.getconclu());

        holder.lists.setMovementMethod(LinkMovementMethod.getInstance()); // Enable clickable links

        final boolean isExpanded = holder.getAdapterPosition() == expandedPosition;
        holder.expandableLayout.setExpanded(isExpanded, false);
        holder.intro.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.lists.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.conclu.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                notifyItemRangeChanged(0, items.size()); // Notify all items to ensure proper expansion/collapse
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView intro;
        public TextView lists;
        public TextView conclu;
        public ExpandableLayout expandableLayout;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            intro = view.findViewById(R.id.intro);
            lists = view.findViewById(R.id.lists);
            conclu = view.findViewById(R.id.conclusion);
            expandableLayout = view.findViewById(R.id.expandableLayout);
        }
    }
}
