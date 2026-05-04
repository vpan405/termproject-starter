/* ============================================================
   CS 3431 - Database Systems I
   Worcester Polytechnic Institute (WPI)

   Sakire Arslan Ay
   All rights reserved.
   This code is provided for educational purposes only and may not be used for commercial applications.
   Note: This is starter code provided to students.
   Modify as instructed in the assignment.
 ============================================================*/

package yelpapp.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

@Repository
public class YelpRepository {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String JDBC_URL = dotenv.get("JDBC_URL");
    private static final String JDBC_USER = dotenv.get("JDBC_USER");
    private static final String JDBC_PASS = dotenv.get("JDBC_PASS");

    private static final Logger logger = Logger.getLogger(YelpRepository.class.getName());

    private Connection connection;

    public List<String> getStateData() {
        List<String> states = new ArrayList<>();
        logger.info("getStates called in initialize.");
        String stateQuery = """
            SELECT DISTINCT state
            FROM business
            ORDER BY state
         """;

        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            logger.info("Executing query: " + stateQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                states.add(rs.getString("state"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return states;
    }

    public List<String> getCities(String state) {
        List<String> cities = new ArrayList<>();
        logger.info("getCategories called in initialize.");
        if (state == null) {
            return cities;
        }
        String cityQuery = """
                SELECT DISTINCT b.city FROM business b
                WHERE b.state = ?
                ORDER BY b.city
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(cityQuery)) {
            logger.info("Executing query: " + cityQuery);
            ps.setString(1, state);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cities;
    }

    public List<String> getCategories(String state, String city) {
        List<String> categories = new ArrayList<>();
        logger.info("getCategories called in initialize.");
        if (state == null) {
            return categories;
        }
        String categoryQuery = """
                SELECT DISTINCT belongsto.cat_name
                FROM belongsto
                JOIN business ON business.b_id = belongsto.b_id
                WHERE business.state= ? AND business.city = ?
                ORDER BY belongsto.cat_name
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(categoryQuery)) {
            logger.info("Executing query: " + categoryQuery);
            ps.setString(1, state);
            ps.setString(2, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("cat_name"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    public List<String> getAttributes(String state, String city) {
        List<String> attributes = new ArrayList<>();
        logger.info("getAttributes called in initialize.");
        if (state == null) {
            return attributes;
        }
        String attributeQuery = """
                SELECT DISTINCT businessattribute.att_name
                FROM businessattribute
                JOIN business ON business.b_id = businessattribute.b_id
                WHERE business.state = ? AND business.city = ? AND businessattribute.att_value = 'True'
                ORDER BY businessattribute.att_name
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(attributeQuery)) {
            logger.info("Executing query: " + attributeQuery);
            ps.setString(1, state);
            ps.setString(2, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                attributes.add(rs.getString("att_name"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return attributes;
    }

    public List<String> getWifiValues(String state, String city) {
        List<String> wifiValues = new ArrayList<>();
        logger.info("getWifiValues called.");
        if (state == null || city == null) {
            return wifiValues;
        }
        String wifiQuery = """
                SELECT DISTINCT A.att_value
                FROM BusinessAttribute A
                JOIN Business B ON B.b_id = A.b_id
                WHERE B.state = ? AND B.city = ?
                  AND A.att_name = 'WiFi'
                  AND A.att_value IS NOT NULL
                ORDER BY A.att_value
                """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(wifiQuery)) {
            logger.info("Executing query: " + wifiQuery);
            ps.setString(1, state);
            ps.setString(2, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                wifiValues.add(rs.getString("att_value"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing getWifiValues: " + ex.getMessage());
            ex.printStackTrace();
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return wifiValues;
    }

    public List<String> getPriceRangeValues(String state, String city) {
        List<String> priceValues = new ArrayList<>();
        logger.info("getPriceRangeValues called.");
        if (state == null || city == null) {
            return priceValues;
        }
        String priceQuery = """
                SELECT DISTINCT A.att_value
                FROM BusinessAttribute A
                JOIN Business B ON B.b_id = A.b_id
                WHERE B.state = ? AND B.city = ?
                  AND A.att_name = 'RestaurantsPriceRange2'
                  AND A.att_value IS NOT NULL
                ORDER BY A.att_value
                """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(priceQuery)) {
            logger.info("Executing query: " + priceQuery);
            ps.setString(1, state);
            ps.setString(2, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                priceValues.add(rs.getString("att_value"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing getPriceRangeValues: " + ex.getMessage());
            ex.printStackTrace();
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return priceValues;
    }

    public List<Business> queryBusinesses(String state, String city, List<String> categories, List<String> attributes, String wifi, String priceRange) {
        List<Business> res = new ArrayList<>();
        logger.info("queryBusinesses is called.");

        boolean hasCategories = categories != null && !categories.isEmpty();
        boolean hasAttributes = attributes != null && !attributes.isEmpty();
        boolean hasWifi = wifi != null && !wifi.isBlank();
        boolean hasPriceRange = priceRange != null && !priceRange.isBlank();

        String businessQuery = """
                SELECT B.b_id, B.name, B.address, B.city, B.state, B.zip,
                       B.rating, B.tip_count, B.latitude, B.longitude
                FROM Business B
                """;

        if (hasCategories) {
            businessQuery += """
                    JOIN BelongsTo C ON C.b_id = B.b_id
                    """;
        }
        if (hasAttributes) {
            businessQuery += """
                    JOIN BusinessAttribute A ON A.b_id = B.b_id
                    """;
        }
        if (hasWifi) {
            businessQuery += """
                    JOIN BusinessAttribute W ON W.b_id = B.b_id
                    AND W.att_name = 'WiFi'
                    AND W.att_value = ?
                    """;
        }
        if (hasPriceRange) {
            businessQuery += """
                    JOIN BusinessAttribute P ON P.b_id = B.b_id
                    AND P.att_name = 'RestaurantsPriceRange2'
                    AND P.att_value = ?
                    """;
        }

        businessQuery += """
                WHERE B.state = ? AND B.city = ?
                """;

        if (hasCategories) {
            //add placeholders for every category in categories
            businessQuery += "AND C.cat_name IN (";
            for (int i = 0; i < categories.size(); i++) {
                if (i < categories.size() - 1) {
                    businessQuery += "?, ";
                } else {
                    businessQuery += "?";
                }
            }
            businessQuery += """
                    )
                    """;
        }

        if (hasAttributes) {
            //add placeholders for every attribute in attributes
            businessQuery += "AND A.att_name IN (";
            for (int i = 0; i < attributes.size(); i++) {
                if (i < attributes.size() - 1) {
                    businessQuery += "?, ";
                } else {
                    businessQuery += "?";
                }
            }
            businessQuery += """
                    ) AND A.att_value = 'True'
                    """;
        }

        if (hasCategories || hasAttributes) {
            businessQuery += """
                    GROUP BY B.b_id, B.name, B.address, B.city, B.state, B.zip,
                             B.rating, B.tip_count, B.latitude, B.longitude
                    """;

            //add the end of the query only if categories or attributes or both are selected
            businessQuery += "HAVING ";
            if (hasCategories) {
                businessQuery += "COUNT(DISTINCT C.cat_name) = " + categories.size();
            }
            if (hasCategories && hasAttributes) {
                businessQuery += " AND ";
            }
            if (hasAttributes) {
                businessQuery += "COUNT(DISTINCT A.att_name) = " + attributes.size();
            }
            businessQuery += "\n";
        }

        businessQuery += """
                ORDER BY B.name
                """;

        // establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            logger.info("Executing query: " + businessQuery);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            int idx = 1;
            if(hasWifi) {
                ps.setString(idx++, wifi);
            }
            if(hasPriceRange) {
                ps.setString(idx++, priceRange);
            }
            ps.setString(idx++, state);
            ps.setString(idx++, city);

            for (String cat : categories) {
                ps.setString(idx++, cat);
            }
            for (String att : attributes) {
                ps.setString(idx++, att);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new Business(
                            rs.getString("b_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zip"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getFloat("rating"),
                            rs.getInt("tip_count"),
                            0,
                            0.0
                    ));
                }
            }
        } catch (SQLException ex) {
            logger.severe("business search failed: " + ex.getMessage());
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public Business getBusinessDetails(String business_id) {
        Business res = null;

        String businessQuery = """
                SELECT b_id, name, address, city, state, zip, rating, tip_count, latitude, longitude
                FROM Business
                WHERE b_id = ?
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            logger.info("Executing query: " + businessQuery);
            ps.setString(1, business_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res = new Business(
                            rs.getString("b_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zip"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getFloat("rating"),
                            rs.getInt("tip_count"),
                            0,
                            0.0
                    );
                }
            }
        } catch (SQLException ex) {
            logger.severe("business search failed: " + ex.getMessage());
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }
    public List<String> getBusinessHours(String business_id, String day_of_week) {
        List<String> res = new ArrayList<>();
        res.add(day_of_week);

        String businessQuery = """
                SELECT h.weekday, h.open_time, h.close_time
                FROM Business b
                    JOIN Hours h ON b.b_id = h.b_id
                WHERE b.b_id = ?
                AND h.weekday = ?;
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            logger.info("Executing query: " + businessQuery);
            ps.setString(1, business_id);
            ps.setString(2, day_of_week);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(rs.getString("open_time"));
                    res.add(rs.getString("close_time"));
                }
            }
        } catch (SQLException ex) {
            logger.severe("business hours search failed: " + ex.getMessage());
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public List<String> getBusinessCategories(String business_id) {
        List<String> res = new ArrayList<>();

        String businessQuery = """
                SELECT bt.cat_name
                FROM Business b
                    JOIN BelongsTo bt ON b.b_id = bt.b_id
                WHERE b.b_id = ?
                GROUP BY b.b_id, bt.cat_name
                ORDER BY bt.cat_name;
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            logger.info("Executing query: " + businessQuery);
            ps.setString(1, business_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(rs.getString("cat_name"));
                }
            }
        } catch (SQLException ex) {
            logger.severe("business category search failed: " + ex.getMessage());
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public List<String> getBusinessAttributes(String business_id) {
        List<String> res = new ArrayList<>();

        String businessQuery = """
                SELECT ba.att_name, ba.att_value
                FROM Business b
                    JOIN BusinessAttribute ba ON b.b_id = ba.b_id
                WHERE b.b_id = ?
                GROUP BY b.b_id, ba.att_name, ba.att_value
                ORDER BY ba.att_name;
        """;
        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            logger.info("Executing query: " + businessQuery);
            ps.setString(1, business_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(rs.getString("att_name") + " (" + rs.getString("att_value") + ")");
                }
            }
        } catch (SQLException ex) {
            logger.severe("business attribute search failed: " + ex.getMessage());
        }
        //close connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

}
