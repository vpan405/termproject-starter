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

    example response [
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
}
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
     /api/endpoint2?param=value
     */
    @GetMapping("/endpoint2")
    public ResponseEntity<?> sampleGetMethod2(@RequestParam String param) {
        List<String> data;
        try {
            data = yelpRepository.placeHolder2(param);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get the data...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("data", data)));
    }

    /*
    http://localhost:3000
    /api/endpoint3
    {
        "key": "value",
        "optionalkeys": ["value1", "value2"]
    }
  */
    @RequestMapping(value = "/endpoint3", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getBusinesses(@RequestBody Map<String, Object> body) {

        List<String> data;
        List<String> optionalkeys;
        String key;


        // Validate and parse required fields from the request body
        try {
            key = body.get("key").toString();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Required fields: data.",
                    "details", ex.getMessage()
            ));
        }
        // get the optional fields if they exist
        optionalkeys = (List<String>) body.get("optionalkeys");

        try {
            data = yelpRepository.placeHolder3(key, optionalkeys);
        } catch (Exception ex) {
            throw new RuntimeException("Search failed...", ex);
        }
        return ResponseEntity.ok(List.of(Map.of("data", data)));
    }
}