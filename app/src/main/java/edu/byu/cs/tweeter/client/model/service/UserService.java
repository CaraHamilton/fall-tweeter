package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service{

    public interface GetUserObserver extends ServiceObserver {
        void handleSuccess(User user);
    }
    public interface LoginObserver extends AuthNotificationObserver { }

    public interface RegisterObserver extends AuthNotificationObserver { }

    public interface LogoutObserver extends SimpleNotificationObserver { }

    public void getUser(AuthToken currUserAuthToken, String userAliasString, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken,
                userAliasString, new GetUserHandler(getUserObserver));
        executeSingleThread(getUserTask);
    }

    public void login(String alias, String password, LoginObserver loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password,
                new AuthNotificationHandler(loginObserver));
        executeSingleThread(loginTask);

    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new AuthNotificationHandler(observer));
        executeSingleThread(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, LogoutObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new SimpleNotificationHandler(logoutObserver));
        executeSingleThread(logoutTask);
    }

        /**
         * Message handler (i.e., observer) for GetUserTask.
         */
    private class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {

        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.handleSuccess(user);
        }
    }

}
