package xyz.a00000.connectionserviceclient;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import xyz.a00000.connectionserviceclient.internal.ConnectionServiceClient;
import xyz.a00000.connectionserviceclient.internal.OnReadException;
import xyz.a00000.connectionserviceclient.utils.JsonUtils;

public class ConnectionServiceClientDecorator<T>implements Closeable {

    private String name;
    private ConnectionServiceClient client;

    public ConnectionServiceClientDecorator(String name, ConnectionServiceClient client) {
        this.name = name;
        this.client = client;
    }

    public void init(OnDataReadyReadDecorator<T> onDataReadyReadDecorator, OnReadException onReadException, ConnectionServiceClient.OnDisconnected onDisconnected) throws IOException {
        this.client.init(data -> {
            String json = new String(data, StandardCharsets.UTF_8);
            T obj = JsonUtils.fromJson(json);
            if (onDataReadyReadDecorator != null && obj != null) {
                onDataReadyReadDecorator.dataReady(obj);
            }
        }, onReadException, onDisconnected);
        sendName();
    }

    public void write(T obj) throws IOException {
        if (obj != null) {
            String json = JsonUtils.toJson(obj);
            if (json != null) {
                this.client.write(json.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void sendName() throws IOException {
        if (name != null && name.length() > 0) {
            client.write(name.getBytes(StandardCharsets.UTF_8));
        } else {
            client.write(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }

}
