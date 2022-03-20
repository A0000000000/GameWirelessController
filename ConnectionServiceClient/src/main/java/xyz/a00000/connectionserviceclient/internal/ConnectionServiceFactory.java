package xyz.a00000.connectionserviceclient.internal;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ConnectionServiceFactory {


    private BluetoothDevice mBluetoothDevice;

    public ConnectionServiceFactory(BluetoothDevice bluetoothDevice) {
        this.mBluetoothDevice = bluetoothDevice;
    }

    public ConnectionServiceClient getConnectionServiceClient(String uuid) throws IOException {
        return new ConnectionServiceClient(mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid)));
    }


}
