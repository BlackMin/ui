package com.baymax.android.pagingrecyclerview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.baymax.android.pagingrecyclerview.test.TestBaseDatabindingListFragment;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, new TestBaseDatabindingListFragment()).commit();
    }
}
