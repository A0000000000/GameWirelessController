package xyz.a00000.connectionserviceclient.utils;

public class ByteLengthUtils {

    public static byte[] getLengthArray(int size) {
        return new byte[] {
                (byte) (size >> 24),
                (byte) (size >> 16),
                (byte) (size >> 8),
                (byte) size
        };
    }

    public static int getLengthInteger(byte[] length) {
        return (((int) length[0]) << 24) | (((int) length[1]) << 16) | (((int) length[2]) << 8) | ((int) length[3]);
    }

}
