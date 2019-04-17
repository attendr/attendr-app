package anudit.attendr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Menu extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String DB = "ATTENDR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        sharedpreferences = getSharedPreferences(DB, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("username", null);
        String atperval = sharedpreferences.getString("average_attendance", "0");
        String coursesval = sharedpreferences.getString("no_of_courses", "0");
        if(name == null)
            openLogin();
        else{
            TextView uname = (TextView) findViewById(R.id.menu_tv_uname);
            uname.setText(name);
            TextView atper = (TextView) findViewById(R.id.menu_c1_per);
            atper.setText(atperval+"%");
            TextView tvcourses = (TextView) findViewById(R.id.menu_tv_courses);
            tvcourses.setText(coursesval+" Courses");
        }

    }
    @Override
    protected void onResume(){
        updateLocal();
        super.onResume();
    }

    public void openScanner(View v){ startActivity(new Intent(this, Scanner.class)); }
    public void openAttendance(View v){ startActivity(new Intent(this, Attendance.class)); }

    public void logout(View v){
        getApplicationContext().getSharedPreferences(DB, 0).edit().clear().commit();
        openLogin();
    }

    public void update(View v){
        updateLocal();
    }

    public void updateLocal(){
        updateGlobal();
        sharedpreferences = getSharedPreferences(DB, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("username", null);
        String atperval = sharedpreferences.getString("average_attendance", "0");
        String coursesval = sharedpreferences.getString("no_of_courses", "0");
        if(name == null)
            openLogin();
        else{
            TextView uname = (TextView) findViewById(R.id.menu_tv_uname);
            uname.setText(name);
            TextView atper = (TextView) findViewById(R.id.menu_c1_per);
            atper.setText(atperval+"%");
            TextView tvcourses = (TextView) findViewById(R.id.menu_tv_courses);
            tvcourses.setText(coursesval+" Courses");
        }
    }

    public void updateGlobal(){
        sharedpreferences = getSharedPreferences(DB, Context.MODE_PRIVATE);

        String inpUname =  sharedpreferences.getString("username", "");
        String inpPass =  sharedpreferences.getString("password", "");

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
//                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", inpUname);
        editor.putString("password", inpPass);
        editor.apply();
    }

    public void openLogin(){
        startActivity(new Intent(this, MainActivity.class));
    }

}
