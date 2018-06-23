package com.aayushsinha.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aayush on 12/06/18.
 */

class RecipeViewHolder extends RecyclerView.ViewHolder {

    private CardView v;
    @BindView(R.id.recipe_image) ImageView recipe_image;
    @BindView(R.id.recipe_name) TextView recipe_name;
    @BindView(R.id.recipe_serve_count) TextView recipe_serve_count;
    private Context context;

    RecipeViewHolder(CardView itemView) {
        super(itemView);
        v = itemView;
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    void setup(JSONObject recepie, final int jsonPosition){
        String recepieName = recepie.optString("name");
        String recepieServeCount = recepie.optString("servings");
        String recepieImage = recepie.optString("image");

        if(recepieImage.length() > 0) {
            Picasso.get().load(recepieImage).into(recipe_image);
        } else {
            recipe_image.setVisibility(View.GONE);
        }

        recipe_name.setText(recepieName);
        recipe_serve_count.setText(String.format("%s%s", context.getString(R.string.serveCountLabel), recepieServeCount));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, stepListActivity.class);
                intent.putExtra(MainActivity.JSONPositionKey, jsonPosition);
                context.startActivity(intent);
            }
        });
    }
}
