package com.example.cube;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends Fragment {
    ImageView moonChang;
    ImageView saetBul;
    ImageView geumJeong;
    ImageView hakSaeng;
    Button notice_more;
    public HomeActivity() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        notice_more = (Button)view.findViewById(R.id.button_more_notice);
        moonChang = (ImageView)view.findViewById(R.id.button_moon);
        saetBul = (ImageView)view.findViewById(R.id.button_saet);
        geumJeong = (ImageView)view.findViewById(R.id.button_geum);
        hakSaeng = (ImageView)view.findViewById(R.id.button_hak);

        notice_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NoticeActivity();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_layout,fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        moonChang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(getActivity().getApplicationContext(),MoonChangActivity.class));
                // Toast.makeText(getActivity().getApplicationContext(),"문창회관 클릭됨.",Toast.LENGTH_SHORT).show();
            }
        });
        saetBul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"샛벌회관 클릭됨.",Toast.LENGTH_SHORT).show();
            }
        });
        geumJeong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"금정회관 클릭됨.",Toast.LENGTH_SHORT).show();
            }
        });
        hakSaeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"학생회관 클릭됨.",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
