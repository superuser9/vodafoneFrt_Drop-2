package com.vodafone.frt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.UserListAdapter;
import com.vodafone.frt.models.UserDataModel;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.Constants;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {

    private ListView lv_userList;
    private FRTSharePrefUtil frtSharePrefUtil;
    private String userName;
    private LinearLayout ivback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) findViewById(R.id.headerText);
        ivback = (LinearLayout) findViewById(R.id.ivback);
        ivback.setOnClickListener(cqBackClick);
        //setSupportActionBar(toolbar);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(getApplicationContext());
        try {
            userName =  AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.username_shared)).getBytes("UTF-16LE"), Base64.DEFAULT));
          // mTitle.setText(userName);
            //mTitle.setText(userName);
            mTitle.setText(getString(R.string.chat));
        } catch (Exception e) {
            e.printStackTrace();
        }
       // toolbar.setTitle(userName);
        if(getIntent() == null || getIntent().getSerializableExtra("data") == null)return;

        ArrayList<UserDataModel> usersLIst = (ArrayList<UserDataModel>) getIntent().getSerializableExtra("data");
        lv_userList = (ListView)findViewById(R.id.lv_user_list);
        lv_userList.setAdapter(new UserListAdapter(this,usersLIst));
        lv_userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDataModel userModel = (UserDataModel) view.getTag(R.integer.data);
                Intent mIntent = new Intent(UsersListActivity.this,ChatActivity.class);
                mIntent.putExtra(Constants.SENDER_ID,userModel.getUser_id());
                mIntent.putExtra(Constants.SENDER_NAME,userModel.getUser_name());
                startActivity(mIntent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


   /* private ArrayList<UserModel> getUserLIst(ArrayList<UserDataModel> data){
        ArrayList<UserModel> mList = new ArrayList<>();
        for (UserDataModel temp : data){
            UserModel model = new UserModel(temp.getUser_id(),temp.getUser_name());
            mList.add(model);
        }
        return mList;
    }
*/

    private final View.OnClickListener cqBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(this,PTRNavigationScreenActivity.class);
        startActivity(mIntent);
        finish();

    }
}
