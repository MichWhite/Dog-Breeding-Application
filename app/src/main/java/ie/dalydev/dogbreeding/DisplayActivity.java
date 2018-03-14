package ie.dalydev.dogbreeding;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Typeface;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ExpandableListView;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;

/*
This class is to display the list of all dogs currently in the database
It also allows the user the option to delete all dogs from the db
A
 */
public class DisplayActivity extends AppCompatActivity {

    private ExpandableListView dogList;
private DogDB dogDB;
private String customPicPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dogList = (ExpandableListView) findViewById(R.id.showDog);
        dogList.setLongClickable(true);


        dogDB = new DogDB(this, null, null, 1);

        if (dogDB.getDogs() == null) {
            Toast.makeText(DisplayActivity.this, "No dogs in list", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));

        } else {

            final ArrayList<Integer> dogHeader = new ArrayList<>();
            for (int i = 0; i < dogDB.getDogs().size(); i++) {
                int dogId = dogDB.getDogs().get(i).getDogId();
                dogHeader.add(dogId);
            }
            final HashMap<Integer, Dog> dog_child = new HashMap<>();

            for (int r = 0; r < dogDB.getDogs().size(); r++) {
                int dogaddId = dogDB.getDogs().get(r).getDogId();
                Dog dogAdd = dogDB.getDogs().get(r);
                dog_child.put(dogaddId, dogAdd);
            }

            dogList.setAdapter(new ExpandableListAdapter(this, dogHeader, dog_child));


            dogList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final Dog deleteDog = dogDB.getDogs().get(position);

                    new AlertDialog.Builder(DisplayActivity.this)
                            .setTitle("Delete!")
                            .setMessage("Do you really want to delete dog "+deleteDog.getName()+" with the Id "+ deleteDog.getDogId()+"?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(DisplayActivity.this, "Dog "+deleteDog.getName() +" with the Id "+ deleteDog.getDogId()+" Deleted", Toast.LENGTH_LONG).show();
                                    dogDB.deleteDog(deleteDog.getChip());

                                    startActivity(new Intent(DisplayActivity.this, DisplayActivity.class));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();



                    return false;
                };
            });
        }
    }

    //When Delete All button is pushed, a confirmation message pops up
    public void deleteAllDogs(View view){

        new AlertDialog.Builder(this)
                .setTitle("Delete!")
                .setMessage("Do you really want to delete all dogs?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        confirmDogDel();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    private static final int CAMERA_REQUEST = 1000;
private  ImageView dogImageView;
    public void updateDog(View view){
        Toast.makeText(DisplayActivity.this, "can not change chip number!", Toast.LENGTH_LONG).show();


        int position = dogList.getPositionForView((View) view.getParent());

        setContentView(R.layout.content_add);

        final Dog updateDog= dogDB.getDogs().get(position-1);
        Button SaveDogButtonPressed = (Button) findViewById(R.id.saveButton);
        dogImageView = (ImageView)findViewById(R.id.imageView);

        SaveDogButtonPressed.setText("Update Dog");

        if(!updateDog.getPhotoPath().equals("")) {

            try {
                File file = new File(updateDog.getPhotoPath());
                FileInputStream streamIn = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(streamIn);
                dogImageView.setImageBitmap(bitmap);
                streamIn.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException h) {
                h.printStackTrace();
            }
        }
        RadioGroup radioGender = (RadioGroup) findViewById(R.id.genderSelection);
        final RadioButton radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        if(updateDog.getGender().equals("Male")){
            radioGender.check(R.id.radioButtonMale);
        }
        else{
            radioGender.check(R.id.radioButtonFemale);
        }
        customPicPath = updateDog.getPhotoPath();
        Button photoButton = (Button) this.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        final EditText chipText = (EditText) findViewById(R.id.enterChip);
        chipText.setText(updateDog.getChip());

        chipText.setFocusable(false);
        chipText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        chipText.setClickable(false);

        final EditText nameText = (EditText) findViewById(R.id.enterName);
        nameText.setText(updateDog.getName());

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.updateDate(updateDog.getDogYear(), updateDog.getDogMonth(), updateDog.getDogDay());



        //Validation
        if (nameText.getText().toString().equals("")) {
            nameText.setError("Name cannot be empty!");
            nameText.isFocused();
        }



        // all validation clear, create a confirm dialogue popup
        else if(!nameText.getText().toString().equals("") && chipText.length()==15) {

            SaveDogButtonPressed.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Boolean customPic;
                    final String Doggender;
                    if (radioButtonMale.isChecked()){
                        Doggender = "Male";}
                    else {
                        Doggender ="Female";
                    }
                    if(customPicPath.equals("")){
                        customPic=false;
                    }else{

                        customPic = true;
                    }
                    final String name = nameText.getText().toString();
                    final String chip = chipText.getText().toString();

                    final int year = datePicker.getYear();
                    final int month = datePicker.getMonth()+1;
                    final int day = datePicker.getDayOfMonth();

                    new AlertDialog.Builder(DisplayActivity.this)
                            .setTitle("Confirm Details!")
                            .setMessage("Gender : " + Doggender + "\n" +
                                    "Naem : " + name + "\n" +
                                    "Date of Birth: " + day + "/" + month + "/" + year + "\n" +
                                    "Chip Number: " + chip+"\n"+
                                "Custom Photo: " +customPic)

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                        //if all validation passes, call to db and add the row

                                        final int dogId =  updateDog.getDogId();

                                    Dog dog = new Dog( dogId, chip, name, Doggender, day, month, year, customPicPath);
                                        if(dogDB.update(dog)){
                                            Toast.makeText(DisplayActivity.this, name + " has been Updated!", Toast.LENGTH_LONG).show();

                                        }
                                        dogDB.close();

                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {


                                                startActivity(new Intent(DisplayActivity.this, DisplayActivity.class));
                                                finish();
                                            }
                                        }, 0);



                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                }
            });
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try{
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
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

    //once OK pushed on confirmation dialogue, db is emptied
    public void confirmDogDel(){

        dogDB.deleteAllDogs();

        Intent deleteIntent = new Intent(DisplayActivity.this, MainActivity.class);
        deleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(deleteIntent);
        finish();

    }

}


