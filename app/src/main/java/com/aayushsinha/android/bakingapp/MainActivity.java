package com.aayushsinha.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String JSON_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private RequestQueue queue;

    public static JSONArray recipeData;
    public static String JSONPositionKey = "JSONPosition";

    @BindView(R.id.main_swiper) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.main_loading_progress) ProgressBar loadingProgressBar;
    @BindView(R.id.errorMessage) TextView errorMessage;
    @BindView(R.id.recipesRecyclerView) RecyclerView recepiesRecyclerView;

    @Nullable private IdilingResourceForTest idilingResourceForTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(idilingResourceForTest != null) {
            idilingResourceForTest.setIdleState(false);
        }

        queue = Volley.newRequestQueue(this);
        loadRecepies();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecepies();
            }
        });

    }

    private void loadRecepies() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
            (Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    setupView(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingProgressBar.setVisibility(View.GONE);
                    errorMessage.setText(R.string.errorLoadingDataMessage);

                }
            });
        queue.add(jsonArrayRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    if(idilingResourceForTest != null) {
                        idilingResourceForTest.setIdleState(true);
                    }
                }
            }
        });
    }

    private void setupView(JSONArray recipes) {
        loadingProgressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);

        int spanCount = MainActivity.calculateNoOfColumns(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        recepiesRecyclerView.setLayoutManager(gridLayoutManager);

        recipeData = recipes;
        RecipeAdapter recepieAdapter = new RecipeAdapter();
        recepiesRecyclerView.setAdapter(recepieAdapter);
        recepiesRecyclerView.setVisibility(View.VISIBLE);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        if(displayMetrics.heightPixels > displayMetrics.widthPixels) {
            //In case of Portrait, Only show one Card in a row.
            return 1;
        }

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdilingResource() {
        if (idilingResourceForTest == null) {
            idilingResourceForTest = new IdilingResourceForTest();
        }
        return idilingResourceForTest;
    }
}
