package com.serviceonwheel.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.listner.UserDetail;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AndroidMultiPartEntity;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.User;
import com.serviceonwheel.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class UpdateProfileActivity extends BaseActivity {

    Activity context;

    EditText etName, etEmail, etMobileNo, etWhatsappMobileNo;

    TextView btnSubmit;

    ImageView imgChangeImage;

    CircularImageView imgUpdateImage;

    File fileImage;

    String resMessage = "", resCode = "";

    boolean checkFile;

    public static final int REQUEST_ID = 2;

    String txtUserName, txtMobileNo, txtEmail, txtDOB, txtWhatsappNumber;

    String userID = "", name = "", email = "", phone = "", image = "";

    TextView tvDob;


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        context = this;

        initComp();

        initToolbar();

        userID = Utils.getUserId(context);

        fileImage = new File(android.os.Environment.getExternalStorageDirectory(), "service_on_wheel.png");

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setData();

        imgChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUserName = etName.getText().toString();
                txtMobileNo = etMobileNo.getText().toString();
                txtEmail = etEmail.getText().toString();
                txtDOB = tvDob.getText().toString();
                txtWhatsappNumber = etWhatsappMobileNo.getText().toString();

                if (txtUserName.isEmpty()) {
                    Utils.showToast(context, "Enter full name");
                } else if (txtWhatsappNumber.length() > 0 && txtWhatsappNumber.length() < 10) {
                    Utils.showToast(context, "Enter valid whatsapp number");
                } else {

                    if (!txtEmail.isEmpty()) {
                        if (!Utils.isValidEmail(txtEmail)) {
                            Utils.showToast(context, "Enter valid email!");
                            return;
                        }
                    }
                    if (global.isNetworkAvailable()) {
                        new ChangeProfile().execute();
                    } else {
                        retryInternet("change_profile");
                    }
                }

            }
        });

        tvDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(context);
                setDate(tvDob);
            }
        });
    }

    private void setDate(final TextView tvDob) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, 0);
                if (myCalendar.after(c)) {
                    Utils.showToast(context, "Please select another date");
                } else {
                    updateLabel();
                }
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy"; // In which you need put
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tvDob.setText(sdf.format(myCalendar.getTime()));
            }
        };

        DatePickerDialog d = new DatePickerDialog(UpdateProfileActivity.this,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        d.setCancelable(false);
        d.show();
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @SuppressLint("StaticFieldLeak")
    private class ChangeProfile extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_Change_Profile;
            try {
                String restUrl = strAPI.replaceAll(" ", "%20");
                String responseString;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(restUrl);
                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {
                                @Override
                                public void transferred(long num) {
                                }
                            });
                    String pathImage = fileImage.getAbsolutePath();

                    File sourceFile;

                    entity.addPart("userID", new StringBody(userID));
                    entity.addPart("user_phone", new StringBody(txtMobileNo));
                    entity.addPart("user_name", new StringBody(txtUserName));
                    entity.addPart("user_email", new StringBody(txtEmail));
                    entity.addPart("user_bdate", new StringBody(txtDOB));
                    entity.addPart("user_whatsapp_no", new StringBody(txtWhatsappNumber));
                    entity.addPart("app_type", new StringBody("Android"));

                    if (checkFile) {
                        sourceFile = new File(pathImage);
                        entity.addPart("userphoto", new FileBody(sourceFile));
                    }
                    httppost.setEntity(entity);

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(r_entity);
                        JSONObject jsonObject = new JSONObject(responseString);
                        resMessage = jsonObject.getString("message");
                        resCode = jsonObject.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("detail");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        userID = jsonObjectList.getString("userID");
                                        name = jsonObjectList.getString("name");
                                        phone = jsonObjectList.getString("phone");
                                        email = jsonObjectList.getString("email");
                                        image = jsonObjectList.getString("image");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
                AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                onBackPressed();
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
        if (addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_ID);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return !shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    insertDummyContactWrapper();
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
            break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
                    startActivityForResult(intent, 1);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap bitmap;

                    bitmap = Global.getSampleBitmapFromFile(fileImage.getAbsolutePath(), 500, 500);

                    OutputStream outFile;
                    try {
                        outFile = new FileOutputStream(fileImage);
                        assert bitmap != null;
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imgUpdateImage.setImageBitmap(bitmap);
                    checkFile = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    assert selectedImage != null;
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    assert c != null;
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = null;
                    BitmapFactory.Options options;

                    try {
                        thumbnail = (BitmapFactory.decodeFile(picturePath));
                    } catch (OutOfMemoryError e) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            thumbnail = BitmapFactory.decodeFile(picturePath, options);
                        } catch (Exception yy) {
                            yy.printStackTrace();
                        }
                    }
                    try {
                        thumbnail = Global.getSampleBitmapFromFile(picturePath, 300, 300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imgUpdateImage.setImageBitmap(thumbnail);
                    checkFile = true;
                    fileImage = new File(picturePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    assert thumbnail != null;
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == NO_NETWORK_REQUEST_CODE) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("change_profile")) {
                    new ChangeProfile().execute();
                }
            }
        }
    }

    private void setData() {
        User user = new User(context);
        user.setOnReceiveListener(new UserDetail() {
            @Override
            public void onReceived() {
                etName.setText(Utils.getUserName(context));
                etEmail.setText(Utils.getEmail(context));
                etMobileNo.setText(Utils.getMobileNo(context));
                etWhatsappMobileNo.setText(Utils.getWhatsappNumber(context));
                tvDob.setText(Utils.getDOB(context));
                try {
                    Glide.with(context)
                            .load(Utils.getUserImage(context))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.user_defult)
                                    .fitCenter())
                            .into(imgUpdateImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        user.getUserInfo();
    }

    private void initComp() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobileNo = findViewById(R.id.etMobileNo);
        etWhatsappMobileNo = findViewById(R.id.etWhatsappMobileNo);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgChangeImage = findViewById(R.id.imgChangeImage);
        imgUpdateImage = findViewById(R.id.imgUpdateimage);
        tvDob = findViewById(R.id.tvDob);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        toolbarTitle.setText("Change Profile");
        ImageView profile = toolbar.findViewById(R.id.imgProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyAccountActivity.class);
                startActivity(i);
            }
        });
        profile.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
