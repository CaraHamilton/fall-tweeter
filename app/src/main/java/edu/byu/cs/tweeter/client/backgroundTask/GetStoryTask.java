package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.util.FakeData;
import edu.byu.cs.tweeter.client.util.Pair;
import edu.byu.cs.tweeter.shared.domain.AuthToken;
import edu.byu.cs.tweeter.shared.domain.Status;
import edu.byu.cs.tweeter.shared.domain.User;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask implements Runnable {
    private static final String LOG_TAG = "GetStoryTask";

    public static final String SUCCESS_KEY = "success";
    public static final String STATUSES_KEY = "statuses";
    public static final String MORE_PAGES_KEY = "more-pages";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose story is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private int limit;
    private Status lastStatus;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastStatus = lastStatus;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            Pair<List<Status>, Boolean> pageOfStatus = FakeData.getPageOfStatus(lastStatus, limit);
            List<Status> statuses = pageOfStatus.getFirst();
            boolean hasMorePages = pageOfStatus.getSecond();

            for (Status s : statuses) {
                TaskUtils.loadImage(s.getUser());
            }

            sendSuccessMessage(statuses, hasMorePages);

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    private void sendSuccessMessage(List<Status> statuses, boolean hasMorePages) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putSerializable(STATUSES_KEY, (Serializable) statuses);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

    private void sendFailedMessage(String message) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, false);
        msgBundle.putString(MESSAGE_KEY, message);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

    private void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, false);
        msgBundle.putSerializable(EXCEPTION_KEY, exception);

        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }
}