package ie.dalydev.dogbreeding;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * This calss is the main display of the App
 * Its current interactivity is to link to other screens
 * to add or display dogs stored in and SQLite db
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

   public void goAddScreen(View view){
        Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(addIntent);
    }

    public void goDisplayScreen(View view){
        Intent addIntent = new Intent(MainActivity.this, DisplayActivity.class);
        startActivity(addIntent);
    }
}

