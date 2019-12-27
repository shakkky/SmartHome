package com.example.smarthome;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class allUsersFragment extends Fragment {
    private Handler responseHandler;
    ArrayList<JSONObject> myArray = new ArrayList<JSONObject>();
    MyRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        final RecyclerView recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    String responseText = bundle.getString("KEY_RESPONSE_TEXT");
                    try {
                        JSONObject object = new JSONObject(responseText);
                        JSONArray allUsers = object.getJSONObject("users").getJSONArray("Items");

                        for (int i = 0; i < allUsers.length(); i++){
                            JSONObject obj = allUsers.getJSONObject(i);
                            myArray.add(obj);
                        }

                        adapter = new MyRecyclerViewAdapter(getActivity(), myArray);
                        //adapter.setClickListener(getActivity());
                        recyclerView.setAdapter(adapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                    } catch (Exception e){
                        System.out.println("failed to parse JSON object");
                    }
                }
            }
        };
        new httpRequest("https://13at12imu2.execute-api.ap-southeast-2.amazonaws.com/default/getUsers", "get", 0, responseHandler).start();
        return v;
    }
}

