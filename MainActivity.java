package com.genora.prospectmanagement.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genora.prospectmanagement.R;
import com.genora.prospectmanagement.asynctask.FetchDataAsyncTask;
import com.genora.prospectmanagement.database.ProspectDatabase;
import com.genora.prospectmanagement.helper.Utils;
import com.genora.prospectmanagement.interfaces.FetchDataResponseListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;


    public static final int CAMERA_SELFIE_REQUEST_CODE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Prospect Management";

    public String[] arraySpinnerEvent = new String[]{"Select Event", "Event 1", "Event 2", "Event 3", "Event 4", "Event 5"};
    public String[] arraySpinnerEmail = new String[]{"Select Email Template", "Email 1", "Email 2", "Email 3", "Email 4", "Email 5"};
    public String[] arraySpinnerSms = new String[]{"Select SMS Template", "SMS 1", "SMS 2", "SMS 3", "SMS 4", "SMS 5"};

    private Utils utils;

    private Spinner spinnerEvent;
    private Spinner spinnerEmailTemplate;
    private Spinner spinnerSmsTemplate;

    private ImageView imageViewSelfie;

    private Uri imageUri;

    private EditText editTextName;
    private EditText editTextCompanyName;
    private EditText editTextEmail;
    private EditText editTextWebsite;
    private EditText editTextPhoneCode;
    private EditText editTextPhone;
    private EditText editTextComment;

    private Button buttonSubmit;

    private CheckBox checkBoxInterestedOpt1;
    private CheckBox checkBoxInterestedOpt2;
    private CheckBox checkBoxInterestedOpt3;
    ProspectDatabase prospectDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        context = this;
        prospectDatabase = new ProspectDatabase(context);

        new FetchDataAsyncTask(context, fetchDataResponseListner , prospectDatabase).execute();

        utils = new Utils();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextCompanyName = (EditText) findViewById(R.id.editTextCompanyName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextWebsite = (EditText) findViewById(R.id.editTextWebsite);
        editTextPhoneCode = (EditText) findViewById(R.id.editTextPhoneCode);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextComment = (EditText) findViewById(R.id.editTextComment);

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.logo);
        getSupportActionBar().setTitle(R.string.title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#1E7F8E")));

        imageViewSelfie = (ImageView) findViewById(R.id.imageViewSelfie);

        imageViewSelfie.setOnClickListener(this);


        checkBoxInterestedOpt1 = (CheckBox) findViewById(R.id.checkBoxInterestedOpt1);
        checkBoxInterestedOpt2 = (CheckBox) findViewById(R.id.checkBoxInterestedOpt2);
        checkBoxInterestedOpt3 = (CheckBox) findViewById(R.id.checkBoxInterestedOpt3);

        spinnerEvent = (Spinner) findViewById(R.id.spinnerEvent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerEvent);
        spinnerEvent.setAdapter(adapter);


        spinnerEmailTemplate = (Spinner) findViewById(R.id.spinnerEmailTemplate);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerEmail);
        spinnerEmailTemplate.setAdapter(adapter);

        spinnerSmsTemplate = (Spinner) findViewById(R.id.spinnerSmsTemplate);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerSms);
        spinnerSmsTemplate.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_SELFIE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getString(R.string.captureCancelMessage), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getString(R.string.captureFailedMessage), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 15;

            final Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),
                    options);

            imageViewSelfie.setImageBitmap(bitmap);
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSubmit:

                // Validations
                if (utils.isEmptyEditText(editTextName, getString(R.string.editTextNameError)) &&
                        utils.isEmptyEditText(editTextCompanyName, getString(R.string.editTextCompanyNameError)) &&
                        utils.isEmptyEditText(editTextEmail, getString(R.string.editTextEmailError)) &&
                        utils.isValidEmail(editTextEmail, getString(R.string.editTextInvalidEmailError)) &&
                        utils.isEmptyEditText(editTextWebsite, getString(R.string.editTextWebsiteError)) &&
                        utils.isValidWebAddress(editTextWebsite, getString(R.string.editTextInvalidWebsiteError)) &&
                        utils.isEmptyEditText(editTextPhoneCode, getString(R.string.editTextPhoneCodeError)) &&
                        utils.isEmptyEditText(editTextPhone, getString(R.string.editTextPhoneError)) &&
                        utils.isValidPhoneNumber(editTextPhone, getString(R.string.editTextInvalidPhoneError)) &&
                        utils.isEmptyEditText(editTextComment, getString(R.string.editTextCommentError)) && (
                        utils.isCheckBoxChecked(checkBoxInterestedOpt1, (TextView) findViewById(R.id.textViewInterestedInError), getString(R.string.checkBoxInterestedError)) ||
                                utils.isCheckBoxChecked(checkBoxInterestedOpt2, (TextView) findViewById(R.id.textViewInterestedInError), getString(R.string.checkBoxInterestedError)) ||
                                utils.isCheckBoxChecked(checkBoxInterestedOpt3, (TextView) findViewById(R.id.textViewInterestedInError), getString(R.string.checkBoxInterestedError))) &&
                        utils.isSpinnerItemSelected(spinnerEmailTemplate, (TextView) findViewById(R.id.textViewSpinnerEmailTemplateError), getString(R.string.spinnerEmailTemplateError)) &&
                        utils.isSpinnerItemSelected(spinnerSmsTemplate, (TextView) findViewById(R.id.textViewSpinnerSmsTemplateError), getString(R.string.spinnerSmsTemplateError)) &&
                        utils.isSpinnerItemSelected(spinnerEvent, (TextView) findViewById(R.id.textViewSpinnerEventError), getString(R.string.spinnerEventError))
                        ) {
                    Toast.makeText(getApplicationContext(), getString(R.string.successMessage), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.imageViewSelfie:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = utils.getOutputMediaFileUri(IMAGE_DIRECTORY_NAME);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(intent, CAMERA_SELFIE_REQUEST_CODE);
                break;
        }
    }

    FetchDataResponseListener fetchDataResponseListner = new FetchDataResponseListener() {
        @Override
        public String onSucess(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        public String onError(String msg) {

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return null;
        }
    };
}