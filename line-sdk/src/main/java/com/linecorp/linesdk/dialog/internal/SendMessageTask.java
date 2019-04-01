package com.linecorp.linesdk.dialog.internal;

import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.message.MessageData;

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
        for (TargetUser targetUser : lists[0]) {
            lineApiClient.sendMessage(targetUser.getId(), messageDataList);
        }
        return null;
    }
}
