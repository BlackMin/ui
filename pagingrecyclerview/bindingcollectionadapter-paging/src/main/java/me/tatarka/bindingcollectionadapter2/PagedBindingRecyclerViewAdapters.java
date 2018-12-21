package me.tatarka.bindingcollectionadapter2;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.RecyclerView;
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffPagedObservableList;
import me.tatarka.bindingcollectionadapter2.paging.R;

/**
 * @see {@link BindingCollectionAdapters}
 */
public class PagedBindingRecyclerViewAdapters {
    // RecyclerView
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"itemBinding", "pageItems", "adapter", "itemIds", "viewHolder", "diffConfig"}, requireAll = false)
    public static <T> void setAdapter(RecyclerView recyclerView,
                                      ItemBinding<T> itemBinding,
                                      PagedList<T> pageItems,
                                      BindingRecyclerViewAdapter<T> adapter,
                                      BindingRecyclerViewAdapter.ItemIds<? super T> itemIds,
                                      BindingRecyclerViewAdapter.ViewHolderFactory viewHolderFactory,
                                      AsyncDifferConfig<T> diffConfig) {
        if (itemBinding == null) {
            throw new IllegalArgumentException("itemBinding must not be null");
        }
        BindingRecyclerViewAdapter oldAdapter = (BindingRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            if (oldAdapter == null) {
                adapter = new BindingRecyclerViewAdapter<>();
            } else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);

        if (diffConfig != null && pageItems != null) {
            AsyncDiffPagedObservableList<T> list = (AsyncDiffPagedObservableList<T>) recyclerView.getTag(R.id.bindingcollectiondapter_list_id);
            if (list == null) {
                list = new AsyncDiffPagedObservableList<>(diffConfig);
                recyclerView.setTag(R.id.bindingcollectiondapter_list_id, list);
                adapter.setItems(list);
            }
            list.update(pageItems);
        } else {
            adapter.setItems(pageItems);
        }

        adapter.setItemIds(itemIds);
        adapter.setViewHolderFactory(viewHolderFactory);

        if (oldAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }
    }
}
