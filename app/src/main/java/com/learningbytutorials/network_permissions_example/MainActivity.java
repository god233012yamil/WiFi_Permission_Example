package com.learningbytutorials.network_permissions_example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private View mLayout;
    private TextView ssidTextView;
    private static String TAG = "";
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define the TAG for this class.
        TAG = this.getClass().getName().replace(getApplicationContext().getPackageName() + ".", "");

        // Set the activity content to an explicit view.
        setContentView(R.layout.activity_main);

        // Finds the view with ID main_layout.
        mLayout = findViewById(R.id.main_layout);

        // Finds the view with ID text_view_id.
        ssidTextView = findViewById(R.id.text_view_id);

        // Finds the view with ID button_enable_wifi, and register a callback to
        // be invoked when this view is clicked.
        findViewById(R.id.button_enable_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAccessFineLocationPermission();
            }
        });

        // Finds the view with ID button_enable_wifi, and register a callback to be invoked when this view is clicked.
        findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssidTextView.setText(R.string.Empty);
            }
        });
    }

    /* This interface is the contract for receiving the results for permission requests.*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If permission requested was PERMISSION_ACCESS_FINE_LOCATION.
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                Snackbar.make(mLayout, R.string.access_fine_location_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                // Get the SSID, and show it in the ssid TextView.
                ssidTextView.setText(getWifiSSID());
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.access_fine_location_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /* Convenience method to request the user to grant the ACCESS_FINE_LOCATION permission
    for this application. A UI with rationale is shown before requesting the permission.*/
    private void requestAccessFineLocationPermission() {
        // If the permission ACCESS_FINE_LOCATION has been denied.
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Gets whether you should show UI with rationale before requesting a permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Get an instance of the AlertDialog.Builder class.
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                // Sets the dialog to be not cancelable.
                alertDialog.setCancelable(false);
                // Setting Dialog Title.
                alertDialog.setTitle(R.string.access_fine_location_alert_title);
                // Setting Dialog Message.
                alertDialog.setMessage(R.string.access_fine_location_alert_message);
                // On pressing Settings button.
                alertDialog.setPositiveButton(
                        getResources().getString(R.string.button_dismiss),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Request the permission ACCESS_FINE_LOCATION to be granted for
                                // this application.
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_ACCESS_FINE_LOCATION);
                            }
                        });
                alertDialog.show();
                //
            } else {
                // Request the permission ACCESS_FINE_LOCATION to be granted for this application.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_ACCESS_FINE_LOCATION);
            }
            // If the permission ACCESS_FINE_LOCATION has been granted.
        } else {
            // Get the SSID, and shows it in the ssid TextView.
            ssidTextView.setText(getWifiSSID());
        }
    }

    /*
    Convenience method to get the WiFi SSID.
     */
    private String getWifiSSID() {
        // Get the Activity context.
        Context context = MainActivity.this;
        // Get a handle to the system-level service CONNECTIVITY_SERVICE.
        //  ConnectivityManager class requires the ACCESS_NETWORK_STATE permission.
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check for null because getSystemService can return null if service name doesn't exist.
        if(connectivityManager != null) {
            // Code for build version greater than Android 10, API 29.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Get the NetworkCapabilities for the given Network.
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    // Check for the presence of a transport on WiFi.
                    if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        // Get a handle to the system-level service WIFI_SERVICE.
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                        if(wifiManager != null) {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            Log.d(TAG, "SSID = " + wifiInfo.getSSID());
                            return wifiInfo.getSSID();
                        }
                    }
                }
            } else {
                NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(info.isConnected()) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    if(wifiManager != null) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        return wifiInfo.getSSID();
                    }
                }
            }
        }
        return "";
    }

}
