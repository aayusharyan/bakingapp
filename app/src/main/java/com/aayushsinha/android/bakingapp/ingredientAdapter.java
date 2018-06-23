package com.aayushsinha.android.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by aayush on 15/06/18.
 */

public class ingredientAdapter extends RecyclerView.Adapter<ingredientViewHolder> {

    private JSONArray ingredients;

    ingredientAdapter(JSONArray ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ingredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_ingredient, parent, false);
        return new ingredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ingredientViewHolder holder, int position) {
        JSONObject singleIngredient = ingredients.optJSONObject(position);
        holder.setup(singleIngredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.length();
    }
}
