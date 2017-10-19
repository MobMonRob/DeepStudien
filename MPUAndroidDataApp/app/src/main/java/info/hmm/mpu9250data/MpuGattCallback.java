package info.hmm.mpu9250data;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by Franz Aufschläger on 14.08.2017.
 */

public class MpuGattCallback extends BluetoothGattCallback {

    private static final UUID SERVICE_UUID =        UUID.fromString("0000f00d-1212-efde-1523-785fef13d123");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000acce-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC2_UUID = UUID.fromString("0000accc-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_CHAR_UUID =     UUID.fromString("0000accb-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTOR_UUID =     UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private Context context = null;
    private BluetoothGatt btGatt = null;

    private boolean writeDone = true;

    public MpuGattCallback(Context c){
        context = c;
    }

    public void SendGfsCmd(byte gfs){

        if(btGatt == null)
            return;

        byte cmd[] = {0x02, gfs};

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);

    }

    public void SendAfsCmd(byte afs){

        if(btGatt == null)
            return;

        byte cmd[] = {0x03, afs};

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);

    }

    public void SendSrCmd(byte sr){

        if(btGatt == null)
            return;

        byte cmd[] = {0x01, sr};

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);
    }

    public void SendDlpfCmd(byte dlpf){

        if(btGatt == null)
            return;

        byte cmd[] = {0x04, dlpf};

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);
    }

    public void SendFsyncCmd(byte fsync){

        if(btGatt == null)
            return;

        byte cmd[] = {0x05, fsync};

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);
    }

    public void SendFusionCmd(boolean fusion){

        if(btGatt == null)
            return;

        byte cmd[] = {0x06, 0x00};

        if(fusion)
            cmd[1] = 0x01;

        BluetoothGattCharacteristic write_char =  btGatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHAR_UUID);
        write_char.setValue(cmd);
        btGatt.writeCharacteristic(write_char);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int state){

        if(state == BluetoothProfile.STATE_CONNECTED){
            ((MainActivity)context).setDeviceConnected();
            gatt.discoverServices();

            btGatt = gatt;
        }
        else{
            ((MainActivity)context).setDeviceDisconnected();
            gatt.disconnect();
            gatt.close();

            btGatt = null;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID);
            BluetoothGattCharacteristic characteristic2 = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC2_UUID);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
            BluetoothGattDescriptor descriptor2 = characteristic2.getDescriptor(DESCRIPTOR_UUID);

            gatt.setCharacteristicNotification(characteristic, true);
            gatt.setCharacteristicNotification(characteristic2, true);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);


        }
    }

    int s =0 ;
    @Override
    public void onDescriptorWrite(final BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
        Log.d("APP", "WRITE DESC");

        try{

            writeDone = true;

            if(descriptor.getCharacteristic().getUuid().compareTo(CHARACTERISTIC_UUID) == 0){

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC2_UUID);
                            BluetoothGattDescriptor desc = characteristic.getDescriptor(DESCRIPTOR_UUID);

                            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(desc);
                        }
                    });


                }
            }
        }
        catch(Exception ex){

        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        if(characteristic.getUuid().compareTo(CHARACTERISTIC_UUID) == 0) {

            Data d = new Data();
            byte[] v = characteristic.getValue();
            d.Ac_x = (v[0] & 0xFF);
            d.Ac_x |= (short) (((int) v[1] & 0xFF) << 8);

            d.Ac_y = (v[2] & 0xFF);
            d.Ac_y |= (short) (((int) v[3] & 0xFF) << 8);

            d.Ac_z = (v[4] & 0xFF);
            d.Ac_z |= (short) (((int) v[5] & 0xFF) << 8);

            d.Gy_x = (v[6] & 0xFF);
            d.Gy_x |= (short) (((int) v[7] & 0xFF) << 8);

            d.Gy_y = (v[8] & 0xFF);
            d.Gy_y |= (short) (((int) v[9] & 0xFF) << 8);

            d.Gy_z = (v[10] & 0xFF);
            d.Gy_z |= (short) (((int) v[11] & 0xFF) << 8);

            d.Ma_x = (v[12] & 0xFF);
            d.Ma_x |= (short) (((int) v[13] & 0xFF) << 8);

            d.Ma_y = (v[14] & 0xFF);
            d.Ma_y |= (short) (((int) v[15] & 0xFF) << 8);

            d.Ma_z = (v[16] & 0xFF);
            d.Ma_z |= (short) (((int) v[17] & 0xFF) << 8);

            //16bit ts (ms)
            d.Ts = (v[18] & 0xFF);
            d.Ts |= (int) (((int) v[19] & 0xFF) << 8);

            ((MainActivity) context).addData(d);
            return;
        }
        else if(characteristic.getUuid().compareTo(CHARACTERISTIC2_UUID) == 0){
            Data2 d = new Data2();
            byte[] v = characteristic.getValue();

            d.Ts = (v[0] & 0xFF);
            d.Ts |= (int) (((int) v[1] & 0xFF) << 8);

            d.q0 = ByteBuffer.wrap(v, 2, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            if(Float.isNaN(d.q0))
                d.q0 = 0;

            d.q1 = ByteBuffer.wrap(v, 6, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            if(Float.isNaN(d.q1))
                d.q1 = 0;

            d.q2 = ByteBuffer.wrap(v, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            if(Float.isNaN(d.q2))
                d.q2 = 0;

            d.q3 = ByteBuffer.wrap(v, 14, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            if(Float.isNaN(d.q3))
                d.q3 = 0;

            ((MainActivity) context).addData2(d);

            return;

        }

    }

}
