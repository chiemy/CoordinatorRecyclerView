package com.hotbody.coordinatorrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: chiemy
 * Date: 17/4/24
 * Description:
 */

public class RecyclerViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new OverScrollListenerLinearLayoutManager(getActivity()));
        int size  = 20;
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(String.valueOf(i));
        }
        recyclerView.setAdapter(new MyAdapter(list));
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> mList;
        MyAdapter(List<String> list) {
            mList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return MyViewHolder.create(parent);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv;
        public static MyViewHolder create(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(20, 20, 20, 20);
            return new MyViewHolder(textView);
        }

        MyViewHolder(View itemView) {
            super(itemView);
            mTv = (TextView) itemView;
        }

        public void bind(String text) {
            mTv.setText(text);
        }
    }

}
