package info.hmm.mpu9250data;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SavedSettings";

    private TextView tv = null;
    private Button btn = null;
    private ProgressBar pb = null;

    private Button btnSendAfs = null;
    private Button btnSendGfs = null;
    private Button btnSendSr = null;
    private Button btnSendDlpf = null;
    private Button btnSendFsync = null;
    private Button btnSendFusion = null;
    private ConstraintLayout lyt = null;
    private Spinner spinnerAfs = null;
    private Spinner spinnerGfs = null;
    private EditText editSr = null;
    private EditText editDlpf = null;
    private EditText editFsync = null;
    private CheckBox cbFusion = null;


    private ArrayList<Data> dataList = new ArrayList<>();
    private ArrayList<Data2> dataList2 = new ArrayList<>();

    private boolean started = false;

    private BleManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.lbl_status);
        btn = (Button) findViewById(R.id.button);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        lyt = (ConstraintLayout) findViewById(R.id.lyt_cmd);

        btnSendAfs = (Button) findViewById(R.id.btn_send_afs);
        btnSendGfs = (Button) findViewById(R.id.btn_send_gfs);
        btnSendSr = (Button) findViewById(R.id.btn_send_sr);
        btnSendDlpf = (Button) findViewById(R.id.btn_send_dlpf);
        btnSendFsync = (Button) findViewById(R.id.btn_send_fsync);
        btnSendFusion = (Button) findViewById(R.id.btn_send_fusion);
        spinnerAfs = (Spinner) findViewById(R.id.spinner_afs);
        spinnerGfs = (Spinner) findViewById(R.id.spinner_gfs);
        editSr = (EditText) findViewById(R.id.edit_sr);
        editDlpf = (EditText) findViewById(R.id.edit_dlpf);
        editFsync = (EditText) findViewById(R.id.edit_fsync);
        cbFusion = (CheckBox) findViewById(R.id.checkBox);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started == false){
                    started = true;
                    btn.setText("Stop");
                }
                else{
                    started = false;
                    saveData();
                    dataList.clear();
                    // OR
                    dataList2.clear();
                    btn.setText("Start");
                }
            }
        });

        btnSendAfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sel = spinnerAfs.getSelectedItemPosition();
                manager.callback.SendAfsCmd((byte)sel);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendGfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sel = spinnerGfs.getSelectedItemPosition();
                manager.callback.SendGfsCmd((byte)sel);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendSr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int sr = Integer.parseInt(editSr.getText().toString());
                manager.callback.SendSrCmd((byte)sr);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendDlpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dlpf = Integer.parseInt(editDlpf.getText().toString());
                manager.callback.SendDlpfCmd((byte)dlpf);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendFsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fsync = Integer.parseInt(editFsync.getText().toString());
                manager.callback.SendFsyncCmd((byte)fsync);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendFusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = cbFusion.isChecked();
                manager.callback.SendFusionCmd(b);

                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int afs = settings.getInt("afs", 0);
        int gfs = settings.getInt("gfs", 0);
        int sr = settings.getInt("sr", 19);
        int dlpf = settings.getInt("dlpf", 0);
        int fsync = settings.getInt("fsync", 0);

        spinnerAfs.setSelection(afs);
        spinnerGfs.setSelection(gfs);

        editSr.setText(""+sr);
        editDlpf.setText(""+dlpf);
        editFsync.setText(""+fsync);

        manager = new BleManager(this);

        if(MainActivity.this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                MainActivity.this.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else{
            manager.startScan();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    manager.startScan();
                }
            }
        }
    }

    private void saveData(){

        try {
            if (dataList.size() == 0 && dataList2.size() == 0)
                return;

            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");

            String fileName = df.format(Calendar.getInstance().getTime()) + "_mpu9250";

            Gson gson = new Gson();

            //Data

            //OR
            if (dataList.size() > 0) {
                String fn = fileName + ".json";

                String json = gson.toJson(dataList);
                FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/" + fn);
                fw.write(json);
                fw.close();
            }

            //Data2

            if (dataList2.size() > 0) {
                String fn = fileName + "_2" + ".json";

                String json = gson.toJson(dataList2);
                FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/" + fn);
                fw.write(json);
                fw.close();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void addData(Data d){
        if(started) {
            dataList.add(d);
        }
    }

    public void addData2(Data2 d){
        if(started) {
            dataList2.add(d);
        }
    }

    public void setDeviceConnected(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.VISIBLE);
                tv.setText("Verbunden");
                lyt.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setDeviceDisconnected(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.INVISIBLE);
                lyt.setVisibility(View.INVISIBLE);
                tv.setText("Warte auf Gerät");

                manager.startScan();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_default) {
            spinnerAfs.setSelection(0);
            spinnerGfs.setSelection(0);
            editSr.setText("19");
            editDlpf.setText("0");
            editFsync.setText("0");

            return true;
        }
        else if(id == R.id.action_save){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();

            int afs = spinnerAfs.getSelectedItemPosition();
            int gfs = spinnerGfs.getSelectedItemPosition();
            int sr = Integer.parseInt(editSr.getText().toString());
            int dlpf = Integer.parseInt(editDlpf.getText().toString());
            int fsync = Integer.parseInt(editFsync.getText().toString());

            editor.putInt("afs", afs);
            editor.putInt("gfs", gfs);
            editor.putInt("sr", sr);
            editor.putInt("dlpf", dlpf);
            editor.putInt("fsync", fsync);

            editor.commit();

        }
        return super.onOptionsItemSelected(item);
    }

}
