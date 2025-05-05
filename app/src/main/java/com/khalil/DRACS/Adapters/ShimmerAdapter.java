package com.khalil.DRACS.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.khalil.DRACS.R;

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {
    private static final int ITEM_COUNT = 5; // Number of shimmer items to show

    @Override
    public ShimmerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shimmer, parent, false);
        return new ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShimmerViewHolder holder, int position) {
        // No binding needed for shimmer items
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
