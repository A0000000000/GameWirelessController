package xyz.a00000.connectionserviceclient.template;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import xyz.a00000.connectionserviceclient.raw.ConnectionServiceClient;
import xyz.a00000.connectionserviceclient.raw.OnConnectionException;
import xyz.a00000.connectionserviceclient.utils.JsonUtils;

public class ConnectionServiceClientTemplate<T> implements Closeable {

    private ConnectionServiceClient client;
    private Class<T> clazz;

    public ConnectionServiceClientTemplate(Class<T> clazz, ConnectionServiceClient client) {
        this.client = client;
        this.clazz = clazz;
    }

    public void init(OnDataReadyReadTemplate<T> onDataReadyReadTemplate, OnConnectionException onConnectionException) throws IOException {
        this.client.init(data -> {
            String json = new String(data, StandardCharsets.UTF_8);
            T obj = JsonUtils.fromJson(json, clazz);
            if (onDataReadyReadTemplate != null && obj != null) {
                onDataReadyReadTemplate.dataReady(obj);
            }
        }, onConnectionException);
    }

    public void write(T obj) {
        if (obj != null) {
            String json = JsonUtils.toJson(obj);
            if (json != null) {
                this.client.write(json.getBytes(StandardCharsets.UTF_8));
            }
        }
    }


    @Override
    public void close() throws IOException {
        this.client.close();
    }

    public ConnectionServiceClient getRawClient() {
        return client;
    }

}
