package com.linecorp.linesdk.dialog.internal;

import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.message.MessageData;

import java.util.ArrayList;
import java.util.List;

public class SendMessageTask extends android.os.AsyncTask<List<TargetUser>, Void, Void> {
    private LineApiClient lineApiClient;
    private List<MessageData> messageDataList;

    SendMessageTask(LineApiClient lineApiClient, List<MessageData> messageDataList) {
        this.lineApiClient = lineApiClient;
        this.messageDataList = messageDataList;
    }

    @Override
    protected Void doInBackground(List<TargetUser>... targetUsers) {
        List<String> targetUserIds = new ArrayList<>();
        for (TargetUser targetUser : targetUsers[0]) {
            targetUserIds.add(targetUser.getId());
        }
        lineApiClient.sendMessageToMultipleUsers(targetUserIds, messageDataList, true);
        return null;
    }
}
