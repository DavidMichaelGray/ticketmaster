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
                    if (venue.get("id").equals(id)) {
                        return venue.get("name").toString();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ERROR: No Venue found with id = " + id);
        return "TBA"; // Return To Be Announced for Unknown venues
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
    public JSONObject getArtistInformation(@RequestParam(required = false) String id) {
        try
        {
            if (id == null)
            {
                JSONObject errorJSON = new JSONObject();
                errorJSON.put("success", false);
                errorJSON.put("message", "ERROR:  No Artist Id field specified");
                return errorJSON;
            }
            // This object will be used to store all the information
            // we want to return about this artist
            JSONObject artistInformation = new JSONObject();
            // This array will be used to store a list of all the artist's performances
            JSONArray artistPerformances = new JSONArray();
            // Get the name and personal information for the artist
            JSONArray artistList = getArtists();
            for (int i = 0; i < artistList.size(); i++)
            {
                JSONObject artist = (JSONObject) artistList.get(i);
                if (artist.get("id").equals(id))
                {
                    // We found the artist in the list
                    artistInformation.put("name", artist.get("name"));
                    artistInformation.put("id", artist.get("id"));
                    artistInformation.put("rank", artist.get("rank"));
                    artistInformation.put("url", artist.get("url"));
                    artistInformation.put("imgSrc", artist.get("imgSrc"));
                    // Now get the list of all events for this artist and their venues
                    JSONArray eventList = getEventsList();
                    for (int x = 0; x < eventList.size(); x++)
                    {
                        JSONObject event = (JSONObject) eventList.get(x);
                        // Make sure this event is not hidden from searching
                        boolean eventhidden = false;
                        try {
                            if ((boolean) event.get("hiddenFromSearch")) {
                                eventhidden = true;
                            }
                        } catch (Exception e) {
                            // hiddenFromSearch field is not present, so the event is not hidden
                        }
                        // If this event is hidden, then we can skip it
                        if (!eventhidden)
                        {
                            // Get the list of artists for this particular event
                            JSONArray eventArtistList = (JSONArray) event.get("artists");
                            for (int y = 0; y < eventArtistList.size(); y++)
                            {
                                JSONObject eventArtist = (JSONObject) eventArtistList.get(y);
                                if (eventArtist.get("id").equals(id))
                                {
                                    // Our artist is performing at this event
                                    // Get the name of the venue
                                    JSONObject venue = (JSONObject) event.get("venue");
                                    String venueName = getVenueNameFromId(venue.get("id").toString());
                                    System.out.println("Artist " + artist.get("name") + " is performing in " + event.get("title") + " at " + venueName);
                                    JSONObject performance = new JSONObject();
                                    performance.put("venue", venueName);
                                    performance.put("event", event.get("title"));
                                    String eventDate;
                                    try
                                    {
                                        // If the event has a startDate, then save the date
                                        eventDate = event.get("startDate").toString();
                                    } catch (Exception e)
                                    {
                                        eventDate = "TBA"; // To Be Announced
                                    }
                                    performance.put("date", eventDate);
                                    artistPerformances.add(performance);
                                    break; // Exit the for loop to avoid bad data where artist is listed more than once for an event
                                }
                            }
                        }
                    }
                    artistInformation.put("success", true); // Return a success flag
                    // Add the list of performances to the information returned about the artist
                    artistInformation.put("performances", artistPerformances);
                    return artistInformation;
                }
            }
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("success", false);
            errorJSON.put("message", "ERROR:  No artist found with id = " + id);
            return errorJSON;
        } catch (Exception e) {
            // Catch any generic errors
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("success", false);
            errorJSON.put("message", "ERROR:  Error occurred during Artist search");
            return errorJSON;
        }
    }
}
