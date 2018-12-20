package com.baymax.android.pagingrecyclerview.datasource;

import java.util.List;
import java.util.Map;

public interface DataRepository<T> {

    public List<T> loadData(Map<String, Object> params);
}
