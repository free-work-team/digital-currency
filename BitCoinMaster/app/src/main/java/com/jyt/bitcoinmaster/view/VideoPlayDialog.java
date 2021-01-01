package com.jyt.bitcoinmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jyt.bitcoinmaster.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VideoPlayDialog extends Dialog {

    public VideoPlayDialog(@NonNull Context context) {
        super(context);
    }

    public VideoPlayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    public static class Builder implements View.OnClickListener {
        private Context context;
        private String title;
        private String content;
        private VideoView videoView;
        private TextView textViewTime;
        private TextView textViewCurrentPosition;
        private ImageView buttonPlay,buttonStop;
        private SeekBar seekBar;
        private Handler handler = new Handler();
        private Runnable runnable = new Runnable() {
            public void run() {
                if (videoView.isPlaying()) {
                    int current = videoView.getCurrentPosition();
                    seekBar.setProgress(current);
                    textViewCurrentPosition.setText(time(videoView.getCurrentPosition()));
                }
                handler.postDelayed(runnable, 500);
            }
        };
        public Builder(Context context) {
            this.context = context;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public VideoPlayDialog create() {
            LayoutInflater layoutInflater = (LayoutInflater) context.
                    getSystemService(context.LAYOUT_INFLATER_SERVICE);
            final VideoPlayDialog dialog = new VideoPlayDialog(context, R.style.Dialog);
            View layout = layoutInflater.inflate(R.layout.videoplaydialog, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            videoView = layout.findViewById(R.id.videoView);
            //设置播放地址
            videoView.setVideoPath(getContent());
            //设置准备好了的监听
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    handler.postDelayed(runnable, 0);
                    videoView.requestFocus();
                    videoView.start();
                    seekBar.setMax(videoView.getDuration());
                    textViewTime.setText(time(videoView.getDuration()));
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    dialog.dismiss();
                }
            });
            textViewTime = (TextView) layout.findViewById(R.id.textViewTime);
            seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
            // 为进度条添加进度更改事件
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

            textViewCurrentPosition = (TextView) layout.findViewById(R.id.textViewCurrentPosition);

            buttonPlay = (ImageView) layout.findViewById(R.id.buttonPlay);
            buttonStop = (ImageView) layout.findViewById(R.id.buttonStop);

            buttonPlay.setOnClickListener(this);
            buttonStop.setOnClickListener(this);
            return dialog;
        }

        private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            // 当进度条停止修改的时候触发
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 取得当前进度条的刻度
                int progress = seekBar.getProgress();
                if (videoView.isPlaying()) {
                    // 设置当前播放的位置
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        };


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonPlay:
                    play();
                    break;
                case R.id.buttonStop:
                    pause();
                    break;
                default:
                    break;
            }
        }
        protected void play() {
            buttonPlay.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
            // 开始线程，更新进度条的刻度
            handler.postDelayed(runnable, 0);
            videoView.start();
            seekBar.setMax(videoView.getDuration());
        }

        protected void pause() {
            if (videoView.isPlaying()) {
                videoView.pause();
                buttonPlay.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.GONE);
            }
        }
        protected String time(long millionSeconds) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(millionSeconds);
            return simpleDateFormat.format(c.getTime());
        }
    }
}
