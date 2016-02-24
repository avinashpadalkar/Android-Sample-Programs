package com.genora.prospectmanagement.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.genora.prospectmanagement.database.ProspectDatabase;
import com.genora.prospectmanagement.interfaces.LoginResponseListner;
import com.genora.prospectmanagement.model.LoginModel;
import com.genora.prospectmanagement.myUtils.MyMessage;
import com.genora.prospectmanagement.myUtils.MyUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by comnet on 08-Feb-16.
 */
public class LoginAsyncTask extends AsyncTask<String, String, String> implements LoginResponseListner {

    Context context;
    LoginModel loginModel;
    ProgressDialog progressDialog;
    LoginResponseListner loginResponseListner;
    ProspectDatabase prospectDatabase;

    public LoginAsyncTask(Context context, LoginModel loginModel, LoginResponseListner loginResponseListner, ProspectDatabase prospectDatabase) {
        this.context = context;
        this.loginModel = loginModel;
        this.loginResponseListner = loginResponseListner;
        this.prospectDatabase = prospectDatabase;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setTitle("Sign In");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String result = post();

        return result;
    }

    private String post() {

        InputStream inputStream = null;
        String result = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(MyUtils.url_login);

        List<NameValuePair> list_namevaluepair = new ArrayList<NameValuePair>();
        list_namevaluepair.add(new BasicNameValuePair("email",
                loginModel.getEmail()));
        list_namevaluepair.add(new BasicNameValuePair("password",
                loginModel.getPassword()));
        StringBuilder builder = new StringBuilder();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list_namevaluepair));

            HttpResponse response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d("RestClient", "Status Code : " + statusCode);

            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = builder.toString();

        return result;

    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (loginParser(result)) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            if (progressDialog.isIndeterminate()) {
                progressDialog.dismiss();
            }
        }

    }

    private boolean loginParser(String result) {

        JSONObject jsonObjectResult = null;
        JSONObject jsonObjectResponse = null;
        JSONObject jsonObjectLoginDetails = null;
        int status = 0;
        String response_msg = "";

        if (result != null) {

            try {
                jsonObjectResult = new JSONObject(result);

                jsonObjectResponse = jsonObjectResult.getJSONObject(MyUtils.TAG_RESPONSE);
                status = Integer.parseInt(jsonObjectResponse.get("status").toString());
                response_msg = jsonObjectResponse.get("error").toString();

                if (status == 1) {

                    jsonObjectLoginDetails = jsonObjectResponse.getJSONObject("login_details");


                    if (jsonObjectLoginDetails.length() > 0) {

                        int id = jsonObjectLoginDetails.getInt("id");
                        String name = jsonObjectLoginDetails.getString("name");
                        String designation = jsonObjectLoginDetails.getString("designation");

                        if (prospectDatabase.insertLoginDetails(id, name, designation)) {
                            MyMessage.myToast(context, "ROW Inserteed");
                        }else{
                            MyMessage.myToast(context, "ROW NOT Inserteed");
                        }

                        loginResponseListner.onSucess(response_msg);

                    }


                    return true;

                } else if (status == 0) {
                    loginResponseListner.onError(response_msg);
                    return false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {

            Toast.makeText(context, "Result is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;

    }


    @Override
    public String onSucess(String msg) {
        return null;
    }

    @Override
    public String onError(String msg) {
        return null;
    }
}
