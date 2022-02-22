package xyz.a00000.bluetoothconnectlib;

import java.io.IOException;

public class ConnectionFactory {

    private String targetDeviceName;
    private String uuid;
    private BluetoothController bluetoothController;

    public ConnectionFactory setTargetDeviceName(String targetDeviceName) {
        this.targetDeviceName = targetDeviceName;
        return this;
    }

    public ConnectionFactory setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public ConnectionFactory setBluetoothController(BluetoothController bluetoothController) {
        this.bluetoothController = bluetoothController;
        return this;
    }

    public BluetoothConnection build() throws IOException {
        return new BluetoothConnection(targetDeviceName, uuid, bluetoothController);
    }

}