class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Integer> dog_header; // header
    // child data in format of header title, child title
    private HashMap<Integer, Dog> dog_child;

    public ExpandableListAdapter(Context context, List<Integer> dog_header, HashMap<Integer, Dog> dog_child) {
        this._context = context;
        this.dog_header = dog_header;
        this.dog_child = dog_child;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.dog_child.get(this.dog_header.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dog_header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.dog_header.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View view, ViewGroup parent) {
        int headerTitle = (int) getGroup(groupPosition);


        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.row_header, null);
        }

        TextView lblListHeader = (TextView) view.findViewById(R.id.dogName1);
        TextView lblListId = (TextView) view.findViewById(R.id.dogId1);
        lblListId.setText(Integer.toString((int) headerTitle));

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(this.dog_child.get(headerTitle).getName());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = infalInflater.inflate(R.layout.row_dog, parent, false);


        int headerTitle = (int) getGroup(groupPosition);

        TextView dogAgeView = (TextView) view.findViewById(R.id.dogAge);
        TextView dogGenderView = (TextView) view.findViewById(R.id.dogGender);
        TextView dogChipView = (TextView) view.findViewById(R.id.dogChip);
        ImageView dogImageView = (ImageView) view.findViewById(R.id.dogImage);

        dogAgeView.setText(this.dog_child.get(headerTitle).getDogDay() + "/" + this.dog_child.get(headerTitle).getDogMonth() + "/" + this.dog_child.get(headerTitle).getDogYear());
        dogGenderView.setText(this.dog_child.get(headerTitle).getGender());
        dogChipView.setText(this.dog_child.get(headerTitle).getChip());

        if (!this.dog_child.get(headerTitle).getPhotoPath().equals("") || this.dog_child.get(headerTitle).getPhotoPath().length() < 1) {

            try {
                File file = new File(this.dog_child.get(headerTitle).getPhotoPath());

                FileInputStream streamIn = new FileInputStream(file);

                Bitmap bitmap = BitmapFactory.decodeStream(streamIn);
                dogImageView.setImageBitmap(bitmap);

                streamIn.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException h) {
                h.printStackTrace();
            }
        }


        return view;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }




    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
