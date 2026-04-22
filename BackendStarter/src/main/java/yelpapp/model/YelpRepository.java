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

    public List<String> placeHolder1() {
        List<String> data = new ArrayList<>();
        logger.info("Placeholder method called.");
        return data;
    }

    public List<String> placeHolder2(String param) {
        List<String> data = new ArrayList<>();
        logger.info("Placeholder method called.");
        return data;
    }

    public List<String> placeHolder3(String key, List<String> optionalkeys) {
        List<String> data = new ArrayList<>();
        logger.info("Placeholder method called.");
        return data;
    }

}
