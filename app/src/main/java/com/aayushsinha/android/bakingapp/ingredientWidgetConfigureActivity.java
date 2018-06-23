package com.aayushsinha.android.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The configuration screen for the {@link ingredientWidget ingredientWidget} AppWidget.
 */
public class ingredientWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.aayushsinha.android.bakingapp.ingredientWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    public static final String JSON_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private RequestQueue queue;
    public static JSONArray recipeData;

    @BindView(R.id.recipeList) ListView recipeList;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public ingredientWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredient_widget_configure);

        ButterKnife.bind(this);

        queue = Volley.newRequestQueue(this);
        loadRecepies();

        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject selectedRecipe = recipeData.optJSONObject(position);
                JSONArray recipeIngredients = selectedRecipe.optJSONArray("ingredients");
                String strIngredients = recipeIngredients.toString();
                Context context = ingredientWidgetConfigureActivity.this;

                saveTitlePref(context, mAppWidgetId, strIngredients);

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ingredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
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
                        Log.e("INTERNET", "Not able to download Data");
                }
        });
        queue.add(jsonArrayRequest);
    }

    private void setupView(JSONArray response) {
        recipeData = response;
        ArrayList<String> items = new ArrayList<>();
        for(int i=0; i < response.length() ; i++) {
            JSONObject singleRecipe = response.optJSONObject(i);
            String name = singleRecipe.optString("name", "");
            items.add(name);
        }

        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, items);
        recipeList.setAdapter(mArrayAdapter);
    }
}

