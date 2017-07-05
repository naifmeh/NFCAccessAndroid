package com.example.lasse.nfcinterface.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView uidProfileField,nameProfileField,lastNameProfileField;
    private ImageButton cloudButton,addUserButton,emulateButton,logOutButton;

    private ImageView oneStarImg,twoStarImg,threeStarImg,fourStarImg;

    private String uidUser,nameUser,lastNameUser;
    private int rankUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uidProfileField = (TextView) findViewById(R.id.uidProfileField);
        nameProfileField = (TextView) findViewById(R.id.nameProfileField);
        lastNameProfileField = (TextView) findViewById(R.id.lastNameProfileField);

        cloudButton = (ImageButton) findViewById(R.id.checkDatabaseButton);
        addUserButton = (ImageButton) findViewById(R.id.addUsrButton);
        emulateButton = (ImageButton) findViewById(R.id.emulateCardButton);
        logOutButton = (ImageButton) findViewById(R.id.logOutButton);

        oneStarImg = (ImageView) findViewById(R.id.oneStarImg);
        twoStarImg = (ImageView) findViewById(R.id.twoStarImg);
        threeStarImg = (ImageView) findViewById(R.id.threeStarImg);
        fourStarImg = (ImageView) findViewById(R.id.fourStarImg);

        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefsKey), Context.MODE_PRIVATE);
        if(sharedPreferences == null && !sharedPreferences.contains(NetworkUtils.UID_FIELD)) startActivity(new Intent(ProfileActivity.this,MainActivity.class));

        uidUser = sharedPreferences.getString(NetworkUtils.UID_FIELD,getString(R.string.errorNotConnected));
        nameUser = sharedPreferences.getString(NetworkUtils.NAME_FIELD,getString(R.string.errorNotConnected));
        lastNameUser = sharedPreferences.getString(NetworkUtils.LAST_NAME_FIELD,getString(R.string.errorNotConnected));
        rankUser = sharedPreferences.getInt(NetworkUtils.RANK_FIELD,-1);

        uidProfileField.setText(uidUser);
        nameProfileField.setText(nameUser);
        lastNameProfileField.setText(lastNameUser);

        handleRank(rankUser);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,AddCardActivity.class));
            }
        });

        cloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,DatabaseDisplayActivity.class));
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });

    }

    private void handleRank(int rank) {
        switch(rank) {
            case 0:
                oneStarImg.setVisibility(View.VISIBLE);
                break;
            case 1:
                oneStarImg.setVisibility(View.VISIBLE);
                twoStarImg.setVisibility(View.VISIBLE);
                break;
            case 2:
                oneStarImg.setVisibility(View.VISIBLE);
                twoStarImg.setVisibility(View.VISIBLE);
                threeStarImg.setVisibility(View.VISIBLE);
                break;
            case 3:
                oneStarImg.setVisibility(View.VISIBLE);
                twoStarImg.setVisibility(View.VISIBLE);
                threeStarImg.setVisibility(View.VISIBLE);
                fourStarImg.setVisibility(View.VISIBLE);
                break;
        }
    }
}
