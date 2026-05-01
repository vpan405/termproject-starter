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
     */
    @GetMapping("/states")
    public ResponseEntity<?> getStates() {
        List<String> states;
        try {
            states = yelpRepository.getStateData();
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the data...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("states", states)));
    }

    /*
     http://localhost:3000
     /api/filters?state=AZ
     */
    @GetMapping("/filters")
    public ResponseEntity<?> getFilters(@RequestParam String state) {
        List<String> categories;
        try {
            categories = yelpRepository.getCategories(state);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the categories for the state and city", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("categories", categories)));
    }

    /*
    http://localhost:3000
    /api/businesses
    {
        "key": "value",
        "optionalkeys": ["value1", "value2"]
    }
  */
    @RequestMapping(value = "/businesses", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getBusinesses(@RequestBody Map<String, Object> body) {

        List<Business> businesses;
        List<String> categories;
        String state;


        // Validate and parse required fields from the request body
        try {
            state = body.get("state").toString();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Required fields: state.", // include city too
                    "details", ex.getMessage()
            ));
        }
        // get the optional fields if they exist
        categories = (List<String>) body.get("categories");

        try {
            businesses = yelpRepository.queryBusinesses(state, categories);
        } catch (Exception ex) {
            throw new RuntimeException("Search failed...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("businesses", businesses)));
    }

    /*
    http://localhost:3000
    /api/businesses/{bid}
     */
    @GetMapping("/businesses/{bid}")
    public ResponseEntity<?> getBusinessDetails(@PathVariable String bid){
        Business businessDetails;
        try {
            businessDetails = yelpRepository.getBusinessDetails(bid);
        } catch (Exception ex) {
            throw new RuntimeException("Count not find the business for the given business id", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("business", businessDetails)));
    }
}