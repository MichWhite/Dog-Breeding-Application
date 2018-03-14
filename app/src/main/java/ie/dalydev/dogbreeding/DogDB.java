package ie.dalydev.dogbreeding;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class creates and manages the SQLite db
 */
public class DogDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dogDB.db";
    private static final String TABLE_DOG = "dog";

    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_CHIP = "chip";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_ID = "dogId";
    private static final String COLUMN_PATH = "photoPath";

    public DogDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // onCreate sets the table name and column name and types
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_DOG_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_DOG + "("
                + COLUMN_ID + " INT PRIMARY KEY, "
                + COLUMN_CHIP + " TEXT, "
                + COLUMN_NAME +" TEXT, "
                + COLUMN_GENDER + " TEXT, " +
                COLUMN_DAY + " INT, " +
                COLUMN_MONTH + " INT, "  +
                COLUMN_YEAR + " INT, " +
                COLUMN_PATH + " TEXT);";

        db.execSQL(CREATE_DOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean update(Dog updateDog){
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(COLUMN_ID, updateDog.getDogId());
             args.put(COLUMN_CHIP, updateDog.getChip());
            args.put(COLUMN_NAME, updateDog.getName());
            args.put(COLUMN_GENDER, updateDog.getGender());
            args.put(COLUMN_DAY, updateDog.getDogDay());
            args.put(COLUMN_MONTH, updateDog.getDogMonth());
            args.put(COLUMN_YEAR, updateDog.getDogYear());
             args.put(COLUMN_PATH, updateDog.getPhotoPath());

        return db.update(TABLE_DOG, args, COLUMN_CHIP + " = " + updateDog.getChip(), null) > 0;


    }


    //adds the details from Dog.java, as entered in AddActivity.java
    public void addDog(Dog dog){

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, dog.getDogId());
        values.put(COLUMN_CHIP, dog.getChip());
        values.put(COLUMN_NAME, dog.getName());
        values.put(COLUMN_GENDER, dog.getGender());
        values.put(COLUMN_DAY, dog.getDogDay());
        values.put(COLUMN_MONTH, dog.getDogMonth());
        values.put(COLUMN_YEAR, dog.getDogYear());
        values.put(COLUMN_PATH, dog.getPhotoPath());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_DOG, null, values);
        db.close();
    }

    //this is used to display the dogs when the method is called
    public ArrayList<Dog> getDogs(){

        String query = "Select * FROM " + TABLE_DOG + " Order by + " + COLUMN_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Dog> dogs = new ArrayList<Dog>();

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                Dog dog = new Dog();
                dog.setDogId(Integer.parseInt(cursor.getString(0)));
                dog.setChip(cursor.getString(1));
                dog.setName(cursor.getString(2));
                dog.setGender(cursor.getString(3));
                dog.setDogDay(Integer.parseInt(cursor.getString(4)));
                dog.setDogMonth(Integer.parseInt(cursor.getString(5)));
                dog.setDogYear(Integer.parseInt(cursor.getString(6)));
                dog.setPhotoPath(cursor.getString(7));

                dogs.add(dog);
                cursor.moveToNext();
            }
        }else {
            dogs = null;
        }
        db.close();
        return dogs;
    }


public int countDogs(){
    ArrayList<Dog> dogs = null;
    dogs = getDogs();
    int countDogs;
if(dogs == null || dogs.isEmpty()){
    countDogs = 0;
}
    else {
    countDogs = dogs.size();
}
    return countDogs;
    }
    //method to validate the name entered is not a duplicate entry
    //whenever this is called an error is thrown
    //where db is searching for a column name based on name
    public boolean checkName(String name) {

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select " + COLUMN_NAME + " from " + TABLE_DOG + " where " + COLUMN_NAME + " = " + name;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    //validate chip is not a duplicate entry
    //this works, with almost identical code to checkName
    public boolean checkChip(String chip) {


            SQLiteDatabase db = this.getWritableDatabase();
            String Query = "Select " + COLUMN_CHIP + " from " + TABLE_DOG + " where " + COLUMN_CHIP + " = " + chip;
            Cursor cursor = db.rawQuery(Query, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }

    //method to delete all records in the db
    public void deleteAllDogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOG, null, null);
        db.close();
    }

    public Boolean deleteDog(String chip) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DOG, COLUMN_CHIP + "=" + chip, null) > 0;
    }


  }
