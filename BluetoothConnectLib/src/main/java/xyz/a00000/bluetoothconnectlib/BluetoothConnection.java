package xyz.a00000.bluetoothconnectlib;

import android.bluetooth.BluetoothDevice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BluetoothConnection implements AutoCloseable {

    private String targetDeviceName;
    private String uuid;
    private BluetoothController bluetoothController;
    private BluetoothController.Stream stream;
    private OnReceive onReceive;
    private Thread receiveThread;
    private DataOutputStream dos;
    private Runnable onDisconnect;

    public BluetoothConnection(String targetDeviceName, String uuid, BluetoothController bluetoothController) throws IOException {
        this.targetDeviceName = targetDeviceName;
        this.uuid = uuid;
        this.bluetoothController = bluetoothController;
    }

    public void setOnReceive(OnReceive onReceive) {
        this.onReceive = onReceive;
    }

    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    public void openConnect() throws IOException {
        BluetoothDevice bluetoothDevice = bluetoothController.getBluetoothDevice(targetDeviceName);
        stream = bluetoothController.connectTargetDevices(bluetoothDevice, uuid);
        dos = new DataOutputStream(stream.os);
        receiveThread = new Thread(() -> {
            DataInputStream dis = new DataInputStream(stream.is);
            while (true) {
                int len = 0;
                try {
                    for (int i = 0; i < 3; i++) {
                        int b = dis.read();
                        len |= b;
                        len <<= 8;
                    }
                    len |= dis.read();
                    byte[] data = new byte[len];
                    dis.read(data);
                    if (onReceive != null) {
                        onReceive.onReceive(data);
                    }
                } catch (IOException e) {
                    break;
                }
            }
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (onDisconnect != null) {
                    onDisconnect.run();
                }
            }
        });
        receiveThread.start();
    }

    public void sendData(byte[] data) throws IOException {
        if (dos != null) {
            int size = data.length;
            byte[] len = {(byte) (size >> 24), (byte) (size >> 16), (byte) (size >> 8), (byte) size};
            dos.write(len);
            dos.write(data);
            dos.flush();
        }
    }

    @Override
    public void close() throws Exception {
        if (stream != null) {
            bluetoothController.disconnectTargetDevices(stream);
            stream = null;
        }
    }

    public interface OnReceive {
        void onReceive(byte[] data);
    }

}
