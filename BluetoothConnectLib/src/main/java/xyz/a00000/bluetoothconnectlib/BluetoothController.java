package xyz.a00000.bluetoothconnectlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BluetoothController {


    private static BluetoothController INSTANCE = null;

    public static BluetoothController getInstance() {
        if (INSTANCE == null) {
            synchronized (BluetoothController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothController();
                }
            }
        }
        return INSTANCE;
    }


    public static class Stream {

        public BluetoothDevice device;
        public BluetoothSocket socket;
        public InputStream is;
        public OutputStream os;

        public Stream(BluetoothDevice device, BluetoothSocket socket, InputStream is, OutputStream os) {
            this.device = device;
            this.socket = socket;
            this.is = is;
            this.os = os;
        }
    }

    private BluetoothAdapter mBlueAdapter;

    private BluetoothController() {
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isSupportBluetooth() {
        return mBlueAdapter.isEnabled();
    }

    public boolean enable() {
        return mBlueAdapter.enable();
    }

    public Set<String> getPairedDevicesName() {
        return getPairedDevices().stream().filter(Objects::nonNull).map(BluetoothDevice::getName).collect(Collectors.toSet());
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return mBlueAdapter.getBondedDevices();
    }

    public BluetoothDevice getBluetoothDevice(String name) {
        Set<BluetoothDevice> deviceSet = getPairedDevices();
        for (BluetoothDevice device : deviceSet) {
            if (name != null && name.equals(device.getName())) {
                return device;
            }
        }
        return null;
    }

    public Stream connectTargetDevices(BluetoothDevice device, String uuid) throws IOException {
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        socket.connect();
        return new Stream(device, socket, socket.getInputStream(), socket.getOutputStream());
    }

    public void disconnectTargetDevices(Stream stream) throws IOException {
        if (stream != null) {
            if (stream.is != null) {
                stream.is.close();
            }
            if (stream.os != null) {
                stream.os.close();
            }
            if (stream.socket != null) {
                stream.socket.close();
            }
            stream.is = null;
            stream.os = null;
            stream.socket = null;
            stream.device = null;
        }
    }

}
