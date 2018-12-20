package com.baymax.android.pagingrecyclerview.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestEntity implements Serializable {

    public long id;

    public String name;

    public TestEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<TestEntity> mockData(int page) {
        List<TestEntity> testEntities = new ArrayList<>();
        for (long i = page * 20; i < (page+1)*20; i++) {
            testEntities.add(new TestEntity(i+1,"test"+(i+1)));
        }
        return testEntities;
    }
}
