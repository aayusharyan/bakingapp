package com.aayushsinha.android.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ingredientActivity extends AppCompatActivity {

    @BindView(R.id.ingredientsRecyclerView) RecyclerView ingredientsRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if(getIntent() == null) {
            finish();
        }
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int currentRecipeID = getIntent().getIntExtra(MainActivity.JSONPositionKey, 0);
        JSONObject currentRecipe = MainActivity.recipeData.optJSONObject(currentRecipeID);

        JSONArray currentIngredients = currentRecipe.optJSONArray("ingredients");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ingredientsRecyclerView.setLayoutManager(layoutManager);
        ingredientAdapter adapter = new ingredientAdapter(currentIngredients);
        ingredientsRecyclerView.setAdapter(adapter);

        ingredientsRecyclerView.addItemDecoration(new DividerItemDecoration(ingredientsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

}
