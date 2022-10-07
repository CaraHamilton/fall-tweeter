package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter<T> {
    public final T view;

    protected Presenter(T view) {this.view = view;}
}
