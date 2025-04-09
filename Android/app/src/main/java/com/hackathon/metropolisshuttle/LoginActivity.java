package com.hackathon.metropolisshuttle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.metropolisshuttle.model.LoginRequest;
import com.hackathon.metropolisshuttle.model.LoginResponse;
import com.hackathon.metropolisshuttle.network.APIService;
import com.hackathon.metropolisshuttle.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String token = Utils.getString(this, "Token");
        if (token != null && !token.trim().isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
            //return;
        }

        progressDialog = new ProgressDialog(this);

        // Get the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Set the action bar background color programmatically
            actionBar.hide();
            //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        }

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        Button buttonSubmit = findViewById(R.id.login_button);

        buttonSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isEmailValid(username)) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API if fields are valid
        callLoginApi(username, password);

    }

    private void callLoginApi(String username, String password) {
        Utils.showProgressDialog(progressDialog);
        // Retrofit setup
        APIService apiService = RetrofitClient.getClient(this).create(APIService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Utils.hideProgressDialog(progressDialog);
                    // Handle the response
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && !loginResponse.getToken().trim().isEmpty()) {
                        Utils.saveString(LoginActivity.this, loginResponse.getToken(), "Token");
                        Utils.saveString(LoginActivity.this, loginResponse.getRole(), "Role");
                        Utils.saveString(LoginActivity.this, loginResponse.getRouteId(), "Route");
                        Utils.saveString(LoginActivity.this, loginResponse.getRouteColor(), "Colour");
                        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Utils.hideProgressDialog(progressDialog);
                        Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.hideProgressDialog(progressDialog);
                    Log.d("TAG", "onResponse: " + response.message());
                    Toast.makeText(LoginActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Utils.hideProgressDialog(progressDialog);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideProgressDialog(progressDialog);
    }
}