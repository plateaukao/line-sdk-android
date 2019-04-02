package com.linecorp.linesdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.linecorp.linesdk.R;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.dialog.internal.SendMessageContract;
import com.linecorp.linesdk.dialog.internal.SendMessagePresenter;
import com.linecorp.linesdk.dialog.internal.SendMessageTargetPagerAdapter;
import com.linecorp.linesdk.dialog.internal.TargetUser;
import com.linecorp.linesdk.dialog.internal.UserThumbnailView;
import com.linecorp.linesdk.message.MessageData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A customized dialog that users can pick friends or groups to send messages
 */
public class SendMessageDialog extends AlertDialog implements SendMessageContract.View {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button buttonConfirm;
    private LinearLayout linearLayoutTargetUser;
    private HorizontalScrollView horizontalScrollView;

    private MessageData message;
    private SendMessageTargetPagerAdapter sendMessageTargetAdapter;
    private Map<String, View> targetUserViewCacheMap = new HashMap<>();

    private LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    private SendMessagePresenter presenter;

    public void setMessage(MessageData message) {
        this.message = message;
    }

    public SendMessageDialog(@NonNull Context context, @NonNull LineApiClient lineApiClient) {
        super(context, R.style.DialogTheme);
        presenter = new SendMessagePresenter(lineApiClient, this);
        sendMessageTargetAdapter = new SendMessageTargetPagerAdapter(context, presenter, presenter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_send_message, null);
        setContentView(content);

        viewPager = content.findViewById(R.id.viewPager);
        tabLayout = content.findViewById(R.id.tabLayout);
        buttonConfirm = content.findViewById(R.id.buttonConfirm);
        linearLayoutTargetUser = content.findViewById(R.id.linearLayoutTargetUserList);
        horizontalScrollView = content.findViewById(R.id.horizontalScrollView);

        setupUi();
    }

    private void setupUi() {
        viewPager.setAdapter(sendMessageTargetAdapter);
        tabLayout.setupWithViewPager(viewPager);

        buttonConfirm.setOnClickListener(confirmClickListener);

        viewPager.post(() -> {
            // In order to be able to show keyboard for search view
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                           | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        });
    }

    private View.OnClickListener confirmClickListener = view -> {
        presenter.sendMessage(message);
        dismiss();
    };

    @Override
    public void dismiss() {
        presenter.release();
        super.dismiss();
    }

    @Override
    public void onTargetUserRemoved(TargetUser targetUser) {
        View targetUserView = targetUserViewCacheMap.get(targetUser.getId());
        linearLayoutTargetUser.removeView(targetUserView);
        sendMessageTargetAdapter.unSelect(targetUser);
        updateConfirmButtonLabel();
    }

    @Override
    public void onTargetUserAdded(TargetUser targetUser) {
        if (targetUserViewCacheMap.get(targetUser.getId()) == null) {
            targetUserViewCacheMap.put(targetUser.getId(), createUserThumbnailView(targetUser));
        }

        View targetUserView = targetUserViewCacheMap.get(targetUser.getId());
        linearLayoutTargetUser.addView(targetUserView, layoutParams);
        // scroll to right
        horizontalScrollView.post(() -> horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
        updateConfirmButtonLabel();
    }

    @NonNull
    private UserThumbnailView createUserThumbnailView(TargetUser targetUser) {
        UserThumbnailView userThumbnailView = new UserThumbnailView(getContext());
        userThumbnailView.setOnClickListener(view -> presenter.removeTargetUser(targetUser));
        userThumbnailView.setTargetUser(targetUser);
        return userThumbnailView;
    }

    @Override
    public void onExceedMaxTargetUserCount(int count) {
        Toast.makeText(getContext(),
                String.format(Locale.getDefault(), "You can only select up to %1$d.", count),
                Toast.LENGTH_LONG)
                .show();
    }

    private void updateConfirmButtonLabel() {
        int targetCount = presenter.getTargetUserListSize();
        if (targetCount == 0) {
            buttonConfirm.setText(android.R.string.ok);
            buttonConfirm.setVisibility(View.GONE);
        } else {
            String text = getContext().getString(android.R.string.ok) + " (" + targetCount + ")";
            buttonConfirm.setText(text);
            buttonConfirm.setVisibility(View.VISIBLE);
        }
    }
}
