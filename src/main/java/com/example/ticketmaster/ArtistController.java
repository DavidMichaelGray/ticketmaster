package com.example.ticketmaster;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    private void getArtistsFromTicketMaster () {
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

                //Get the required object from the above created object
                JSONObject obj = (JSONObject) artistArray.get(0);
                //Get the required data using its key
                System.out.println(obj.get("name"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(path="/artists", produces = "application/json")
    public String getEmployees()
    {
        getArtistsFromTicketMaster();
        return "{\"Testing\"}";
    }
}
