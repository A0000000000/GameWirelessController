package xyz.a00000.connectionserviceclient.raw;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import xyz.a00000.connectionserviceclient.utils.ByteLengthUtils;

public class ConnectionServiceClient implements Closeable {

    private BluetoothSocket mBluetoothSocket;

    public ConnectionServiceClient(BluetoothSocket bluetoothSocket) throws IOException {
        this.mBluetoothSocket = bluetoothSocket;
        this.mBluetoothSocket.connect();
    }

    private InputStream is;
    private OutputStream os;
    private OnDataReadyRead onDataReadyRead;
    private OnConnectionException onConnectionException;
    private Thread mReadThread;

    public void init(OnDataReadyRead onDataReadyRead, OnConnectionException onConnectionException) throws IOException {
        is = mBluetoothSocket.getInputStream();
        os = mBluetoothSocket.getOutputStream();
        this.onDataReadyRead = onDataReadyRead;
        this.onConnectionException = onConnectionException;
        initRead();
    }

    private void initRead() {
        mReadThread = new Thread(() -> {
            while (true) {
               try {
                   byte[] length = new byte[4];
                   for (int i = 0; i < length.length; i++) {
                       length[i] = (byte) is.read();
                       if (length[i] < 0) {
                           throw new RuntimeException("Normal Exit.");
                       }
                   }
                   int size = ByteLengthUtils.getLengthInteger(length);
                   if (size != 0) {
                       byte[] data = new byte[size];
                       for (int i = 0; i < size; ++i) {
                           data[i] = (byte) is.read();
                           if (data[i] < 0) {
                               throw new RuntimeException("Normal Exit.");
                           }
                       }
                       if (onDataReadyRead != null) {
                           Log.d("IO", "IN begin");
                           Log.d("IO", "size = " + size);
                           Log.d("IO", "data = " + Arrays.toString(data));
                           Log.d("IO", "IN end");
                           onDataReadyRead.dataReady(data);
                       }
                   }
               } catch (Exception e) {
                   if (onConnectionException != null) {
                       onConnectionException.connectionException(e);
                   }
                   break;
               }
            }
        });
        mReadThread.start();
    }

    public void write(byte[] data) {
        try {
            if (data != null && data.length != 0) {
                Log.d("IO", "OUT begin");
                Log.d("IO", "size = " + data.length);
                Log.d("IO", "data = " + Arrays.toString(data));
                Log.d("IO", "OUT end");
                byte[] size = ByteLengthUtils.getLengthArray(data.length);
                os.write(size);
                os.write(data);
                os.flush();
            }
        } catch (Exception e) {
            if (onConnectionException != null) {
                onConnectionException.connectionException(e);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        mBluetoothSocket.close();
    }

}
