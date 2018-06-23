package com.aayushsinha.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ingredientWidgetConfigureActivity ingredientWidgetConfigureActivity}
 */
public class ingredientWidget extends AppWidgetProvider {

    private static String ingredientData;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        ingredientData = ingredientWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        setRemoteAdapter(context, views);

//        try {
//            JSONArray ingredientList = new JSONArray(ingredientData);
//            updateList(views, ingredientList);
//
//        } catch (JSONException e) {
//            Log.e("JSON", "Not able to Parse JSON");
//            e.printStackTrace();
//        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            ingredientWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        if(isJSONValid(ingredientData)) {
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra("data", ingredientData);
            views.setRemoteAdapter(R.id.ingredientListView, intent);
        }
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONArray(test);
        } catch (JSONException ex1) {
            try {
                new JSONObject(test);
            } catch (JSONException ex) {
                return false;
            }
        }
        return true;
    }

}

