package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{

    private StatusService statusService;

    public interface View extends ViewPages<Status>{ }

    public StoryPresenter(View view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    public void getItems(User user) {
        statusService.loadMoreItemsStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetStoryObserver());
    }

    private class GetStoryObserver implements StatusService.GetStoryObserver {

        @Override
        public void handleSuccess(List<Status> story, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (story.size() > 0) ? story.get(story.size() - 1) : null;
            view.addItems(story);
            StoryPresenter.this.hasMorePages = hasMorePages;
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get story: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
    }
}
