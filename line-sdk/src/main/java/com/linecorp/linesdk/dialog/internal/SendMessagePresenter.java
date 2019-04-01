package com.linecorp.linesdk.dialog.internal;

import android.os.AsyncTask;

import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.message.MessageData;

import java.util.ArrayList;
import java.util.List;

public class SendMessagePresenter implements SendMessageContract.Presenter, TargetListAdapter.OnSelectedChangeListener  {
    SendMessageContract.View view;

    private LineApiClient lineApiClient;

    private List<TargetUser> targetUserList = new ArrayList<>();

    private List<AsyncTask> asyncTaskList = new ArrayList<>();

    private static final int  MAX_TARGET_SIZE = 10;

    public SendMessagePresenter(LineApiClient lineApiClient, SendMessageContract.View view) {
        this.lineApiClient = lineApiClient;
        this.view = view;
    }

    @Override
    public void removeTargetUser(TargetUser targetUser) {
        targetUserList.remove(targetUser);
        view.onTargetUserRemoved(targetUser);
    }

    @Override
    public void addTargetUser(TargetUser targetUser) {
        targetUserList.add(targetUser);
        view.onTargetUserAdded(targetUser);
    }

    @Override
    public void sendMessage(MessageData messageData) {
        List messageDataList = new ArrayList<MessageData>() {{ add(messageData); }};
        // no need to put into asyncTask list since we don't need to handle results in foreground.
        new SendMessageTask(lineApiClient, messageDataList).execute(targetUserList);
    }

    @Override
    public void onSelected(TargetUser targetUser, boolean isSelected) {
        if (isSelected) {
            if (targetUserList.size() < MAX_TARGET_SIZE) {
                addTargetUser(targetUser);
            } else {
                view.onTargetUserRemoved(targetUser);
                view.onExceedMaxTargetUserCount(MAX_TARGET_SIZE);
            }
        } else {
            removeTargetUser(targetUser);
        }
    }

    @Override
    public int getTargetUserListSize() {
        return targetUserList.size();
    }

    @Override
    public void destroy() {
        for (AsyncTask task : asyncTaskList) {
            task.cancel(true);
        }
    }

    @Override
    public void getFriends(GetTargetUserTask.NextAction nextAction) {
        getTargets(TargetUser.Type.FRIEND, nextAction);
    }

    @Override
    public void getGroups(GetTargetUserTask.NextAction nextAction) {
        getTargets(TargetUser.Type.GROUP, nextAction);
    }

    private void getTargets(TargetUser.Type type, GetTargetUserTask.NextAction nextAction) {
        GetTargetUserTask task = new GetTargetUserTask(type, lineApiClient, nextAction);
        task.execute();
        asyncTaskList.add(task);
    }
}
