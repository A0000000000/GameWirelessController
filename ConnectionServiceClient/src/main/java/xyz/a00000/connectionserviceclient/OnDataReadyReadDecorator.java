package xyz.a00000.connectionserviceclient;

public interface OnDataReadyReadDecorator<T> {

    void dataReady(T data);

}
