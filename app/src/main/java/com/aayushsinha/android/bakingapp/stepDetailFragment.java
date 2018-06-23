package com.aayushsinha.android.bakingapp;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class stepDetailFragment extends Fragment {

    @BindView(R.id.short_description) TextView shortDescriptionTV;
    @BindView(R.id.description) TextView descriptionTV;
    @BindView(R.id.thumbnail) ImageView recipeImage;
    @BindView(R.id.tablet_video_holder) PlayerView playerView;
    @BindView(R.id.videoBufferingProgressBar) ProgressBar videoBufferingProgressBar;
    @BindView(R.id.fragmentRoot) LinearLayout fragmentRoot;
    @BindView(R.id.playerContainer) LinearLayout playerContainer;

    private String shortDescription;
    private String description;
    private String imageURL;

    private Dialog mFullScreenDialog;
    private boolean isFullScreen;
    private boolean isZoomed;
    private String videoURL;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public stepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this.getActivity();
        if(activity == null) {
            return;
        }
        if(getArguments() == null) {
            return;
        }

        shortDescription = "";
        description = "";
        imageURL = "";
        videoURL = "";

        if (getArguments().containsKey(stepListActivity.SHORT_DESC_KEY)) {
            shortDescription = getArguments().getString(stepListActivity.SHORT_DESC_KEY);
        }

        if(getArguments().containsKey(stepListActivity.DESC_KEY)) {
            description = getArguments().getString(stepListActivity.DESC_KEY);
        }

        if(getArguments().containsKey(stepListActivity.THUMBNAIL_URL_KEY)) {
            imageURL = getArguments().getString(stepListActivity.THUMBNAIL_URL_KEY);
        }

        if(getArguments().containsKey(stepListActivity.VIDEO_URL_KEY)) {
            videoURL = getArguments().getString(stepListActivity.VIDEO_URL_KEY);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.step_detail, container, false);
        ButterKnife.bind(this, view);

        shortDescriptionTV.setText(shortDescription);
        descriptionTV.setText(description);

        if(imageURL.length() > 0) {
            Picasso.get().load(imageURL).into(recipeImage);
        }

        isFullScreen = false;

        if(!Objects.equals(videoURL, "")) {
            initPlayer();
        }

        return view;
    }



    private void initPlayer() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        playerView.setPlayer(player);


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(Objects.requireNonNull(getActivity()), Util.getUserAgent(getActivity(), getString(R.string.app_name)), bandwidthMeter);

        final MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoURL));

        player.prepare(videoSource);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == Player.STATE_BUFFERING) {
                    videoBufferingProgressBar.setVisibility(View.VISIBLE);
                } else {
                    videoBufferingProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        initFullscreenDialog();
        playerContainer.setVisibility(View.VISIBLE);
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(Objects.requireNonNull(getActivity()), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Override
            public void onBackPressed() {
                closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        isFullScreen = true;
        if(isZoomed) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        playerContainer.addView(playerView, 0);
        isFullScreen = false;
        if(isZoomed) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        ((ImageView)playerView.findViewById(R.id.exo_controller_fullscreen)).setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_fullscreen));
        mFullScreenDialog.dismiss();
    }

    public void toggleFullScreen(View view) {
        if (isFullScreen) {
            closeFullscreenDialog();
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_fullscreen));
        } else {
            openFullscreenDialog();
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_fullscreen_exit));
        }
    }

    public void toggleZoom(View view) {
        if(isZoomed) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            isZoomed = false;
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_zoom_in));
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            isZoomed = true;
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_zoom_out));
        }
    }

//
//    @Override
//    protected void onDestroy() {
//        if(player != null) {
//            player.release();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(player != null) {
//            player.release();
//        }
//    }

}
