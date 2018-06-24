package com.aayushsinha.android.bakingapp;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class stepDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.step_media_video) PlayerView playerView;
    @BindView(R.id.videoBufferingPB) ProgressBar videoBufferingPB;
    @BindView(R.id.exo_controller_fullscreen) ImageView fullScreenBtn;
    @BindView(R.id.exo_controller) LinearLayout playbackControlView;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout appBar;
    @BindView(R.id.step_detail_container) NestedScrollView stepDetailContainer;

    private Dialog mFullScreenDialog;
    private boolean isFullScreen;
    private boolean isZoomed;
    private SimpleExoPlayer player;
    private String videoURL;
    private long position;
    public static String SELECTED_POSITION = "exo_player_time_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(stepListActivity.STEP_ID_KEY, getIntent().getStringExtra(stepListActivity.STEP_ID_KEY));
            arguments.putString(stepListActivity.SHORT_DESC_KEY, getIntent().getStringExtra(stepListActivity.SHORT_DESC_KEY));
            arguments.putString(stepListActivity.DESC_KEY, getIntent().getStringExtra(stepListActivity.DESC_KEY));
            arguments.putString(stepListActivity.THUMBNAIL_URL_KEY, getIntent().getStringExtra(stepListActivity.THUMBNAIL_URL_KEY));
            stepDetailFragment fragment = new stepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.step_detail_container, fragment).commit();
        } else {
            position = savedInstanceState.getLong(SELECTED_POSITION, 0);
        }

        isFullScreen = false;

        videoURL = getIntent().getStringExtra(stepListActivity.VIDEO_URL_KEY);

        if(Objects.equals(videoURL, "")) {
            toolbarLayout.removeView(playerView);
            appBar.setExpanded(false, false);
            appBar.setActivated(false);
            appBar.setMinimumHeight(toolbar.getHeight());
            ViewCompat.setNestedScrollingEnabled(stepDetailContainer,false);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBar.getLayoutParams();
            if(lp != null) {
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) lp.getBehavior();
                if (behavior != null) {
                    behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                        @Override
                        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                            return false;
                        }
                    });
                    lp.setBehavior(behavior);
                    appBar.setLayoutParams(lp);
                }
            }
        } else {
            initPlayer();
        }
    }

    private void initPlayer() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        if (position != 0) {
            player.seekTo(position);
        }

        playerView.setPlayer(player);


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);

        final MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoURL));

        player.prepare(videoSource);
        player.setPlayWhenReady(true);

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
                    videoBufferingPB.setVisibility(View.VISIBLE);
                } else {
                    videoBufferingPB.setVisibility(View.GONE);
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
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
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
        toolbarLayout.addView(playerView, 0);
        isFullScreen = false;
        if(isZoomed) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        ((ImageView)playerView.findViewById(R.id.exo_controller_fullscreen)).setImageDrawable(getDrawable(R.drawable.ic_fullscreen));
        mFullScreenDialog.dismiss();
    }

    public void toggleFullScreen(View view) {
        if (isFullScreen) {
            closeFullscreenDialog();
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(stepDetailActivity.this, R.drawable.ic_fullscreen));
        } else {
            openFullscreenDialog();
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(stepDetailActivity.this, R.drawable.ic_fullscreen_exit));
        }
    }

    public void toggleZoom(View view) {
        if(isZoomed) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            isZoomed = false;
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(stepDetailActivity.this, R.drawable.ic_zoom_in));
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            isZoomed = true;
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(stepDetailActivity.this, R.drawable.ic_zoom_out));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(player != null) {
            position = player.getCurrentPosition();
            player.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null) {
            position = player.getCurrentPosition();
            player.release();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(SELECTED_POSITION, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getLong(SELECTED_POSITION, 0);
        if(player != null) {
            player.seekTo(position);
        }
    }
}
