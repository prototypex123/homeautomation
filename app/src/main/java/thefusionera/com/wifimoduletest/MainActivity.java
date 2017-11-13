package thefusionera.com.wifimoduletest;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "--------------------";
    // declare buttons and text inputs
    private Button connectButton, wifiButton, hotspotButton;
    private EditText editTextIPAddress, editTextPortNumber;
    Switch s1, s2, s3, s4;
    SeekBar sb1, sb2, sb3, sb4;
    TextView textView;

    String ipAddress;
    String portNumber;
    String espIPAddress;
    String encryptionType;

    Retrofit.Builder builder;
    Retrofit retrofit;
    RetrofitService retrofitService;

    String selectedSSID;
    String selectedPassword;
    String selectedIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // assign buttons
        connectButton = (Button)findViewById(R.id.connect_button);
        wifiButton = (Button)findViewById(R.id.wifi_button);
        hotspotButton = (Button) findViewById(R.id.hotspot_button);

        s1 = (Switch) findViewById(R.id.s1);
        s2 = (Switch) findViewById(R.id.s2);
        s3 = (Switch) findViewById(R.id.s3);
        s4 = (Switch) findViewById(R.id.s4);

        sb1 = (SeekBar) findViewById(R.id.seekBar1);
        sb2 = (SeekBar) findViewById(R.id.seekBar2);
        sb3 = (SeekBar) findViewById(R.id.seekBar3);
        sb4 = (SeekBar) findViewById(R.id.seekBar4);

        s1.setEnabled(false);
        s2.setEnabled(false);
        s3.setEnabled(false);
        s4.setEnabled(false);

        sb1.setEnabled(false);
        sb2.setEnabled(false);
        sb3.setEnabled(false);
        sb4.setEnabled(false);

        textView = (TextView) findViewById(R.id.text_view);

        // assign text inputs
        editTextIPAddress = (EditText)findViewById(R.id.editTextIPAddress);
        editTextPortNumber = (EditText)findViewById(R.id.editTextPortNumber);

        // set button listener (this class)

        connectButton.setOnClickListener(this);
        wifiButton.setOnClickListener(this);
        hotspotButton.setOnClickListener(this);

        s1.setOnCheckedChangeListener(this);
        s2.setOnCheckedChangeListener(this);
        s3.setOnCheckedChangeListener(this);
        s4.setOnCheckedChangeListener(this);

        sb1.setOnSeekBarChangeListener(this);
        sb2.setOnSeekBarChangeListener(this);
        sb3.setOnSeekBarChangeListener(this);
        sb4.setOnSeekBarChangeListener(this);

        selectedSSID = getIntent().getStringExtra("ssid");
        selectedPassword = getIntent().getStringExtra("password");
        selectedIpAddress = getIntent().getStringExtra("ip_address");
        encryptionType = getIntent().getStringExtra("encryptionType");
