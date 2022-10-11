package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    private StatusService statusService;

    public interface View extends ViewPages<Status>{ }

    public FeedPresenter(View view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    public void getItems(User user) {
        statusService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetFeedObserver());
    }

    private class GetFeedObserver implements StatusService.GetFeedObserver {

        @Override
        public void handleSuccess(List<Status> items, boolean value) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
            hasMorePages = value;
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
}
