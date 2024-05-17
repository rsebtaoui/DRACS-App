package com.example.DRACS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class R_centres extends Fragment {


    public R_centres() {
        // Required empty public constructor
    }


    public static R_centres newInstance(String param1, String param2) {
        R_centres fragment = new R_centres();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_centres, container, false);

        Button eljadida = view.findViewById(R.id.btnElJadida);
        Button casa = view.findViewById(R.id.btncasa);
        Button bnsliman = view.findViewById(R.id.btnbnsliman);
        Button settat = view.findViewById(R.id.btnsettat);
        Button barrchid = view.findViewById(R.id.btnbarrchid);
        Button zmamra = view.findViewById(R.id.btnkhmis);
        Button wladfraj = view.findViewById(R.id.btnwladfraj);
        Button sidibennour = view.findViewById(R.id.btnSB);

        eljadida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openGoogleMaps(33.247881771701344, -8.502983357646494);
            }
        });

        casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.59457671474566, -7.60098941654711);
            }
        });

        bnsliman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.61036819358808, -7.124757284407918);
            }
        });

        settat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.012851049943926, -7.616730529883791);
            }
        });

        barrchid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.2738516182784, -7.586592444747574);
            }
        });

        zmamra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(32.6305907107246, -8.754465575491885);
            }
        });

        wladfraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(32.95870479583255, -8.221324423614597);
            }
        });

        sidibennour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(32.65750840219919, -8.425363994368995);
            }
        });


        return view;
    }

    private void openGoogleMaps(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(Label+Name)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }



    @Override
    public void onStart() {
        super.onStart();

        // Assuming your fragment is attached to MainActivity
        Activity_main mainActivity = (Activity_main) getActivity();
        if (mainActivity != null) {
            mainActivity.changeIconmenu(R.mipmap.royaum_maroc3);
            mainActivity.changeIconinfo(R.mipmap.logo_dra3);
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        Activity_main mainActivity = (Activity_main) getActivity();
        if (mainActivity != null) {
            mainActivity.changeIconmenu(R.drawable.ic_menu);
            mainActivity.changeIconinfo(R.drawable.outline_info_24);
        }

    }

}