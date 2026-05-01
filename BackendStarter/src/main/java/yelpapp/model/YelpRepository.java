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

    // returns business state
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
            logger.info("Executing query:" + stateQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                states.add(rs.getString("state"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query:" + ex.getMessage());
            ex.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return states;
    }

    // returns business categories based on its state
    public List<String> getCategories(String state) {
        List<String> categories = new ArrayList<>();
        logger.info("getCategoriesForCity called in initialize.");
        if (state == null) {
            return categories;
        }
        String categoryQuery = """
            SELECT DISTINCT BelongsTo.cat_name
            FROM BelongsTo
            JOIN business ON BelongsTo.b_id = business.b_id
            WHERE business.state = ?
            ORDER BY BelongsTo.cat_name
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(categoryQuery)) {
            logger.info("Executing query:" + categoryQuery);
            ps.setString(1, state);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("cat_name"));
            }
        } catch (SQLException ex) {
            logger.severe("Error executing query:" + ex.getMessage());
            ex.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // return business attributes
    public List<Business> queryBusinesses(String state, List<String> categories) {
        List<Business> res = new ArrayList<>();
        logger.info("queryBusinesses is called.");
        String businessQuery = """ 
            SELECT b_id, name, address, city, state, zip, latitude, longitude, rating, tip_count
            FROM business
            WHERE business.state = ?
        """; // is_open???
        /*
        for (String cat: categories){
            // do something
         */
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            ps.setString(1, state);
            // int count = 2;
            /*
            for (String cat: categories){
                // do something, increment count
            */
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new Business (
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
                            0, // assign rank to 0. rank field is needed for similarity search query result
                            0.0 // assign distance to 0. distance field is needed for similarity search query result
                    ));
                }
            }
        } catch (SQLException ex) {
            logger.severe("Business search failed!: " + ex.getMessage());
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Business getBusinessDetails(String business_id){
        Business res = null;
        String businessQuery = """
            SELECT b_id, name, address, city, state, zip, latitude, longitude, rating, tip_count
            FROM business
            WHERE b_id = ?
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {
            ps.setString(1,business_id);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res = new Business (
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
                            0, // assign rank to 0. rank field is needed for similarity search query result
                            0.0 // assign distance to 0. distance field is needed for similarity search query result
                    );
                }
            }
        } catch (SQLException ex) {
            logger.severe("Business details query failed!: " + ex.getMessage());
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}
