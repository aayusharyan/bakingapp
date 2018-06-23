package com.aayushsinha.android.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aayush on 15/06/18.
 */

public class ingredientViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ingredientName) TextView ingredientName;
    @BindView(R.id.ingredientQuant) TextView ingredientQuant;

    ingredientViewHolder(LinearLayout itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setup(JSONObject singleIngredient) {
        String name = singleIngredient.optString("ingredient", "");
        ingredientName.setText(name);

        String quant = singleIngredient.optString("quantity", "0");
        String unit = singleIngredient.optString("measure", "Units");

        ingredientQuant.setText(String.format("%s %s", quant, unit));
    }
}
