package com.example.ticketmaster;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.client.RestTemplate;

@RestController
public class ArtistController
{

    private JSONArray getArtists ()
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/artists.json";
            // Fetch JSON response as String wrapped in ResponseEntity
            ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
            int responsecode = response.getStatusCodeValue();
            if (responsecode != 200) {
                // If the Ticketmaster S3 endpoint failed, then we need to return an error
                throw new RuntimeException("Ticketmaster S3 endpoint failed.  HttpResponseCode: " + responsecode);
            }
            String artistsJSON = response.getBody();
            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONArray artistArray = (JSONArray) parse.parse(artistsJSON);

            return artistArray;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return new JSONArray();
    }
    private String getVenueNameFromId (String id) {
        try {

            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/venues.json";
            // Fetch JSON response as String wrapped in ResponseEntity
            ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
            int responsecode = response.getStatusCodeValue();
            if (responsecode != 200) {
                // If the Ticketmaster S3 endpoint failed, then we need to return an error
                throw new RuntimeException("Ticketmaster S3 endpoint failed.  HttpResponseCode: " + responsecode);
            }
            String venuesJSON = response.getBody();
            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONArray venueList = (JSONArray) parse.parse(venuesJSON);
            for (int i = 0; i < venueList.size(); i++) {
                JSONObject venue = (JSONObject) venueList.get(i);
                 if (venue.get("id").equals(id)) {
                     return venue.get("name").toString();
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

            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/events.json";
            // Fetch JSON response as String wrapped in ResponseEntity
            ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
            int responsecode = response.getStatusCodeValue();
            if (responsecode != 200) {
                // If the Ticketmaster S3 endpoint failed, then we need to return an error
                throw new RuntimeException("Ticketmaster S3 endpoint failed.  HttpResponseCode: " + responsecode);
            }
            String eventsJSON = response.getBody();
            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONArray eventList = (JSONArray) parse.parse(eventsJSON);
            return eventList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    @GetMapping("/api/artists")
    @ResponseBody
    public ResponseEntity<JSONObject> getArtistInformation(@RequestParam(required = false) String id) {
        try
        {
            if (id == null)
            {
                JSONObject errorJSON = new JSONObject();
                errorJSON.put("success", false);
                errorJSON.put("message", "ERROR:  No Artist Id field specified");
                return new ResponseEntity<JSONObject>(errorJSON, HttpStatus.BAD_REQUEST);
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
                        try
                        {
                            if ((boolean) event.get("hiddenFromSearch"))
                            {
                                eventhidden = true;
                            }
                        } catch (Exception e)
                        {
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
                    return new ResponseEntity<JSONObject>(artistInformation, HttpStatus.OK);
                }
            }
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("success", false);
            errorJSON.put("message", "ERROR:  No artist found with id = " + id);
            return new ResponseEntity<JSONObject>(errorJSON, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Catch any generic errors
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("success", false);
            errorJSON.put("message", "ERROR:  Error occurred during Artist search");
            return new ResponseEntity<JSONObject>(errorJSON, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
