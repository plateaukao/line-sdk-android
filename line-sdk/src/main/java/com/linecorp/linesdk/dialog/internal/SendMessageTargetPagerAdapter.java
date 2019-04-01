package com.linecorp.linesdk.dialog.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linecorp.linesdk.R;

import java.util.HashMap;
import java.util.List;

public class SendMessageTargetPagerAdapter extends PagerAdapter {
    private Context context;
    private SendMessagePresenter presenter;
    private TargetListAdapter.OnSelectedChangeListener listener;
    private HashMap<TargetUser.Type, RecyclerView> viewHashMap = new HashMap<>();

    public SendMessageTargetPagerAdapter(Context context,
                                         SendMessagePresenter presenter,
                                         TargetListAdapter.OnSelectedChangeListener listener) {
        this.context = context;
        this.presenter= presenter;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return TargetUser.getTargetTypeCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View item;
        switch (TargetUser.Type.values()[position]) {
            case FRIEND: {
                item = createFriendsView();
                RecyclerView recyclerView = item.findViewById(R.id.recyclerView);
                viewHashMap.put(TargetUser.Type.FRIEND, recyclerView);
                break;
            }
            case GROUP: {
                item = createGroupsView();
                RecyclerView recyclerView = item.findViewById(R.id.recyclerView);
                viewHashMap.put(TargetUser.Type.GROUP, recyclerView);
                break;
            }
            default:
                return null;
        }

        container.addView(item);
        return item;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(TargetUser.Type.values()[position]) {
            case FRIEND:
                return context.getString(R.string.select_tab_friends);
            case GROUP:
                return context.getString(R.string.select_tab_groups);
            default:
                return "";
        }
    }

    public void removeTargetUser(TargetUser targetUser) {
        TargetUser.Type type = targetUser.getType();
        ((TargetListAdapter)viewHashMap.get(type).getAdapter()).unSelectUser(targetUser);
    }

    private View createTargetView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layoutSelectTarget = inflater.inflate(R.layout.layout_select_target, null, false);
        RecyclerView recyclerView = layoutSelectTarget.findViewById(R.id.recyclerView);
        SearchView searchView = layoutSelectTarget.findViewById(R.id.searchView);

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
                    adapter.filter(query);
                }
            }
        });

        return layoutSelectTarget;
    }

    private void addTargetUsersToRecyclerView(RecyclerView recyclerView, List<TargetUser> targetUsers) {
        TargetListAdapter targetListAdapter = (TargetListAdapter) recyclerView.getAdapter();
        if (targetListAdapter == null) {
            TargetListAdapter adapter = new TargetListAdapter(targetUsers, listener);
            recyclerView.setAdapter(adapter);
        } else {
            targetListAdapter.addAll(targetUsers);
        }
    }

    private View createFriendsView() {
        View layoutSelectTarget = createTargetView();
        RecyclerView recyclerView = layoutSelectTarget.findViewById(R.id.recyclerView);
        presenter.getFriends(list -> addTargetUsersToRecyclerView(recyclerView, list));

        return layoutSelectTarget;
    }

    private View createGroupsView() {
        View layoutSelectTarget = createTargetView();
        RecyclerView recyclerView = layoutSelectTarget.findViewById(R.id.recyclerView);
        presenter.getGroups(list -> addTargetUsersToRecyclerView(recyclerView, list));

        return layoutSelectTarget;
    }
}
