package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {

    private static final int PAGE_SIZE = 10;

    private View view;
    private StatusService statusService;
    private UserService userService;

    private Status lastStatus;
    boolean hasMorePages;
    private boolean isLoading;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(Boolean set);
        void addStatus(List<Status> feed);
        void startNewActivity(User user);
    }

    public FeedPresenter(View view) {
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
    }

    public boolean isLoading() {
        return isLoading;
    }
    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);

        statusService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus, new GetFeedObserver());
    }

    public void getUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserObserver());
    }

    private class GetFeedObserver implements StatusService.GetFeedObserver {

        @Override
        public void addStatus(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            view.addStatus(statuses);
            FeedPresenter.this.hasMorePages = hasMorePages;
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get feed: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
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
