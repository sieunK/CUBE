package com.example.cube.Administrator;

import com.example.cube.Administrator.Board.BoardActivity;
import com.example.cube.Opening.LoginActivity;
import com.example.cube.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cube.Administrator.Menu.MenuConfigActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ConfigFragment extends Fragment {
    @Nullable
    private Bundle savedInstanceState;
    private FirebaseAuth mAuth;
    private ListView mListView;
    private static final String[] MENU = {"공지사항", "메뉴 설정", "광고 설정", "기본 설정", "로그아웃"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_fragment, null);

        mAuth = FirebaseAuth.getInstance();

        mListView = (ListView) view.findViewById(R.id.config_view);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, MENU);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListView Clicked ", position + "clicked");
                switch (position){
                    case 0: {
                        Toast.makeText(getActivity(), "공지사항" ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), BoardActivity.class));
                        break;
                    }
                    case 1: {
                        Toast.makeText(getActivity(), "메뉴 설정 페이지입니다" ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MenuConfigActivity.class));
                        break;
                    }
                    case 2:{
                        Toast.makeText(getActivity(), "광고 설정 페이지입니다" ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), AdConfigActivity.class));
                        break;                    }
                    case 3:{
                        Toast.makeText(getActivity(), "앱 기본 설정 페이지입니다" ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), AppConfigActivity.class));
                        break;                    }
                    case 4:{
                        mAuth.signOut();
                        Toast.makeText(getActivity(), "로그아웃되었습니다." ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                        break;
                    }
                    default: {
                        break;

                    }
                }
            }
        });
        return view;
    }


}
