package yelpapp.model;

public class Business {
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

    public Business(String business_id,
                    String name,
                    String address,
                    String city,
                    String state,
                    String zipcode,
                    double latitude,
                    double longitude,
                    float star_rating,
                    int tipCount,
                    int rank,
                    double distance) {
        this.b_id = business_id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = star_rating;
        this.tip_count = tipCount;
        this.rank = rank;
        this.distance = distance;
    }

    public String getB_id() {
        return b_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRating() {
        return rating;
    }

    public int getTip_count() {
        return tip_count;
    }

    public int getRank() {
        return rank;
    }

    public double getDistance() {
        return distance;
    }
}