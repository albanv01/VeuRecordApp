package com.example.recordveu;

import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.recordveu.MainViewModel;
import com.example.recordveu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    private MediaRecorder mMediaRecorder;


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private ImageView logo;
    private FloatingActionButton record;
    private FloatingActionButton stop;
    private FloatingActionButton play;

    private String fileName;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        logo = view.findViewById(R.id.logo);
        record = view.findViewById(R.id.record);
        stop = view.findViewById(R.id.stop);
        play = view.findViewById(R.id.play);

        String ruta = getContext().getExternalFilesDir(null).getAbsolutePath();
        fileName = ruta + "/audiorecord.3gp";

        /*empezar la grabacion*/
        record.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_baseline_record_voice_over_24));
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(fileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                    Log.e("RECORDING",
                            "Se ha empezado a grabar");
                } catch (IOException e) {
                    Log.e("RECORDING",
                            "No se puede iniciar la grabación");
                }

                mRecorder.start();
            }
        });



        stop.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_baseline_mic_24));

            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            } else if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        });



        play.setOnClickListener(v -> {
            logo.setImageDrawable
                    (getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
            if (mRecorder == null && mPlayer == null) {
                mPlayer = new MediaPlayer();

                try {
                    mPlayer.setDataSource(fileName);
                    mPlayer.prepare();
                    mPlayer.start();

                    mPlayer.setOnCompletionListener(mediaPlayer -> {
                        stop.callOnClick();
                    });
                } catch (IOException e) {
                    Log.e("RECORDING", "No se puede iniciar la reproducción");
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(
                    getContext(),
                    "Permission needed",
                    Toast.LENGTH_LONG
            ).show();
            getActivity().finish();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}