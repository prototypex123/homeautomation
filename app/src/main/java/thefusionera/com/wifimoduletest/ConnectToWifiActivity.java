package thefusionera.com.wifimoduletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ConnectToWifiActivity extends AppCompatActivity {

    private static final String TAG = "::::::::::::::::::::";
    RecyclerView recyclerView;
    WifiAdapter wifiAdapter;
    WifiManager wifi;
    String ipAddress;
    List<ScanResult> scanResults;
    List<ScanResult> ssidList;


    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = wifi.getScanResults();

                ssidList = new ArrayList<>();

                for (ScanResult scanResult : scanResults)
                {
                    if(!containsSSID(scanResult))
                        ssidList.add(scanResult);
                }
                wifiAdapter = new WifiAdapter(ConnectToWifiActivity.this, ssidList, ipAddress);
                recyclerView.setAdapter(wifiAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ConnectToWifiActivity.this));
            }
        }
    };

    public boolean containsSSID(ScanResult scanResult)
    {
        for(ScanResult s : ssidList)
        {
            if(s.SSID.equals(scanResult.SSID))
                return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_wifi);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        ipAddress = getIntent().getStringExtra("ip_address");




        //TODO: Request Permissions
        //TODO: Make sure location is enabled

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
//        List<ScanResult> scanResults =  wifi.getScanResults();

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try{
            if(wifiScanReceiver!=null)
                unregisterReceiver(wifiScanReceiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();

    }
}
