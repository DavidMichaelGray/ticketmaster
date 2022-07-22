# David Gray Interview Assignment for Ticketmaster
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

1. Install Java 17 on your PC
2. Download the latest **ticketmaster-1.0.jar** from the Release page of this github page
3. Type **java -jar ticketmaster-1.0.jar** in the directory where you downloaded the jar file
4. Browse the REST API endpoint here:  http://localhost:8080/api/artists?id=21
 

<h2>To build and run the application using the source code</h2>
1. Install the Java Development Kit (JDK) for Java 17 onto your PC
2. Install latest version of Maven onto your PC
3. Click on Code | Download ZIP on this github page to download the source code for the project
4. Extract the .zip file into a working directory
5. Open a Command Prompt and navigate to the root of the working directory where you unzipped the project
6. Type **mvn spring-boot:run** to run the application
7. Browse the REST API endpoint here:  http://localhost:8080/api/artists?id=21



