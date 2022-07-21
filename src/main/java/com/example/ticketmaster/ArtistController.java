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

    private JSONArray getArtists () {
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

    private JSONArray getEventsList() {
        try {

            URL url = new URL("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/events.json");

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
                JSONArray eventList = (JSONArray) parse.parse(inline);

                return eventList;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    @GetMapping("/api/artists")
    @ResponseBody
    public String getArtistInformation(@RequestParam(required = false) String id) {
        if (id == null) {
            return "ERROR:  You must provide an Artist ID number";
        }
        // This object will be used to store all the information
        // we want to return about this artist
        JSONObject artistInformation = new JSONObject();
        // Get the name and personal information for the artist
        JSONArray artistList = getArtists();
        for (int i = 0; i < artistList.size(); i++) {
            JSONObject artist = (JSONObject) artistList.get(i);
            System.out.println("Artist Name:  " + artist.get("name"));
            if (artist.get("id").equals(id)) {
                // We found the artist in the list
                artistInformation.put("name", artist.get("name"));
                // Now get the list of all events for this artist and their venues
                JSONArray eventList = getEventsList();
                for (int x = 0; x < eventList.size(); x++) {
                    JSONObject event = (JSONObject) eventList.get(x);
                    // Get the list of artists for this particular event
                    JSONArray eventArtistList = (JSONArray) event.get("artists");
                    for (int y = 0; y < eventArtistList.size(); y++) {
                        JSONObject eventArtist = (JSONObject) eventArtistList.get(y);
                        if (eventArtist.get("id").equals(id)) {
                            // Our artist is performing at this event
                            // Get the name of the venue
                            JSONObject venue = (JSONObject) event.get("venue");
                            String venueName = getVenueNameFromId(venue.get("id").toString());
                            System.out.println("Artist is performing in "+ event.get("title") + " at " + venueName);
                        }
                    }
                }
                return artistInformation.toJSONString();
            }
        }
        return "ERROR:  No artist found with id = "+ id;
    }

    @GetMapping(path="/api/testing", produces = "application/json")
    public String testing()
    {
        getArtists();
        return "{\"Testing\"}";
    }
}
