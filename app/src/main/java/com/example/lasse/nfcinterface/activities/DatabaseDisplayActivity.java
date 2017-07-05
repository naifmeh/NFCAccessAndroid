package com.example.lasse.nfcinterface.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;
import com.example.lasse.nfcinterface.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseDisplayActivity extends AppCompatActivity implements NetworkUtils.ResponseHttpCallback,NetworkUtils.ReponseDeleteHttpCallback{

    private static final String TAG = DatabaseDisplayActivity.class.getSimpleName();
    private ArrayList<JSONObject> jsonArray;

    private RecyclerView recyclerView;
    private MyRecyclerAdapter mAdapter;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;

    private int userRank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_display);
        progressBar = (ProgressBar) findViewById(R.id.progressBarDatabaseFetch);

        sharedPreferences = SharedPrefsHelper.getSharedPrefs(this,getString(R.string.sharedPrefsKey),Context.MODE_PRIVATE);
        if(sharedPreferences == null && !sharedPreferences.contains(NetworkUtils.UID_FIELD))
            startActivity(new Intent(DatabaseDisplayActivity.this,MainActivity.class));

        this.userRank = SharedPrefsHelper.getRank();
        if(this.userRank == -1) startActivity(new Intent(DatabaseDisplayActivity.this,MainActivity.class));
        else if(this.userRank < 1) {
            Toast.makeText(this,getString(R.string.errorPrivilege),Toast.LENGTH_LONG).show();
            startActivity(new Intent(DatabaseDisplayActivity.this,ProfileActivity.class));
        }

        URL url = NetworkUtils.getUrlForGetAll();
        NetworkUtils.sendGetAllUsersRequest(this,url,this);

        recyclerView = (RecyclerView) findViewById(R.id.databaseRecyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void reponseHttpPostRequete(int reponse) {

    }

    @Override
    public void reponseHttpGetAllRequete(JSONObject jObj) {
        try {
            JSONArray jsonArray = jObj.getJSONArray("users");
            ArrayList<JSONObject> arrayJson = new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayJson.add(jsonObject);
                Log.d(TAG,"JSON OBJECT -- "+jsonObject.toString());
            }

            this.jsonArray = arrayJson;
            mAdapter = new MyRecyclerAdapter(this.jsonArray);
            recyclerView.setAdapter(mAdapter);

            mAdapter.notifyDataSetChanged();



        } catch(JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void reponseHttpDeleteUser(int reponse) {

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView uid,name,lastName,rank;
        private ImageButton deleteButton;


        public MyViewHolder(View itemView) {
            super(itemView);

            uid = (TextView) itemView.findViewById(R.id.uidDatabase);
            name = (TextView) itemView.findViewById(R.id.NameDatabase);
            lastName = (TextView) itemView.findViewById(R.id.lastNameDatabase);
            rank = (TextView) itemView.findViewById(R.id.rankDatabase);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteDatabase);
        }
    }
    private class MyRecyclerAdapter extends RecyclerView.Adapter<DatabaseDisplayActivity.MyViewHolder> {
        private ArrayList<JSONObject> jsonObjArray;

        public MyRecyclerAdapter(ArrayList<JSONObject> jsonTab) {
            this.jsonObjArray = jsonTab;

        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.user_row_database,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            if(position ==0) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            if(this.jsonObjArray != null) {
                try {
                    holder.uid.setText(jsonObjArray.get(position).getString(NetworkUtils.UID_FIELD));
                    Log.d(TAG,"POSITION -- "+position);
                    holder.name.setText(jsonObjArray.get(position).getString(NetworkUtils.NAME_FIELD));
                    holder.lastName.setText(jsonObjArray.get(position).getString(NetworkUtils.LAST_NAME_FIELD));
                    holder.rank.setText(jsonObjArray.get(position).getString(NetworkUtils.RANK_FIELD));

                    holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int pos = holder.getAdapterPosition();
                            final JSONObject tempJson = jsonArray.get(pos);
                            jsonArray.remove(pos);
                            notifyItemRemoved(pos);
                            Snackbar.make(view,R.string.userDeletedSnackbar,Snackbar.LENGTH_LONG)
                                    .addCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar transientBottomBar, int event) {
                                            int position = holder.getAdapterPosition();
                                            switch(event) {
                                                case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                                    jsonArray.add(pos,tempJson);
                                                    notifyItemInserted(pos);
                                                    break;
                                                default:
                                                    URL url = NetworkUtils.getUrlForDelete(holder.uid.getText().toString().trim());
                                                    NetworkUtils.sendDeleteUserRequest(DatabaseDisplayActivity.this,url,DatabaseDisplayActivity.this);

                                                    break;
                                            }
                                        }
                                    }).setAction(R.string.undoSnackBar, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();

                            mAdapter.notifyItemRangeChanged(pos,mAdapter.getItemCount());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public int getItemCount() {
            if(this.jsonObjArray != null) {
                Log.d(TAG,"Array size : "+String.valueOf(this.jsonObjArray.size()));
                if(this.jsonObjArray.size() == 0) {
                    Log.d(TAG,"NO DATA TO DISPLAY");
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    Snackbar.make(recyclerView,R.string.noUserInDb,Snackbar.LENGTH_LONG).addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            switch(event) {
                                case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                    startActivity(new Intent(DatabaseDisplayActivity.this,MainActivity.class));
                                    break;
                            }
                        }
                    }).setAction("Add user", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(DatabaseDisplayActivity.this,AddCardActivity.class));
                        }
                    }).show();

                }
                return this.jsonObjArray.size();
            }
            return 0;
        }
    }
}
