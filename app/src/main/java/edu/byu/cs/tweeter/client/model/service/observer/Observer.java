package edu.byu.cs.tweeter.client.model.service.observer;

public interface Observer {

    void handleFailure(String message);
    void handleException(Exception exception);
}
