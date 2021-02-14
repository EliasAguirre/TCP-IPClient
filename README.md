# TCP/IP Client
TCP Client to communicate with RFC2229

## What is it?
<p>This is a Command Line Interface Client that allows a user to communicate with the Dictionary server (dict.org:2628). I use the java.net library through the use of the Socket in order to establish the connection with the server. To run, compile using 'make'. You can run the program using 'make run' which has the default debug option off. You can also run the program using 'java -jar CSdict.jar [-d]'. Note the -d option is for debug, which will print every command sent from the Client to the Server and every response from the Server to the Client. Additionally it will print the status responses, which are otherwise hidden. </p>


### NOTES/IMPROVEMENTS
<ol>
<li>Parsing of response:  Currently I identify the end of a response by parsing each text line and looking for the specific status codes. Ideally an improvement would be to identify responses in blocks, from expected status code and parse all the way to the first '.' which is how the protocol designates an end of a text response block.</li>
<li>Class Abstraction: Currently all funtionality is in the Command Class, we have one class containing all command methods to communicate with the Dictionary. Ideally we could have some helper classes to clean each method or even separate each command in its own class. For the scope of this project it didn't seem necessary.</li>
</ol>
