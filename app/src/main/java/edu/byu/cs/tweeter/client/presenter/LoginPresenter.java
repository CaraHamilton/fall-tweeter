package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter<LoginPresenter.View>{

    private UserService userService;

    public interface View extends ViewBase{
        void startNewActivity(User user);
    }

    public LoginPresenter(View view) {
        super(view);
        userService = new UserService();
    }

    public void login(String alias, String password) {
        validateLogin(alias, password);
        userService.login(alias, password, new LoginObserver());
    }

    public void validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    private class LoginObserver implements UserService.LoginObserver {

        @Override
        public void handleSuccess(User loggedInUser, AuthToken authToken) {
            // Cache user session information

            view.displayMessage("Logging In...");

            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.startNewActivity(loggedInUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to login because of exception: " + ex.getMessage());
        }
    }
}
