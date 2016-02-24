package com.genora.prospectmanagement.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genora.prospectmanagement.R;
import com.genora.prospectmanagement.database.ProspectDatabase;
import com.genora.prospectmanagement.helper.Utils;
import com.genora.prospectmanagement.asynctask.LoginAsyncTask;
import com.genora.prospectmanagement.interfaces.LoginResponseListner;
import com.genora.prospectmanagement.model.LoginModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Utils utils;
    Context context;
    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;

    private Button buttonSignIn;
    LoginModel loginModel;
    String email, password;

    ProspectDatabase prospectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        prospectDatabase = new ProspectDatabase(context);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setIcon(R.mipmap.logo);
        getSupportActionBar().setTitle(R.string.title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#1E7F8E")));

        utils = new Utils();


        editTextLoginEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignIn:
                if (utils.isEmptyEditText(editTextLoginEmail, getString(R.string.editTextEmailError)) &&
                        utils.isValidEmail(editTextLoginEmail, getString(R.string.editTextInvalidEmailError)) &&
                        utils.isEmptyEditText(editTextLoginPassword, getString(R.string.editTextPasswordError))) {

//                    utils.clearEditText(editTextLoginEmail);
//                    utils.clearEditText(editTextLoginPassword);

                    email = editTextLoginEmail.getText().toString().trim();
                    password = editTextLoginPassword.getText().toString().trim();

                    loginModel = new LoginModel();

                    loginModel.setEmail(email);
                    loginModel.setPassword(password);

                    new LoginAsyncTask(context, loginModel, loginResponseListner, prospectDatabase).execute();


//

                }
                break;
        }
    }

    LoginResponseListner loginResponseListner = new LoginResponseListner() {
        @Override
        public String onSucess(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            return null;
        }

        @Override
        public String onError(String msg) {

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return null;

        }

    };
}