package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{

    public interface GetFollowingObserver extends PagedNotificationObserver<User> { }

    public interface GetFollowersObserver extends PagedNotificationObserver<User> { }

    public interface GetFollowersCountObserver extends ServiceObserver{
        void handleSuccess(int count);
    }

    public interface GetFollowingCountObserver extends ServiceObserver{
        void handleSuccess(int count);
    }

    public interface IsFollowerObserver extends ServiceObserver{
        void handleSuccess(Boolean value);
    }

    public interface FollowObserver extends SimpleNotificationObserver { } //will be able to delete this

    public interface UnfollowObserver extends SimpleNotificationObserver { }

    public void loadMoreItemsFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, GetFollowingObserver getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedNotificationHandler<User>(getFollowingObserver));
        executeSingleThread(getFollowingTask);
    }

    public void loadMoreItemsFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, GetFollowersObserver getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedNotificationHandler<User>(getFollowersObserver));
        executeSingleThread(getFollowersTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, GetFollowersCountObserver getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new GetFollowersCountHandler(getFollowersCountObserver));
        executeSingleThread(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, GetFollowingCountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new GetFollowingCountHandler(getFollowingCountObserver));
        executeSingleThread(followingCountTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, IsFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        executeSingleThread(isFollowerTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, FollowObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        executeSingleThread(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, UnfollowObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        executeSingleThread(unfollowTask);
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends BackgroundTaskHandler<GetFollowersCountObserver> {
        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetFollowersCountObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.handleSuccess(count);
        }
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends BackgroundTaskHandler<GetFollowingCountObserver> {

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetFollowingCountObserver observer, Bundle data) {
            int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
            observer.handleSuccess(count);
        }
    }

    // IsFollowerHandler

    private class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(IsFollowerObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            observer.handleSuccess(isFollower);
        }
    }
}
