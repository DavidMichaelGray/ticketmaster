# David Gray Assignment for Ticketmaster
This is a Spring Boot application that provides a single REST API endpoint to search for an Artist available through Ticketmaster.
After running the application, the endpoint is accessed at the following URL:  

http://localhost:8080/api/artists?id=##  
where ## is the Artist Id number 

Example:  http://localhost:8080/api/artists?id=21

The endpoint returns a JSON object with all of the information for the artist and a list of all the events at which the artist is performing.  For example, a search for artist with id = 21, returns the following JSON for the artist named HRH Prog: 

        {
        "success": true,
        "name": "HRH Prog",
        "rank": 1,
        "id": "21", 
        "imgSrc": "//some-base-url/hrh-prog.jpg",
        "performances": [ 
        {
            "date": "2020-10-17T00:00:00",
            "venue": "O2 Academy Sheffield",
            "event": "Fusion Prog" 
        },
        { 
            "date": "TBA", 
            "venue": "O2 Academy Brixton",
            "event": "A festival Live"
        },
        {
            "date": "TBA", 
            "venue": "O2 Academy Sheffield",
            "event": "Huge Live" 
        }
        ],
        "url": "/hrh-prog-tickets/artist/21" 
        }

<h2>To run the application</h2>

1. Install JRE (Java Runtime Enviroment) for Java 17 onto your PC
2. Download the **ticketmaster-1.0.jar** from the Release section of this github page
3. Type **java -jar ticketmaster-1.0.jar** in the directory where you downloaded the jar file
4. Browse the REST API endpoint here:  http://localhost:8080/api/artists?id=21
 

<h2>To build and run the application using the source code</h2>
1. Install the Java Development Kit (JDK) for Java 17 onto your PC<br>  
2. Install latest version of Maven onto your PC and add the path to the mvn binary to your PATH  <br>
3. Click on Code | Download ZIP on this github page to download the source code for the project  <br>
4. Extract the .zip file into a working directory  <br>
5. Open a Command Prompt and navigate to the root of the working directory where you unzipped the project <br> 
6. Type <b>mvn spring-boot:run</b> to run the application  <br>
7. Browse the REST API endpoint here:  http://localhost:8080/api/artists?id=21  <br>

<h2>Implementation Notes and Decisions</h2>
1. I assumed that any of the data might change, so I coded the search to pull the data from S3 every time.  <br>
2. I observed that venue #40 does not exist, so for events assigned to an invalid venue, I put "TBA" (to be announced) for the venue.  <br>
3. I observed that some events have dates and others do not, so I just return blank for the date for the events that do not have one.  I did not change the format of the date.  <br>
4. Events with the "hideFromSearch" property set to true are excluded from the search results.  <br>
5. The JSON returned by my REST API endpoint includes a boolean "success" property that indicates whether the search was successful or not.  <br>
6. Invalid requests to the REST API (e.g. invalid Artist Id) return HTTP Status of 400.


