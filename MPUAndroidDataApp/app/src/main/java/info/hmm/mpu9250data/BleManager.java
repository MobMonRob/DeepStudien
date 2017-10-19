package info.hmm.mpu9250data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;


/**
 * Created by Franz Aufschläger on 14.08.2017.
 */

public class BleManager implements BluetoothAdapter.LeScanCallback {
    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;

    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    MpuGattCallback callback;

    public BleManager(final Context c){
        context = c;
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        context = c;
    }

    public void startScan(){
        try {
            bluetoothAdapter.startLeScan(this);
        }
        catch (Exception ex){
        }
    }

    public void stopScan(){
        try {
            bluetoothAdapter.stopLeScan(this);
        }
        catch (Exception ex){
        }
    }

    private String decodeLocalName(final byte[] data, final int start, final int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (Exception ex) {
        }
        return null;
    }

    private String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        try{
            final BluetoothDevice dev = bluetoothDevice;
            String devName = bluetoothDevice.getName();

            if(devName == null)
                devName = decodeDeviceName(bytes);

            if(devName != null){

                if(devName.compareTo("HMMMPU") == 0) {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopScan();
                                callback = new MpuGattCallback(context);
                                dev.connectGatt(context, false, callback);
                            }
                        });
                }
            }
        }
        catch (Exception ex){
        }
    }

    public boolean isEnabled() {
        try
        {
            return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        }
        catch (Exception ex){
        }
        return false;
    }
}
