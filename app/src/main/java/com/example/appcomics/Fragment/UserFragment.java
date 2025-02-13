package com.example.appcomics.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appcomics.Controller.LoginActivity;
import com.example.appcomics.R;

public class UserFragment extends Fragment {
    private String username;
    private EditText edituser;
    private TextView info;
    private TextView logout;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);


        edituser = view.findViewById(R.id.username);
        info = view.findViewById(R.id.info);
        logout = view.findViewById(R.id.logout);


        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
            edituser.setText(username); // Set username in EditText
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logoutUser() {

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
         getActivity().finish(); // Finish the current activity to remove it from the back stack
    }
}
