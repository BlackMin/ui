package com.baymax.android.pagingrecyclerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PagedList.Config;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.baymax.android.pagingrecyclerview.LoadingStateView.LOADING_STATE;
import com.baymax.android.pagingrecyclerview.datasource.DataRepository;
import com.baymax.android.pagingrecyclerview.datasource.PageDataSource;
import java.util.HashMap;

public abstract class BasePagingListFragment<T> extends Fragment {

    private LoadingStateView loadingStateView;

    private RecyclerView recyclerView;

    private PagedListAdapter pageAdapter;

    private PageDataSource<T> pageDataSource;

    private DataRepository<T> dataRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paging_list, container, false);
        loadingStateView = view.findViewById(R.id.loading_state_view);
        loadingStateView.setRetryClickListener(v -> {
            loadingStateView.setLoadingState(LOADING_STATE.STATE_LOADING);
            //loadData();
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(getLayoutManager());
        if(getItemDecoration() != null) {
            recyclerView.addItemDecoration(getItemDecoration());
        }
        recyclerView.setAdapter(pageAdapter = getPageAdapter());
        LiveData<PagedList> liveData = new LivePagedListBuilder(getDataSourceFactory(), getPageConfig()).build();
        liveData.observe(getActivity(), new Observer<PagedList>() {
            @Override
            public void onChanged(PagedList pagedList) {
                pageAdapter.submitList(pagedList);
            }
        });
    }

    public @NonNull LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    public ItemDecoration getItemDecoration() {
        return new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
    }

    private DataSource.Factory getDataSourceFactory() {
        return new DataSource.Factory<Integer,T>() {
            @NonNull
            @Override
            public DataSource<Integer, T> create() {
                return new PageDataSource<>(dataRepository = getDataRepository(), new HashMap<>());
            }
        };
    }

    public @NonNull Config getPageConfig() {
        return new Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPrefetchDistance(5)
                .build();
    }

    public abstract @NonNull DataRepository<T> getDataRepository();

    public abstract @NonNull PagedListAdapter getPageAdapter();



}
