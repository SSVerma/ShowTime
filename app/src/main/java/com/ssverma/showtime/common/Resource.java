package com.ssverma.showtime.common;

public class Resource<T> {

    private final T data;
    private final Throwable throwable;

    public Resource(T data, Throwable throwable) {
        this.data = data;
        this.throwable = throwable;
    }

    public Resource(T data) {
        this(data, null);
    }

    public Resource(Throwable throwable) {
        this(null, throwable);
    }

    public boolean isSuccess() {
        return data != null && throwable == null;
    }

    public T getData() {
        if (throwable == null) {
            return data;
        }
        throw new IllegalStateException("Always check first isSuccess(). Exception: " + throwable);
    }

    public Throwable getThrowable() {
        if (data == null) {
            return throwable;
        }
        throw new IllegalStateException("Always check first isSuccess(). Data: " + data);
    }
}
