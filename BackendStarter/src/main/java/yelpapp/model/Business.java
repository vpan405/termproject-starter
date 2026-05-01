package yelpapp.model;

public class Business { // is_open???
    private final String b_id;
    private final String name;
    private final String address;
    private final String city;
    private final String state;
    private final String zip;
    private final double latitude;
    private final double longitude;
    private final float rating;
    private final int tip_count;
    private int rank = 0;
    private double distance;

    public Business(String b_id, String name, String address, String city, String state,
                    String zip, double latitude, double longitude, float rating, int tip_count, int rank, double distance) {
        this.b_id = b_id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.tip_count = tip_count;
        this.rank = rank;
        this.distance = distance;
    }
    public String getB_id(){
        return b_id;
    }
    public String getBusiness_name(){
        return name;
    }
    public String getStreet_address(){
        return address;
    }
    public String getCity(){
        return city;
    }
    public String getState(){
        return state;
    }
    public String getZipcode(){
        return zip;
    }
    public float getStar_rating(){
        return rating;
    }
    public int getNum_tips(){
        return tip_count;
    }
    public int getRank(){
        return rank;
    }
    public double getDistance(){
        return distance;
    }
    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
}