//        Toast.makeText(this, "selectedssid  " + selectedSSID, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "selectedpassword  " + selectedPassword, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "selectedipaddress  " + selectedIpAddress, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "encryptionType  " + encryptionType, Toast.LENGTH_SHORT).show();

        if(selectedSSID != null && selectedPassword != null && selectedIpAddress != null)
        {
            editTextIPAddress.setText(selectedIpAddress);
            editTextPortNumber.setText("80");
            Toast.makeText(this, "Connecting to Wifi " + selectedSSID, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Password " + selectedPassword, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Encryption Type " + encryptionType, Toast.LENGTH_SHORT).show();

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + selectedSSID + "\"";

            if(encryptionType == null)
            {
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
            else {
                if (encryptionType.equals("WEP")) {
                    conf.wepKeys[0] = "\"" + selectedPassword + "\"";
                    conf.wepTxKeyIndex = 0;
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                } else if (encryptionType.equals("WPA") || encryptionType.equals("WPA2")) {
                    conf.preSharedKey = "\"" + selectedPassword + "\"";
                }
            }



//            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // Open network.... No password

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.wifi_button)
        {
            Intent intent = new Intent(MainActivity.this, ConnectToWifiActivity.class);
            intent.putExtra("ip_address", "192.168.4.1");
            startActivity(intent);

        }
        else if(view.getId() == R.id.hotspot_button)
        {
            String networkSSID = "ESPap";
            Toast.makeText(this, "Connecting to Wifi " + networkSSID, Toast.LENGTH_SHORT).show();

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // Open network.... No password

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();



        }

        else if (view.getId() == R.id.connect_button)
        {
            // get the ip address
            String ipAddress = editTextIPAddress.getText().toString().trim();
            // get the port number
            String portNumber = editTextPortNumber.getText().toString().trim();

            builder = new Retrofit.Builder().baseUrl("http://"+ipAddress+":"+portNumber);

            retrofit = builder.build();

            retrofitService = retrofit.create(RetrofitService.class);
            Call<ResponseBody> result = retrofitService.getGeneralStatus();
            result.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String res = response.body().string();
                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
//                        textView.setText(res);
                        String switchStatuses[] = res.split(",");
                        for(String s : switchStatuses)
                        {
                            changeSwitchStatus(s);
                        }
//
                        s1.setEnabled(true);
                        s2.setEnabled(true);
                        s3.setEnabled(true);
                        s4.setEnabled(true);

                        sb1.setEnabled(true);
                        sb2.setEnabled(true);
                        sb3.setEnabled(true);
                        sb4.setEnabled(true);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "ERROR",Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            String parameterValue = ""; // 0 for OFF 1 for ON
            String parameterName = ""; // pin number (12,13,14,15)

        if(compoundButton.getId() == s1.getId())
            parameterName = "status1";
        else if(compoundButton.getId() == s2.getId())
            parameterName = "status2";
        else if(compoundButton.getId() == s3.getId())
            parameterName = "status3";
        else if(compoundButton.getId() == s4.getId())
            parameterName = "status4";


            if(b)
                parameterValue = "1";
            else
                parameterValue = "0";

        retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> result = retrofitService.getDeviceStatus(parameterName, parameterValue);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Toast.makeText(MainActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                    //changeSwitchStatus(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSwitchStatus(String switchStatus) {

        String array[] = switchStatus.split(":");
        if(array[0].equals("Device1"))
        {
            if(array[1].equals("ON"))
                s1.setChecked(true);
            else if(array[1].equals("OFF"))
                s1.setChecked(false);
        }
        else if(array[0].equals("Device2"))
        {
            if(array[1].equals("ON"))
                s2.setChecked(true);
            else if(array[1].equals("OFF"))
                s2.setChecked(false);
        }
        else if(array[0].equals("Device3"))
        {
            if(array[1].equals("ON"))
                s3.setChecked(true);
            else if(array[1].equals("OFF"))
                s3.setChecked(false);
        }
        else if(array[0].equals("Device4"))
        {
            if(array[1].equals("ON"))
                s4.setChecked(true);
            else if(array[1].equals("OFF"))
                s4.setChecked(false);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//        Toast.makeText(getApplicationContext(),"seekbar progress: "+ progress, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int progress = 130 - (seekBar.getProgress() + 10);

        Toast.makeText(getApplicationContext(),"seekbar progress : " + progress, Toast.LENGTH_SHORT).show();



        String parameterName = null;


        if(seekBar.getId() == sb1.getId())
            parameterName = "status1";
        else if(seekBar.getId() == sb2.getId())
            parameterName = "status2";
        else if(seekBar.getId() == sb3.getId())
            parameterName = "status3";
        else if(seekBar.getId() == sb3.getId())
            parameterName = "status4";

        Toast.makeText(MainActivity.this, parameterName, Toast.LENGTH_SHORT).show();



        retrofitService = retrofit.create(RetrofitService.class);

        Call<ResponseBody> result = retrofitService.getDeviceIntensity(parameterName, String.valueOf(progress));
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Toast.makeText(MainActivity.this, "Intensity Response " + response.body().string(), Toast.LENGTH_SHORT).show();
                    //changeSwitchStatus(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR",Toast.LENGTH_SHORT).show();
            }
        });


    }



}
