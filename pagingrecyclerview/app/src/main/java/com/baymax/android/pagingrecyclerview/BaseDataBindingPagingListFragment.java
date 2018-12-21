package com.baymax.android.pagingrecyclerview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.DataSource;
import androidx.paging.DataSource.Factory;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.baymax.android.pagingrecyclerview.LoadingStateView.LOADING_STATE;
import com.baymax.android.pagingrecyclerview.databinding.FragmentDatabindingPagingListBinding;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffPagedObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.ItemBindingModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindModel;

public abstract class BaseDataBindingPagingListFragment extends Fragment {

    public static final int PAGE_SIZE = 20;
    private FragmentDatabindingPagingListBinding binding;

    private MutableLiveData<Integer> mutableLoadingState = new MutableLiveData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentDatabindingPagingListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(getViewModel());
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mutableLoadingState.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                binding.getViewModel().loadingState.set(state!=null?state:LOADING_STATE.STATE_SUCCESS);
            }
        });
    }

    //TODO : 继承ViewModel 通过ViewModelProviders创建DefaultViewModel实例
    //需要DefaultViewModel是静态内部类 或者 非内部类
    public DefaultViewModel getViewModel() {
        return new DefaultViewModel();
    }

    public class DefaultViewModel {

        public ItemBinding itemBinding = ItemBinding.of(new OnItemBindModel<BaseItemViewModel>() {
            @Override
            public void onItemBind(ItemBinding itemBinding, int position,
                    BaseItemViewModel item) {
                super.onItemBind(itemBinding, position, item);
            }
        });

        public ObservableInt loadingState = new ObservableInt(LOADING_STATE.STATE_SUCCESS);

        private Map<String, Object> pageParams = new HashMap<>();

        public AsyncDifferConfig<BaseItemViewModel> diffConfig = new AsyncDifferConfig.Builder<BaseItemViewModel>(
                new ItemCallback<BaseItemViewModel>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull BaseItemViewModel oldItem,
                            @NonNull BaseItemViewModel newItem) {
                        return false;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull BaseItemViewModel oldItem,
                            @NonNull BaseItemViewModel newItem) {
                        return false;
                    }
                }).build();

        public LiveData<PagedList<BaseItemViewModel>> items = new LivePagedListBuilder(
                new Factory<Integer,BaseItemViewModel>() {

                    @NonNull
                    @Override
                    public DataSource<Integer, BaseItemViewModel> create() {
                        return new PageKeyedDataSource<Integer, BaseItemViewModel>() {
                            //方法执行非UI线程
                            @Override
                            public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                                    @NonNull LoadInitialCallback<Integer, BaseItemViewModel> callback) {
                                Log.i("jhy", "loadInitial size:{page:0,pagesize:" + params.requestedLoadSize + "}");
                                pageParams.put("page",0);
                                pageParams.put("pageSize",PAGE_SIZE);
                                mutableLoadingState.postValue(LOADING_STATE.STATE_LOADING);
                                getObservable(pageParams).subscribe(result->{
                                    if(result != null && result.size() > 0) {
                                        mutableLoadingState.postValue(LOADING_STATE.STATE_SUCCESS);
                                        callback.onResult(result,null,1);
                                    }else {
                                        mutableLoadingState.postValue(LOADING_STATE.STATE_EMPTY);
                                        callback.onResult(result,null,null);
                                    }
                                },e->{
                                    mutableLoadingState.postValue(LOADING_STATE.STATE_ERROR);
                                    e.printStackTrace();
                                });
                            }

                            @Override
                            public void loadBefore(@NonNull LoadParams<Integer> params,
                                    @NonNull LoadCallback<Integer, BaseItemViewModel> callback) {
                                //ignored this method, because pagekeyeddatasource save all data in memory and never drop any data.
                            }

                            //方法执行非UI线程
                            @Override
                            public void loadAfter(@NonNull LoadParams<Integer> params,
                                    @NonNull LoadCallback<Integer, BaseItemViewModel> callback) {
                                Log.i("jhy", "load after size:{page:" + params.key + ",pagesize:" + params.requestedLoadSize
                                        + "}");
                                pageParams.put("page",params.key);
                                getObservable(pageParams).subscribe(result->{
                                    mutableLoadingState.postValue(LOADING_STATE.STATE_SUCCESS);
                                    if(result != null && result.size() > 0) {
                                        callback.onResult(result,params.key+1);
                                    }else {
                                        callback.onResult(result,null);
                                    }
                                },e->{
                                    mutableLoadingState.postValue(LOADING_STATE.STATE_ERROR);
                                    e.printStackTrace();
                                });
                            }
                        };
                    }
                },
                new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(PAGE_SIZE)
                        .setEnablePlaceholders(false)
                        .setPageSize(PAGE_SIZE).build()).build();
        public BindingRecyclerViewAdapter<BaseItemViewModel> adapterFactory = new BindingRecyclerViewAdapter<>();

        public DefaultViewModel() {
            if (getAdapterFactory() != null) {
                this.adapterFactory = getAdapterFactory();
            }
        }

        public LayoutManager layoutManager() {
            return new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        }

        public ItemDecoration itemDecoration() {
            return new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        }

        public BindingRecyclerViewAdapter<BaseItemViewModel> getAdapterFactory() {
            return null;
        }

    }

    public interface BaseItemViewModel extends ItemBindingModel {

    }

    public abstract Observable<List<BaseItemViewModel>> getObservable(Map<String, Object> params);

    public abstract boolean itemsIsSame(@NonNull BaseItemViewModel oldItem,
            @NonNull BaseItemViewModel newItem);

    public abstract boolean contentsIsSame(@NonNull BaseItemViewModel oldItem,
            @NonNull BaseItemViewModel newItem);
}
