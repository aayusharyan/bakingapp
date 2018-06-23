package com.aayushsinha.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class stepListActivity extends AppCompatActivity {

    public static String STEP_ID_KEY        = "IDENTIFIER";
    public static String SHORT_DESC_KEY     = "SHORT_DESCRIPTION";
    public static String DESC_KEY           = "DESCRIPTION";
    public static String VIDEO_URL_KEY      = "VIDEO_URL";
    public static String THUMBNAIL_URL_KEY  = "THUMBNAIL_URL";
    private static final int MENU_ITEM_ING  = 100;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.step_list) RecyclerView recyclerView;
    private boolean mTwoPane;
    private int currentRecipeID;
    private JSONObject currentRecipe;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == MENU_ITEM_ING) {
            Intent intent = new Intent(this, ingredientActivity.class);
            intent.putExtra(MainActivity.JSONPositionKey, currentRecipeID);
            startActivity(intent);
        } else {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, MENU_ITEM_ING, Menu.NONE, "Ingredients");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);

        ButterKnife.bind(this);

        if(getIntent() == null) {
            finish();
        }
        currentRecipeID = getIntent().getIntExtra(MainActivity.JSONPositionKey, 0);

        currentRecipe = MainActivity.recipeData.optJSONObject(currentRecipeID);
        if(currentRecipe == null) {
            finish();
        }


        String recipeName = currentRecipe.optString("name", "");
        toolbar.setTitle(recipeName);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }

        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        JSONArray stepsData = currentRecipe.optJSONArray("steps");
        recyclerView.setAdapter(new StepAdapter(this, stepsData, mTwoPane));
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void toggleFullScreen(View view) {
        if(mTwoPane) {
            stepDetailFragment fragment = (stepDetailFragment) getSupportFragmentManager().findFragmentById(R.id.step_detail_container);
            if(fragment != null) {
                fragment.toggleFullScreen(view);
            }
        }
    }

    public void toggleZoom(View view) {
        if(mTwoPane) {
            stepDetailFragment fragment = (stepDetailFragment)getSupportFragmentManager().findFragmentById(R.id.step_detail_container);
            fragment.toggleZoom(view);
        }
    }
}
