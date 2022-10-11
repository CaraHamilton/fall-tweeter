package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User>{

    private FollowService followService;

    public interface View extends ViewPages<User> { }

    public FollowersPresenter(View view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    public void getItems(User user) {
        followService.loadMoreItemsFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetFollowersObserver());
    }

    private class GetFollowersObserver implements FollowService.GetFollowersObserver {

        @Override
        public void handleSuccess(List<User> items, boolean value) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
            FollowersPresenter.this.hasMorePages = value;
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get followers: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
    }
}
