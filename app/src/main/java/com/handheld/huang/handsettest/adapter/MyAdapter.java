package com.handheld.huang.handsettest.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.LayoutResultItemBinding;

import java.util.List;


/**
 * @author huang
 * @date 2017/11/20
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Result> mResultList;
    private Context mContext;
    private com.handheld.huang.handsettest.databinding.LayoutResultItemBinding binding;

    public MyAdapter(List<Result> list, Context context) {
        mResultList = list;
        mContext = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = LayoutResultItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new MyViewHolder(binding.getRoot());
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
        TextView mTestResultName;

        MyViewHolder(View view) {
            super(view);
            mTestResultName = view.findViewById(R.id.test_result_name);
        }
    }
}
