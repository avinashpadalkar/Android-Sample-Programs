package com.genora.prospectmanagement.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.widget.Toast;

import com.genora.prospectmanagement.database.ProspectDatabase;
import com.genora.prospectmanagement.interfaces.FetchDataResponseListener;
import com.genora.prospectmanagement.model.EventModel;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by comnet on 09-Feb-16.
 */
public class FetchDataAsyncTask extends AsyncTask<String, String, String> {
    Context context;
    FetchDataResponseListener fetchDataResponseListner;
    ProgressDialog progressDialog;
    ProspectDatabase prospectDatabase;

    public FetchDataAsyncTask(Context context, FetchDataResponseListener fetchDataResponseListner, ProspectDatabase prospectDatabase) {

        this.context = context;
        this.fetchDataResponseListner = fetchDataResponseListner;
        this.prospectDatabase = prospectDatabase;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Loading Data");
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
        HttpPost httpPost = new HttpPost(MyUtils.url_Fetch_Data);

        List<NameValuePair> list_namevaluepair = new ArrayList<NameValuePair>();
        list_namevaluepair.add(new BasicNameValuePair("company_id",
                MyUtils.company_id));
        list_namevaluepair.add(new BasicNameValuePair("timestamp",
                MyUtils.timestamp));
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

        fetchDataParser(result);
        progressDialog.dismiss();
    }

    private void fetchDataParser(String result) {

        JSONObject jsonObject_Result = null;
        JSONObject jsonObject_Response = null;
        JSONArray jsonArray_Event = null;
        JSONArray jsonArray_Services = null;

        int status = 0;
        String response_msg = "";
        List<EventModel> eventModelList = null;
        if (result != null) {

            try {
                jsonObject_Result = new JSONObject(result);

                jsonObject_Response = jsonObject_Result.getJSONObject(MyUtils.TAG_RESPONSE);

                status = Integer.parseInt(jsonObject_Response.get("status").toString());
                response_msg = jsonObject_Response.get("error").toString();

                if (status == 1) {

                    fetchDataResponseListner.onSucess(response_msg);


//                    parse EVENT
                    jsonArray_Event = jsonObject_Response.getJSONArray(MyUtils.TAG_EVENT);
                    JSONObject jsonObject_Event = null;
                    int id = 0;
                    String name = null;
                    eventModelList = new ArrayList<EventModel>();
                    for (int i = 0; i < jsonArray_Event.length(); i++) {

                        jsonObject_Event = jsonArray_Event.getJSONObject(i);

                        id = jsonObject_Event.getInt("id");
                        name = jsonObject_Event.getString("name");

                        EventModel eventModel = new EventModel();
                        eventModel.setId(id);
                        eventModel.setName(name);

                        eventModelList.add(eventModel);

                    }

                    prospectDatabase.insertEventdetails(eventModelList);

                    jsonArray_Services = jsonObject_Response.getJSONArray(MyUtils.TAG_SERVICES);
                    JSONObject jsonObject_Services = null;
                    String services = "";

                    for (int i = 0; i < jsonObject_Services.length(); i++) {

                        services = jsonObject_Services.getString("service");

                    }


                } else if (status == 0) {
                    fetchDataResponseListner.onError(response_msg);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, "Result is NULL", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(context, eventModelList.size(), Toast.LENGTH_SHORT).show();
    }


}
