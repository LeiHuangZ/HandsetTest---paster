package com.handheld.huang.handsettest.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author huang
 * @date 2017/11/20
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Result> mResultList;
    private Context mContext;

    public MyAdapter(List<Result> list, Context context) {
        mResultList = list;
        mContext = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Result result = mResultList.get(position);
        holder.mTestResultName.setText(result.getName());
        if (result.getIsCheck() == 0){
            holder.mTestResultName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.yes), null, null, null);
        }else if (result.getIsCheck() == 1){
            holder.mTestResultName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.no), null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.test_result_name)
        TextView mTestResultName;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
