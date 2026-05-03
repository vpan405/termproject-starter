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

    public List<Business> queryBusinesses(String state, String city, List<String> categories) {
        List<Business> res = new ArrayList<>();
        logger.info("queryBusinesses is called.");
        String businessQuery = """
                    """;
        if (!categories.isEmpty()) {
            businessQuery += """
                SELECT B.b_id, name, address, city, state, zip, rating, tip_count, latitude, longitude
                              FROM Business B
                              JOIN BelongsTo C ON C.b_id = B.b_id
                              WHERE B.state = ?
                                AND B.city = ?
                                AND C.cat_name IN (
                              
        """;
        }
        else {
            businessQuery += """
                SELECT B.b_id, name, address, city, state, zip, rating, tip_count, latitude, longitude
                              FROM Business B
                              WHERE B.state = ?
                                AND B.city = ?
                              
        """;
        }
        //add placeholders for every category in categories
        for (int i = 0; i < categories.size(); i++) {
            if (i == categories.size() - 1) {
                businessQuery += """
                    ? """;
            }
            else {
                businessQuery += """
                    ?, """;
            }
        }

        //add the end of the query only if categories are selected
        if (!categories.isEmpty()) {
            businessQuery += """
            ) 
            GROUP BY B.b_id, name, address, city, state, zip, rating, tip_count, latitude, longitude
            HAVING COUNT(DISTINCT cat_name) = ?
            """;
        }

        businessQuery += """
            ORDER BY name""";

        //establish connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //execute query
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            logger.info("Executing query: " + businessQuery);
            ps.setString(1, state);
            ps.setString(2, city);
            int index = 3;
            for (String cat: categories) {
                ps.setString(index, cat);
                index++;
            }
            if(!categories.isEmpty()) {
                ps.setInt(index, categories.size());
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

}
