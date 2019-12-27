package com.example.smarthome;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public Bundle authBundle = null;
    public String hasGlobal = null;
    public String admin = null;
    public String globalIP = "shakssmarts.ddns.net";
    public String loungeRoomLocalIP = "192.168.1.174:8002";
    public String bookshelfLocalIP = "192.168.1.86:8001";
    public String bedroomBlindsLocalIP = "192.168.1.152:8003";
    public String username = null;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Smart Home Control Panel");
        setContentView(R.layout.activity_main2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getBundle();
        setIPs();
        setMenu(navView);
        loadFragment(newInstance(list));
    }

    public void setMenu (BottomNavigationView v){
        if (admin.equals("true") && hasGlobal.equals("true")) {
            v = (BottomNavigationView) findViewById(R.id.nav_view);
            Menu menu = v.getMenu();
            menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.app_name))
                    .setIcon(R.drawable.ic_people_black_24dp).setTitle(R.string.title_all_users);
        }
    }

    public static homeFragment newInstance(ArrayList list) {
        homeFragment myFragment = new homeFragment();

        Bundle args = new Bundle();
        args.putStringArrayList("list", list);
        myFragment.setArguments(args);

        return myFragment;
    }

    public static accountFragment newAccountFragment(String hasGlobal, String username, String admin){
        accountFragment myFragment = new accountFragment();
        Bundle args = new Bundle();
        args.putString("hasGlobal", hasGlobal);
        args.putString("username", username);
        args.putString("admin", admin);
        myFragment.setArguments(args);
        return myFragment;
    }

    public static allUsersFragment newAllUsersFragment(String hasGlobal, String username){
        allUsersFragment myFragment = new allUsersFragment();
        Bundle args = new Bundle();
        args.putString("hasGlobal", hasGlobal);
        args.putString("username", username);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = newInstance(list);
                break;

            case R.id.navigation_dashboard:
                fragment = newAccountFragment(hasGlobal, username, admin);
                break;

            case 3:
                fragment = newAllUsersFragment(hasGlobal, username);
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void getBundle(){
        if (authBundle == null) authBundle = getIntent().getExtras();
        if (hasGlobal == null) hasGlobal = authBundle.getString("hasGlobal");
        if (username == null) username = authBundle.getString("username");
        if (admin == null) admin = authBundle.getString("admin");
    }


    private void setIPs(){
        if (hasGlobal.equals("true")) {
            initGlobalList();
        } else {
            initLocalList();
        }
    }

    private void initGlobalList(){
        list.add(0, "http://" + globalIP + ":8000/lamp");
        list.add(1, "http://" + globalIP + ":8001/lamp");
        list.add(2, "http://" + globalIP + ":8000/motion");
        list.add(3, "http://" + globalIP + ":8001/motion");
        list.add(4, "http://" + globalIP + ":8003/leftMotorPosition");
        list.add(5, "http://" + globalIP + ":8003/rightMotorPosition");
    }

    private void initLocalList(){
        list.add(0, "http://" + loungeRoomLocalIP + "/lamp");
        list.add(1, "http://" + bookshelfLocalIP + "/lamp");
        list.add(2, "http://" + loungeRoomLocalIP + "/motion");
        list.add(3, "http://" + bookshelfLocalIP + "/motion");
        list.add(4, "http://" + bedroomBlindsLocalIP + "/leftMotorPosition");
        list.add(5, "http://" + bedroomBlindsLocalIP + "/rightMotorPosition");
    }

}
