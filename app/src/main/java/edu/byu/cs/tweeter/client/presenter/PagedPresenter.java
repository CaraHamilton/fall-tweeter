package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.ViewPages> {

    public abstract void getItems(User user);

    public static final int PAGE_SIZE = 10;

    public UserService userService;
    
    public T lastItem;
    public boolean hasMorePages = true;
    public boolean isLoading = false;

    protected PagedPresenter(ViewPages<T> view) {
        super(view);
        userService = new UserService();
    }

    public interface ViewPages<T> {
        void displayMessage(String message);
        void setLoadingFooter(Boolean value);
        void startNewActivity(User user);
        void addItems(List<T> items);
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(user);
        }
    }

    public void getUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.displayMessage("Getting user's profile...");
            view.startNewActivity(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }

}
