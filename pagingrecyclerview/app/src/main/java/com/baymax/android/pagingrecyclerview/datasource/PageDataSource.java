package com.baymax.android.pagingrecyclerview.datasource;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageDataSource<T> extends PageKeyedDataSource<Integer, T> {

    public static String TAG = "PAGE_DATA_SOURCE";

    private DataRepository<T> dataRepository;

    private Map<String, Object> requestParams;

    public PageDataSource(@NonNull DataRepository<T> dataRepository,
            Map<String, Object> requestParams) {
        this.dataRepository = dataRepository;
        this.requestParams = (requestParams == null ? new HashMap<>() : requestParams);
    }

    public void updateParams(String key, Object value) {
        requestParams.put(key, value);
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, T> callback) {
        Log.i(TAG, "loadInitial size:{page:0,pagesize:" + params.requestedLoadSize + "}"+Thread.currentThread());
        requestParams.put("page", 0);
        requestParams.put("pageSize", params.requestedLoadSize);
        List<T> data = dataRepository.loadData(requestParams);
        callback.onResult(data, null, 1);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, T> callback) {
        Log.i(TAG, "load before size:{page:" + params.key + ",pagesize:" + params.requestedLoadSize
                + "}");
        requestParams.put("page", params.key);
        List<T> data = dataRepository.loadData(requestParams);
        if (data != null && data.size() > 0) {
            callback.onResult(data, params.key - 1);
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, T> callback) {
        Log.i(TAG, "load after size:{page:" + params.key + ",pagesize:" + params.requestedLoadSize
                + "}"+Thread.currentThread());
        requestParams.put("page", params.key);
        List<T> data = dataRepository.loadData(requestParams);
        if (data != null && data.size() > 0) {
            callback.onResult(data, params.key + 1);
        }
    }
}
