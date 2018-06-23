package com.aayushsinha.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by aayush on 14/06/18.
 */

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {
    private final stepListActivity mParentActivity;
    private JSONArray steps;


    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONObject singleStep = (JSONObject) view.getTag();
            int identifier = singleStep.optInt("id", 0);
            String shortDescription = singleStep.optString("shortDescription", "");
            String description = singleStep.optString("description", "");
            String videoURL = singleStep.optString("videoURL", "");
            String thumbnailURL = singleStep.optString("thumbnailURL", "");

            if (mTwoPane) {
                Bundle arguments = new Bundle();

                arguments.putInt(stepListActivity.STEP_ID_KEY, identifier);
                arguments.putString(stepListActivity.SHORT_DESC_KEY, shortDescription);
                arguments.putString(stepListActivity.DESC_KEY, description);
                arguments.putString(stepListActivity.VIDEO_URL_KEY, videoURL);
                arguments.putString(stepListActivity.THUMBNAIL_URL_KEY, thumbnailURL);

                stepDetailFragment fragment = new stepDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.step_detail_container, fragment).commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, stepDetailActivity.class);

                intent.putExtra(stepListActivity.STEP_ID_KEY, identifier);
                intent.putExtra(stepListActivity.SHORT_DESC_KEY, shortDescription);
                intent.putExtra(stepListActivity.DESC_KEY, description);
                intent.putExtra(stepListActivity.VIDEO_URL_KEY, videoURL);
                intent.putExtra(stepListActivity.THUMBNAIL_URL_KEY, thumbnailURL);

                context.startActivity(intent);
            }
        }
    };

    StepAdapter(stepListActivity parent, JSONArray stepList, boolean twoPane) {
        steps = stepList;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_content, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepViewHolder holder, int position) {
        holder.setup(steps.optJSONObject(position));
        holder.v.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return steps.length();
    }
}
