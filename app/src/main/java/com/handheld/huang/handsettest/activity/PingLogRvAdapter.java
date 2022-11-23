package com.handheld.huang.handsettest.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LeiHuang
 */
public class PingLogRvAdapter extends RecyclerView.Adapter<PingLogRvAdapter.PingLogRvViewHolder> {
    public final List<String> lines = new ArrayList<>();

    public void addLog(String paramString) {
        this.lines.add(paramString);
        notifyItemInserted(this.lines.size() - 1);
    }

    public void clearLog() {
        int i = this.lines.size();
        this.lines.clear();
        notifyItemRangeRemoved(0, i);
    }

    @Override
    public int getItemCount() {
        return this.lines.size();
    }

    @Override
    public void onBindViewHolder(PingLogRvViewHolder paramPingLogRvViewHolder, int paramInt) {
        paramPingLogRvViewHolder.textView.setText(this.lines.get(paramInt));
    }

    @NonNull
    @Override
    public PingLogRvViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new PingLogRvViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, paramViewGroup, false));
    }

    public static class PingLogRvViewHolder extends RecyclerView.ViewHolder {
        public TextView textView = this.itemView.findViewById(android.R.id.text1);

        public PingLogRvViewHolder(View param1View) {
            super(param1View);
        }
    }
}
