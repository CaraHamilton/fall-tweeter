package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.observer.AuthNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthNotificationHandler extends BackgroundTaskHandler<AuthNotificationObserver> {

    public AuthNotificationHandler(AuthNotificationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(AuthNotificationObserver observer, Bundle data) {
        User user = (User) data.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
        observer.handleSuccess(user, authToken);    }
}
