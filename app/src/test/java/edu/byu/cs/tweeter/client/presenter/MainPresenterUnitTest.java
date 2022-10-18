package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private StatusService mockStatusService;
    private Status mockStatus;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup()
    {
        // Create mocks
        mockView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockStatus = Mockito.mock(Status.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();

    }

    @Test
    public void testPostStatus_postSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2, StatusService.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        runPostStatus(answer);
        verifyResult("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postFailed() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2, StatusService.PostStatusObserver.class);
                observer.handleFailure("something bad happened");
                return null;
            }
        };

        runPostStatus(answer);
        verifyResult("Failed to post status: something bad happened");
    }

    @Test
    public void testPostStatus_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2, StatusService.PostStatusObserver.class);
                observer.handleException(new Exception("Test Exception"));
                return null;
            }
        };

        runPostStatus(answer);
        verifyResult("Failed to post status because of exception: Test Exception");

    }

    private void runPostStatus(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);
    }

    private void verifyResult(String s) {
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView).displayMessage(s);
    }

}
