/* ============================================================
   CS 3431 - Database Systems I
   Worcester Polytechnic Institute (WPI)

   Sakire Arslan Ay
   All rights reserved.
   This code is provided for educational purposes only and may not be used for commercial applications.
   Note: This is starter code provided to students.
   Modify as instructed in the assignment.
 ============================================================*/

package yelpapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yelpapp.model.Business;
import yelpapp.model.YelpRepository;

import java.util.*;
import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class YelpController {

    private final YelpRepository yelpRepository;

    @Autowired
    public YelpController(YelpRepository yelpRepository) {
        this.yelpRepository = yelpRepository;
    }

    /*
    http://localhost:3000
    /api/states
    Example response: [
  {
    "states": [
      "AZ",
      "IL",
      "NC",
      "NV",
      "OH",
      "PA",
      "SC",
      "WI"
    ]
  }
]
    */
    @GetMapping("/states")
    public ResponseEntity<?> getStates() {
        List<String> states;
        try {
            states = yelpRepository.getStateData();
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the states...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("states", states)));
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities(@RequestParam String state) {
        List<String> cities;
        try {
            cities = yelpRepository.getCities(state);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the cities...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("cities", cities)));
    }

    /*
     http://localhost:3000
     /api/filters?state=value
     example: [
  {
    "categories": [
      "Acai Bowls",  "Accessories"]}]
     */
    @GetMapping("/filters")
    public ResponseEntity<?> getFilters(@RequestParam String state, @RequestParam String city) {
        List<String> categories;
        List<String> attributes;
        try {
            categories = yelpRepository.getCategories(state, city);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the categories for the state and city..", ex);
        }
        try {
            attributes = yelpRepository.getAttributes(state, city);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the attributes for the state and city..", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("categories", categories), Map.of("attributes", attributes)));
    }

    /*
    http://localhost:3000
    /api/businesses
    {
	"state": "NC",
    "categories": ["Restaurants", "Food"]
    }
  */
    @RequestMapping(value = "/businesses", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getBusinesses(@RequestBody Map<String, Object> body) {

        List<Business> businesses;
        List<String> categories;
        String state;
        String city;


        // Validate and parse required fields from the request body
        try {
            state = body.get("state").toString();
            city = body.get("city").toString();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Required fields: state, city",
                    "details", ex.getMessage()
            ));
        }

        // get the optional fields if they exist
        categories = (List<String>) body.get("categories");

        try {
            businesses = yelpRepository.queryBusinesses(state, city, categories);
        } catch (Exception ex) {
            throw new RuntimeException("Search failed...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("businesses", businesses)));
    }

    /*
    http://localhost:3000
    /api/businesses/{bid}
    {
	"state": "NC",
    "categories": ["Restaurants", "Food"]
    }
  */
    @GetMapping("/businesses/{bid}")
    public ResponseEntity<?> getBusinessDetails(@PathVariable String bid) {
        Business businessDetails;

        try {
            businessDetails = yelpRepository.getBusinessDetails(bid);
        } catch (Exception ex) {
            throw new RuntimeException("Could not find the business for given business id ... ", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("business", businessDetails)));
    }
}