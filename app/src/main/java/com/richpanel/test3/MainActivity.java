package com.richpanel.test3;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.richpanel.android.RichpanelSDK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void callHelpcenter(View view) {
        Toast.makeText(this, "callHelpcenter", Toast.LENGTH_SHORT).show();
        RichpanelSDK richpanelSDK = new RichpanelSDK(this, "apiKey", "apiSecret");
//        richpanelSDK.InitiateMessenger();

        richpanelSDK.track("test");

        Map<String, Object> identityData = new HashMap<>();
        identityData .put("uid", "1");
        identityData .put("email", "shubhanshu@richpanel.com");
        richpanelSDK.identify(identityData);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("order_date", "1392036272");
        eventData.put("stripe_invoice", "38572984");

        richpanelSDK.track("test2", eventData);

    }

}