package xyz.a00000.connectionserviceclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.a00000.connectionserviceclient.raw.ConnectionServiceFactory;
import xyz.a00000.connectionserviceclient.template.ConnectionServiceFactoryTemplate;

public class ConnectionServiceController {

    private static volatile ConnectionServiceController INSTANCE;

    public static ConnectionServiceController getInstance() {
        if (INSTANCE == null) {
            synchronized (ConnectionServiceController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionServiceController();
                }
            }
        }
        return INSTANCE;
    }

    private final BluetoothAdapter mBluetoothAdapter;

    private ConnectionServiceController() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private Set<BluetoothDevice> getPairedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    public List<String> getPairedDevicesName() {
        return getPairedDevices().stream().map(BluetoothDevice::getName).collect(Collectors.toList());
    }

    private BluetoothDevice getBluetoothDeviceByName(String name) {
        Set<BluetoothDevice> devices = getPairedDevices();
        for (BluetoothDevice device : devices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }

    public ConnectionServiceFactory createServiceFactory(String deviceName) {
        return new ConnectionServiceFactory(getBluetoothDeviceByName(deviceName));
    }

    public static <T> ConnectionServiceFactoryTemplate<T> makeTemplateFactory(ConnectionServiceFactory factory, Class<T> clazz) {
        return new ConnectionServiceFactoryTemplate<>(clazz, factory);
    }

}
