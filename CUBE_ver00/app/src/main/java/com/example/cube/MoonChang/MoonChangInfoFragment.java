package com.example.cube.MoonChang;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.example.cube.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//이페이지는 지도 띄우는거만 수행하고 나머지는 xml로 직접 입력했어요(잘안바뀌니까) 그리고 원산지,전화번호는 금정은있는데 문창은 안떠있어서 우선뺏어요
public class MoonChangInfoFragment extends Fragment
        implements OnMapReadyCallback
{
    private MapView mapView = null;
    private ScrollView scrollView;
    private FrameLayout frameLayout;

    public MoonChangInfoFragment()
    {
        // required
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_info, container, false);

        frameLayout = (FrameLayout)layout.findViewById(R.id.background_map);
        scrollView = (ScrollView)layout.findViewById(R.id.info_scroll);
        mapView = (MapView)layout.findViewById(R.id.map);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        });

        mapView.getMapAsync(this);

        return layout;
    }



    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }



    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng munchang = new LatLng(35.234102, 129.082083);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(munchang);

        markerOptions.title("문창회관");


        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(munchang,17));

      //  googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

}

