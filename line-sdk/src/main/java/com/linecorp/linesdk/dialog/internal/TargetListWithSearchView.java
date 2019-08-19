package com.linecorp.linesdk.dialog.internal;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.View;

import com.linecorp.linesdk.R;

import java.util.List;

public class TargetListWithSearchView extends ConstraintLayout {

    private TargetListAdapter.OnSelectedChangeListener listener;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private AppCompatTextView emptyView;

    public TargetListWithSearchView(
            Context context,
            TargetListAdapter.OnSelectedChangeListener listener) {
        super(context);
        this.listener = listener;
        init();
    }

    public TargetListWithSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetListWithSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void addTargetUsers(List<TargetUser> targetUsers) {
        TargetListAdapter targetListAdapter = (TargetListAdapter) recyclerView.getAdapter();
        if (targetListAdapter == null) {
            TargetListAdapter adapter = new TargetListAdapter(targetUsers, listener);
            recyclerView.setAdapter(adapter);
        } else {
            targetListAdapter.addAll(targetUsers);
        }
    }

    public void unSelect(TargetUser targetUser) {
        TargetListAdapter targetListAdapter = (TargetListAdapter) recyclerView.getAdapter();
        if (targetListAdapter == null) {
            return;
        }

        targetListAdapter.unSelect(targetUser);
    }

    private void init() {
        View layoutSelectTarget = inflate(getContext(), R.layout.layout_select_target, this);

        recyclerView = layoutSelectTarget.findViewById(R.id.recyclerView);
        searchView = layoutSelectTarget.findViewById(R.id.searchView);
        emptyView = layoutSelectTarget.findViewById(R.id.emptyView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText(newText);
                return true;
            }

            private void searchText(String query) {
                TargetListAdapter adapter = ((TargetListAdapter)recyclerView.getAdapter());
                if (adapter != null) {
                    int filteredCount = adapter.filter(query);
                    if (filteredCount == 0) {
                        emptyView.setText(R.string.search_no_results);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
