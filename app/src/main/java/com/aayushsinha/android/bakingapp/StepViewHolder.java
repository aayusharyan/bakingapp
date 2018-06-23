package com.aayushsinha.android.bakingapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aayush on 14/06/18.
 */

class StepViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.id_text) TextView stepID;
    @BindView(R.id.content) TextView stepContent;

    public CardView v;

    StepViewHolder(CardView view) {
        super(view);
        v = view;
        ButterKnife.bind(this, view);
    }

    void setup(JSONObject singleStep) {
        String stepName = singleStep.optString("shortDescription", "");
        final int stepIdentifier = singleStep.optInt("id", 0);

        v.setTag(singleStep);
        stepContent.setText(stepName);
        stepID.setText(String.valueOf(stepIdentifier + 1));
    }
}
