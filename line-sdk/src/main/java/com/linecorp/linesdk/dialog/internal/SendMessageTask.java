package com.linecorp.linesdk.dialog.internal;

import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.message.MessageData;

import java.util.ArrayList;
import java.util.List;

public class SendMessageTask extends android.os.AsyncTask<List<TargetUser>, Void, Void> {
    private LineApiClient lineApiClient;
    private List<MessageData> messageDataList;

    public SendMessageTask(LineApiClient lineApiClient, List<MessageData> messageDataList) {
        this.lineApiClient = lineApiClient;
        this.messageDataList = messageDataList;
    }

    @Override
    protected Void doInBackground(List<TargetUser>... lists) {
        List<String> idList = new ArrayList<>();
        for (TargetUser targetUser : lists[0]) {
            idList.add(targetUser.getId());
        }
        lineApiClient.sendMessageToMultipleUsers(idList, messageDataList);
        return null;
    }
}
