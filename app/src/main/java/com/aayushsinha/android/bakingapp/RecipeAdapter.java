package com.aayushsinha.android.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aayush on 12/06/18.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private JSONArray recepies;

    RecipeAdapter() {
        this.recepies = MainActivity.recipeData;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_layout, parent, false);

        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        try {
            JSONObject singleRecepie = recepies.getJSONObject(position);
            holder.setup(singleRecepie, position);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", "Error Parsing JSON");
        }
    }

    @Override
    public int getItemCount() {
        return recepies.length();
    }
}
