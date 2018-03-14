package ie.dalydev.dogbreeding;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
This class is to add dogs to the SQL Database. It also Validates that no duplicate Chips are entered
and displays this to the user
 */
public class AddActivity extends AppCompatActivity {

    //global variable to reference xml files
    private TextView genderLabel;
    private RadioGroup genderSelection;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    private TextView nameLabel;
    private EditText enterName;
    private String customPicPath;
    private Boolean customPic;

    private TextView DoBLabel;
    private DatePicker DateofBirth;

    private TextView chipNum;
    private EditText enterChip;
    private static final int CAMERA_REQUEST = 1000;

    //onCreate is to setup the screen as displayed to the user, references xml file
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        genderLabel = (TextView) findViewById(R.id.genderLabel);
        genderSelection = (RadioGroup) findViewById(R.id.genderSelection);
        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);

        nameLabel = (TextView) findViewById(R.id.nameLabel);
        enterName = (EditText) findViewById(R.id.enterName);

        DoBLabel = (TextView) findViewById(R.id.DoBLabel);
        DateofBirth = (DatePicker) findViewById(R.id.datePicker);

        chipNum = (TextView) findViewById(R.id.chipNum);
        enterChip = (EditText) findViewById(R.id.enterChip);
        DateofBirth.setMaxDate(System.currentTimeMillis());

        Button photoButton = (Button) this.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }


    //when all details have been entered, add dog pressed
    //takes inputs and validates that there are values in every field
    public void SaveDogButtonPressed(View view) {

        EditText chipText = (EditText) findViewById(R.id.enterChip);
        final String chip = chipText.getText().toString();
        EditText nameText = (EditText) findViewById(R.id.enterName);
        final String name = nameText.getText().toString();

        final int year = DateofBirth.getYear();
        final int month = DateofBirth.getMonth() + 1;
        final int day = DateofBirth.getDayOfMonth();

        final String gender;

        if (radioButtonMale.isChecked()) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        if(customPicPath==null){
            customPic=false;
            customPicPath="";
        }else{
            customPic = true;
        }

        //Validation
        if (nameText.getText().toString().equals("")) {
            nameText.setError("Name cannot be empty!");
            nameText.isFocused();
        } else if (chipText.length() < 15) {
            chipText.setError("Chip Number must be 15 digits long!");
            chipText.isFocused();
        }

        // all validation clear, create a confirm dialogue popup
        else if (!nameText.getText().toString().equals("") && chipText.length() == 15) {


            new AlertDialog.Builder(this)
                    .setTitle("Confirm Details!")
                    .setMessage(genderLabel.getText().toString() + ": " + gender + "\n" +
                            nameLabel.getText() + ": " + name + "\n" +
                            DoBLabel.getText() + ": " + day + "/" + month + "/" + year + "\n" +
                            chipNum.getText() + ": " + chip + "\n"+
                             "Custom Photo :" + customPic)

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            confirmAdd(chip, name, gender, day, month, year, customPicPath);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();

        }


    }

    //Once ok pushed on confimation box this then validated that a duplicate chip has not been entered
    public void confirmAdd(String chip, String name, String gender, int day, int month, int year, String customPicPath) {

        DogDB db = new DogDB(this, null, null, 1);

        //validate duplicate name
        //for some reason this was not working
        //db was searching for a column name of what the user enetered


        if (db.checkChip(chip))   //validate chip works...code very similar to checkName
        {
            Toast.makeText(this, "Chip already registered!", Toast.LENGTH_LONG).show();
        } else {
            //if all validation passes, call to db and add the row
            int dogId = db.countDogs() + 1;
            for (int i = 0; i < db.countDogs(); i++) {
                if (db.getDogs().get(i).getDogId() == dogId) {
                    dogId = dogId + 1;
                }
            }

            Dog dog = new Dog(dogId, chip, name, gender, day, month, year, customPicPath);

            db.addDog(dog);
            db.close();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(AddActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }, 0);

            Toast.makeText(AddActivity.this, name + " has been added!", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
                createImageFile(imageBitmap);
            }
        }




    public void createImageFile(Bitmap imageBitmap) {
        try {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
            File file = new File(dir, timeStamp + ".png");
        FileOutputStream fOut  = new FileOutputStream(file);

        customPicPath = file.getPath();

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
        } catch (FileNotFoundException e) {
        e.printStackTrace();
        }catch (IOException i) {
            i.printStackTrace();
        }
    }

}