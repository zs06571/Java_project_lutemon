package com.example.java_project_lutemon.ui.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.java_project_lutemon.R;

public class SplashFragment extends Fragment {

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView logo = view.findViewById(R.id.iv_logo);
        TextView title = view.findViewById(R.id.tv_title);
        LottieAnimationView particle = view.findViewById(R.id.lottie_particle);
        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotationBy(360f)
                .setDuration(1400)
                .setStartDelay(200)
                .start();

        title.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(1600)
                .start();

        new Handler().postDelayed(() -> {
            if (isAdded()) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_splash_to_login);
            }
        }, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
