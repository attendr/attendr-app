package anudit.attendr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class dbController extends AppCompatActivity {

    static SharedPreferences sharedpreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("Attendr", Context.MODE_PRIVATE);
    }

    public static void setUsername(String un){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", un);
        editor.commit();
    }

    public static String getUsername(){
        return sharedpreferences.getString("username", null);
    }

    public static void setPassword(String pass){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("password", pass);
        editor.commit();
    }

    public static String getPassword(){
        return sharedpreferences.getString("password", null);
    }

}
