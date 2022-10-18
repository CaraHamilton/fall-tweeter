package edu.byu.cs.tweeter.client.presenter;

import androidx.annotation.Nullable;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public abstract class PresenterUnitTest {

    abstract void handleObserver();

    @Nullable
    Answer<Void> getAnswer() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2, StatusService.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };
        return answer;
    }
}
