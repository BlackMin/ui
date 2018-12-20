package com.baymax.android.pagingrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import com.baymax.android.pagingrecyclerview.adapter.TestPageAdapter;
import com.baymax.android.pagingrecyclerview.datasource.DataRepository;
import com.baymax.android.pagingrecyclerview.test.TestEntity;
import java.util.List;
import java.util.Map;

public class MainFragment extends BasePagingListFragment<TestEntity> {

    @NonNull
    @Override
    public DataRepository<TestEntity> getDataRepository() {
        return new DataRepository<TestEntity>() {
            @Override
            public List<TestEntity> loadData(Map<String, Object> params) {
                int page = (int) params.get("page");
                if(page == 5) {
                    return null;
                }
                return TestEntity.mockData(page);
            }
        };
    }

    @NonNull
    @Override
    public PagedListAdapter getPageAdapter() {
        return new TestPageAdapter(getContext(), new ItemCallback<TestEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull TestEntity oldItem,
                    @NonNull TestEntity newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull TestEntity oldItem,
                    @NonNull TestEntity newItem) {
                return (oldItem.name != null && oldItem.name.equals(newItem.name)) || (
                        oldItem.name == null
                                && newItem.name == null);
            }
        });
    }




}
