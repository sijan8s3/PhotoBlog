package com.devs.blog.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.devs.blog.R;

public class NotificationFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {


        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
}

