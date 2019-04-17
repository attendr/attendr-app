package anudit.attendr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import anudit.attendr.dbController;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String DB = "ATTENDR";
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewDialog = new ViewDialog(this);

        sharedpreferences = getSharedPreferences(DB, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("username", null);
        if(name != null)
            openMenu();
    }


    public void login(View v){
        EditText uname = (EditText)findViewById(R.id.login_tb_uname);
        EditText pass = (EditText)findViewById(R.id.login_tb_pass);
        String inpUname =  uname.getText().toString();
        String inpPass =  pass.getText().toString();

        viewDialog.showDialog();
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.post("https://theattendrapp.herokuapp.com/student-login?username="+inpUname+"&password="+inpPass)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ATTENDR", response.toString());
                        String resp = "error";
                        String autht= null;
                        String no_of_courses = null;
                        String average_attendance = null;
                        try {
                            resp = response.get("success").toString();
                            autht = response.get("auth_token").toString();
                            no_of_courses = response.get("no_of_courses").toString();
                            average_attendance = response.get("average_attendance").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("auth_token", autht);
                        editor.putString("no_of_courses", no_of_courses);
                        editor.putString("average_attendance", average_attendance);

                        editor.apply();

                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d("ATTENDR", error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        viewDialog.hideDialog();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", inpUname);
        editor.putString("password", inpPass);
        editor.apply();




        openMenu();
    }

    public void openMenu(){
        startActivity(new Intent(this, Menu.class));
    }
}
