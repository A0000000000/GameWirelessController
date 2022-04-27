package xyz.a00000.connectionserviceclient.internal;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    private OnReadException onReadException;
    private Thread mReadThread;

    public void init(OnDataReadyRead onDataReadyRead, OnReadException onReadException) throws IOException {
        is = mBluetoothSocket.getInputStream();
        os = mBluetoothSocket.getOutputStream();
        this.onDataReadyRead = onDataReadyRead;
        this.onReadException = onReadException;
        initRead();
    }

    private void initRead() {
        mReadThread = new Thread(() -> {
            while (true) {
               try {
                   byte[] length = new byte[4];
                   is.read(length);
                   int size = ByteLengthUtils.getLengthInteger(length);
                   byte[] data = new byte[size];
                   is.read(data);
                   if (onDataReadyRead != null) {
                       onDataReadyRead.dataReady(data);
                   }
               } catch (Exception e) {
                   if (onReadException != null) {
                       onReadException.readException(e);
                   }
                   break;
               }
            }
        });
        mReadThread.start();
    }

    public void write(byte[] data) throws IOException {
        if (data != null) {
            byte[] size = ByteLengthUtils.getLengthArray(data.length);
            Log.d("WRITE", "size = " + data.length);
            os.write(size);
            os.write(data);
            os.flush();
        }
    }

    @Override
    public void close() throws IOException {
        mBluetoothSocket.close();
    }

}
