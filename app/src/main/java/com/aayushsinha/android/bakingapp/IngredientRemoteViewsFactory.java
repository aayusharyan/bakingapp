package com.aayushsinha.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IngredientRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    JSONArray data = new JSONArray();
    private Context context;

    IngredientRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        String ingredientsData = intent.getStringExtra("data");
        if(ingredientsData != null && ingredientsData.length() > 0) {
            try {
                JSONArray ingredients = new JSONArray(ingredientsData);
                setData(ingredients);
            } catch (JSONException e) {
                Log.e("JSON", "Not able to Parse JSON Properly");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.single_ingredient);
        JSONObject singleIngredient = data  .optJSONObject(position);

        String ingredient = singleIngredient.optString("ingredient", "");
        view.setTextViewText(R.id.ingredientName, ingredient);

        String quant = singleIngredient.optString("quantity", "0");
        String unit = singleIngredient.optString("measure", "Units");
        view.setTextViewText(R.id.ingredientQuant, String.format("%s %s", quant, unit));

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void setData(JSONArray ingredients) {
        data = ingredients;
    }
}
