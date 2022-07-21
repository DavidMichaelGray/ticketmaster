package com.example.ticketmaster;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
@RestController
public class ArtistController
{

    private JSONArray getArtistsFromTicketMaster () {
        try {

            URL url = new URL("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/artists.json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else
            {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONArray artistArray = (JSONArray) parse.parse(inline);

                return artistArray;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new JSONArray ());
    }
    private String getVenueNameFromId (String id) {
        try {

            URL url = new URL("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/venues.json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else
            {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONArray venueList = (JSONArray) parse.parse(inline);

                for (int i = 0; i < venueList.size(); i++) {
                    JSONObject venue = (JSONObject) venueList.get(i);
                    System.out.println("Venue name: " + venue.get("name"));
                    if (venue.get("id").equals(id)) {
                        return venue.get("name").toString();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR: No Venue found with id = " + id;
    }

    @GetMapping("/api/artists")
    @ResponseBody
    public String getArtists(@RequestParam(required = false) String id) {
        if (id == null) {
            return "ERROR:  You must provide an Artist ID number";
        }
        JSONArray artistList = getArtistsFromTicketMaster();
        for (int i = 0; i < artistList.size(); i++) {
            JSONObject artist = (JSONObject) artistList.get(i);
            System.out.println("Artist Name:  " + artist.get("name"));
            if (artist.get("id").equals(id)) {
                System.out.println("The venue with id = 45 is " + getVenueNameFromId("45"));
                return "The artist with id = " + id + " is " + artist.get("name");
            }
        }
        return "ERROR:  No artist found with id = "+ id;
    }

    @GetMapping(path="/api/testing", produces = "application/json")
    public String testing()
    {
        getArtistsFromTicketMaster();
        return "{\"Testing\"}";
    }
}
