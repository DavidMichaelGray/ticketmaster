# David Gray Assignment for Ticketmaster
This is a Spring Boot application that provides a single REST API endpoint to search for an Artist available through Ticketmaster.
After running the application, the endpoint is accessed at the following URL:  

http://localhost:8080/api/artists?id=##  
where ## is the Artist Id number 

Example:  http://localhost:8080/api/artists?id=21

The endpoint returns a JSON object with all of the information for the artist and a list of all the events at which the artist is performing.  For example, a search for artist with id = 21, returns the following: 

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

To run the application:
1. Install Java 17 on your PC
2. Download the latest ticketmaster-1.0.jar from the Release page on this site
3. Type java -jar ticketmaster-1.0.jar in the directory where you downloaded the jar file
4. Browse the REST API endpoint here:  http://localhost:8080/api/artists?id=21




