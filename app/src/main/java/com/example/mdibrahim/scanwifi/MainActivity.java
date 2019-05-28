package com.example.mdibrahim.scanwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Handler;

import static android.R.attr.delay;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private android.os.Handler mHandler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textView = (TextView) findViewById(R.id.textView2);
        buttonScan = (Button) findViewById(R.id.scanBtn);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scanWifi();
                mHandler.postDelayed(mtr, 100);
            }
        });

        listView = (ListView) findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        int i = 0;

        scanWifi();
    }

    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning Wifi....", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                double dis = calculateDistance((double)scanResult.level, (double)scanResult.frequency);
                int level = scanResult.level + 100;
                String ssid, s = "";
                //arrayList.add(scanResult.SSID + " -> " + scanResult.level + "\n" );
                s += scanResult.SSID + "\n";
                for( int i = 0; i < 60; i++ ) s += '-';
                s += '\n';
                //s += String.valueOf(level-100);
                if( level >= 50 ) s+=" Signal Strength = "+String.valueOf(level-100)+ "(Good)\n";
                else if( level >= 20 ) s+=" Signal Strength = "+String.valueOf(level-100)+ "(Medium)\n";
                else s+=" Signal Strength = "+String.valueOf(level-100)+ "(Weak)\n";
                s+=" Distance = " + dis + "m\n";
                arrayList.add(s);
                adapter.notifyDataSetChanged();
            }
        }

    };
/*
    @Override
    public void onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }
    */


    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }
    private  Runnable mtr = new Runnable() {
        @Override
        public void run() {
            scanWifi();
            mHandler.postDelayed(this, 1000);
        }
    };
}
