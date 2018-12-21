package com.baymax.android.pagingrecyclerview.test;

import androidx.annotation.NonNull;
import com.baymax.android.pagingrecyclerview.BaseDataBindingPagingListFragment;
import com.baymax.android.pagingrecyclerview.R;
import io.reactivex.Observable;
import io.reactivex.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import com.baymax.android.pagingrecyclerview.BR;

public class TestBaseDatabindingListFragment extends BaseDataBindingPagingListFragment {

    @Override
    public Observable<List<BaseItemViewModel>> getObservable(Map<String, Object> params) {
        final int page = (int) params.get("page");
        return new Observable<List<TestEntity>>() {
            @Override
            protected void subscribeActual(Observer<? super List<TestEntity>> observer) {
                observer.onNext(TestEntity.mockData(page));
                observer.onComplete();
            }
        }.map(result->{
            List<BaseItemViewModel> models =new ArrayList<>();
            if(result != null) {
                for (TestEntity entity : result) {
                    if(entity.id % 5 == 0) {
                        models.add(new TestTitleViewModel("第"+entity.id/5+"组数据"));
                    }
                    models.add(new TestItemViewModel(entity));
                }
            }
            return models;
        });
    }

    @Override
    public boolean itemsIsSame(@NonNull BaseItemViewModel oldItem,
            @NonNull BaseItemViewModel newItem) {
        return false;
    }

    @Override
    public boolean contentsIsSame(@NonNull BaseItemViewModel oldItem,
            @NonNull BaseItemViewModel newItem) {
        return false;
    }

    public class TestItemViewModel implements BaseItemViewModel {

        public TestEntity test;

        public TestItemViewModel(TestEntity test) {
            this.test = test;
        }

        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding) {
            itemBinding.set(BR.itemViewModel, R.layout.view_databinding_list_item_test);
        }
    }

    public class TestTitleViewModel implements BaseItemViewModel {

        public String title;

        public TestTitleViewModel(String title) {
            this.title = title;
        }

        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding) {
            itemBinding.set(BR.itemViewModel, R.layout.view_databinding_list_item_title);
        }
    }
}
