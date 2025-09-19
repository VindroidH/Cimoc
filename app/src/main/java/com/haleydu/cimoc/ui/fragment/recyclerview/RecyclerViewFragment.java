package com.haleydu.cimoc.ui.fragment.recyclerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haleydu.cimoc.R;
import com.haleydu.cimoc.ui.adapter.BaseAdapter;
import com.haleydu.cimoc.ui.fragment.BaseFragment;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Hiroshi on 2016/10/11.
 */

public abstract class RecyclerViewFragment extends BaseFragment implements BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener {

    protected RecyclerView mRecyclerView;

    @Override
    protected void initView() {
        mRecyclerView = mView.findViewById(R.id.recycler_view_content);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(initLayoutManager());
        BaseAdapter adapter = initAdapter();
        if (adapter != null) {
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
            mRecyclerView.addItemDecoration(adapter.getItemDecoration());
            mRecyclerView.setAdapter(adapter);
        }
    }

    abstract protected BaseAdapter initAdapter();

    protected abstract RecyclerView.LayoutManager initLayoutManager();

    @Override
    public void onItemClick(View view, int position) {
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_recycler_view;
    }

}
