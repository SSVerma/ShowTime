package com.ssverma.showtime.data;

public class NetworkState {

    public static final NetworkState LOADING = new NetworkState(Status.RUNNING);
    public static final NetworkState LOADED = new NetworkState(Status.SUCCESS);

    private Status status;
    private String message;

    public NetworkState(Status status) {
        this.status = status;
    }

    public NetworkState(Status status, String message) {
        this.message = message;
    }

    public static NetworkState error(String errorMessage) {
        return new NetworkState(Status.FAILED, errorMessage);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

}
