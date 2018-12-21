package com.baymax.android.pagingrecyclerview.purepaging.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.baymax.android.pagingrecyclerview.R;
import com.baymax.android.pagingrecyclerview.purepaging.TestEntity;

public class TestPageAdapter extends PagedListAdapter<TestEntity,TestPageAdapter.TestItemViewHolder> {

    private Context context;

    public TestPageAdapter(Context context,
            @NonNull ItemCallback<TestEntity> diffCallback) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @Override
    public TestItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestItemViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.view_list_item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TestItemViewHolder holder, int position) {
        holder.bindItem(getItem(position));
    }

    public class TestItemViewHolder extends ViewHolder {

        private TextView idView;

        private TextView nameView;

        public TestItemViewHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.tv_id);
            nameView = itemView.findViewById(R.id.tv_name);
        }

        public void bindItem(TestEntity data) {
            idView.setText("id:" + data.id);
            nameView.setText("name:" + data.name);
        }
    }

}
