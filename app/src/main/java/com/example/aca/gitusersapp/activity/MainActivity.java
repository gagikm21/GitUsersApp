package com.example.aca.gitusersapp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.aca.gitusersapp.R;
import com.example.aca.gitusersapp.adapter.CustomScrollListener;
import com.example.aca.gitusersapp.adapter.UserItemAdapter;
import com.example.aca.gitusersapp.client.ApiManager;
import com.example.aca.gitusersapp.client.Result;
import com.example.aca.gitusersapp.utils.NetworkUtils;
import com.example.aca.gitusersapp.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements UserItemAdapter.OnItemClickListener {

    UserItemAdapter userItemAdapter;
    ArrayList<Result.UsersList> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsers = new ArrayList<>();
        loadUsers(0);
        initRecyclerView();
    }

    private void initRecyclerView(){
        userItemAdapter = new UserItemAdapter(mUsers,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        CustomScrollListener scrollListener = new CustomScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadUsers(totalItemsCount);
            }
        };
        RecyclerView mRecyclerView = findViewById(R.id.rv_main);
        mRecyclerView.addOnScrollListener(scrollListener);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(userItemAdapter);
    }

    private void loadUsers(int page) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            Call<List<Result.UsersList>> call = ApiManager.getApiClient().getUsers(page, 10);
            call.enqueue(new Callback<List<Result.UsersList>>() {
                @Override
                public void onResponse(Call<List<Result.UsersList>> call, Response<List<Result.UsersList>> response) {
                    List<Result.UsersList> users = response.body();
                    if (users != null) {
                        mUsers.addAll(users);
                        userItemAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Result.UsersList>> call, Throwable t) {
                    Log.v("TAG", "Failure : " + t.toString());
                }
            });
        } else {
            Log.v("TAG", "No network connection");
        }
    }

    @Override
    public void onItemClicked(int adapterPosition) {
        openFragmentInContainer(adapterPosition);
    }

    private void openFragmentInContainer(int adapterPosition){
        UserFragment userFragment = new UserFragment();
        userFragment.userId = adapterPosition + 1;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_main, userFragment);
        fragmentTransaction.addToBackStack(userFragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }
}