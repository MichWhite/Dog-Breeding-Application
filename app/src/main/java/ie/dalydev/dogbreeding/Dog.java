package ie.dalydev.dogbreeding;

/**
 * This is where each Dog object gets created. Details can then be passed to db
 */
public class Dog {
    private int dogId;
    private String chip;
    private String name;
    private String gender;
    private int day;
    private int month;
    private int year;
    private String photoPath;

    public Dog(int dogId, String chip, String name, String gender, int day, int month, int year,String photoPath) {
        this.dogId = dogId;
        this.chip = chip;
        this.name = name;
        this.gender = gender;
        this.day = day;
        this.month = month;
        this.year = year;
        this.photoPath = photoPath;
    }

    public Dog() {
        dogId = 0;
        chip = "";
        name = "";
        gender="";
        day = 0;
        month = 0;
        year = 0;
        photoPath= "";
    }

    public String getPhotoPath() {return photoPath; }
    public void setPhotoPath(String photoPath) {this.photoPath = photoPath;}

    public int getDogId() {return dogId; }
    public void setDogId(int dogId) {this.dogId = dogId;}

    public int getDogDay() {return day; }
    public void setDogDay(int day) {this.day = day;}

    public int getDogMonth() {return month; }
    public void setDogMonth(int month) {this.month = month;}

    public int getDogYear() {return year; }
    public void setDogYear(int year) {this.year = year;}


    public String getChip() {return chip; }
    public void setChip(String chip) {this.chip = chip; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGender(){
        return gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }

}