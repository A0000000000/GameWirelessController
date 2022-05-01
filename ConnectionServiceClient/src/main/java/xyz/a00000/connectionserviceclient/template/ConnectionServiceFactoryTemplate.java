package xyz.a00000.connectionserviceclient.template;


import java.io.IOException;

import xyz.a00000.connectionserviceclient.raw.ConnectionServiceFactory;

public class ConnectionServiceFactoryTemplate<T> {

    private ConnectionServiceFactory factory;
    private Class<T> clazz;

    public ConnectionServiceFactoryTemplate(Class<T> clazz, ConnectionServiceFactory factory) {
        this.clazz = clazz;
        this.factory = factory;
    }

    public ConnectionServiceClientTemplate<T> getConnectionServiceClient(String uuid) throws IOException {
        return new ConnectionServiceClientTemplate<>(clazz, factory.getConnectionServiceClient(uuid));
    }

    public ConnectionServiceFactory getRawFactory() {
        return factory;
    }

}
