package com.example.cube;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;

public class MoonChangWeekMenu extends Fragment
{
    int layout;
    public MoonChangWeekMenu() {}
    public MoonChangWeekMenu setup(int _layout)
    {
        layout = _layout;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(layout, container, false);
        return linearLayout;
    }
}