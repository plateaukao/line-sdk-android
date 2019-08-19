package com.linecorp.linesdk.dialog.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.linecorp.linesdk.R;

import java.util.HashMap;

public class SendMessageTargetPagerAdapter extends PagerAdapter {
    private Context context;
    private SendMessagePresenter presenter;
    private TargetListAdapter.OnSelectedChangeListener listener;
    private HashMap<TargetUser.Type, TargetListWithSearchView> viewHashMap = new HashMap<>();

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
        TargetUser.Type targetUserType = TargetUser.Type.values()[position];
        TargetListWithSearchView view = new TargetListWithSearchView(context, listener);
        switch (targetUserType) {
            case FRIEND: {
                presenter.getFriends(view::addTargetUsers);
                break;
            }
            case GROUP: {
                presenter.getGroups(view::addTargetUsers);
                break;
            }
            default:
                return null;
        }
        viewHashMap.put(targetUserType, view);

        container.addView(view);
        return view;
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

    public void unSelect(TargetUser targetUser) {
        TargetUser.Type type = targetUser.getType();
        viewHashMap.get(type).unSelect(targetUser);
    }
}
