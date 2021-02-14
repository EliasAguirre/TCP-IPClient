# TCP/IP Client
TCP Client to communicate with RFC2229

## What is it? How does it work?
<p>This is a Command Line Interface Client that allows a user to communicate with the Dictionary server (dict.org:2628). I use the java.net library through the use of the Socket in order to establish the connection with the server. To run, compile using 'make'. You can run the program using 'make run' which has the default debug option off. You can also run the program using 'java -jar CSdict.jar [-d]'. Note the -d option is for debug, which will print every command sent from the Client to the Server and every response from the Server to the Client. Additionally it will print the status responses, which are otherwise hidden. </p>

<p>The Client gracefully deals with all errors, not killing the program but rather provide feedback on what action the user can take.</p>

<p>Valid Commands:
<ol>
<li>open SERVER PORT</li>
  <ol>
<li>Provide a server and a port to establish the connection to. You need to run this before any other command or they will fail, except quit.</li>
</ol>
<li>dict</li>
  <ol>
<li>List all dictionaries.</li>
</ol>
<li>set DICTIONARY</li>
  <ol>
<li>Set dictionary to retireve definitions from.</li>
</ol>
<li>define WORD</li>
  <ol>
<li>Find all defition for WORD in all dictionaries or in set dictionary.</li>
</ol>
<li>match WORD</li>
  <ol>
<li>Get and preint all exact matches for WORD.</li>
</ol>
<li>prefixmatch WORD</li>
  <ol>
<li>Get and print all prefix matches for WORD.</li>
</ol>
<li>close</li>
  <ol>
<li>Command to close the established connection but not kill the program.</li>
</ol>
<li>quit</li>
  <ol>
<li>Command to exit the Client and kill the program.</li>
</ol>
</ol>

</p>


### NOTES/IMPROVEMENTS
<ol>
<li>Parsing of response:  Currently I identify the end of a response by parsing each text line and looking for the specific status codes. Ideally an improvement would be to identify responses in blocks, from expected status code and parse all the way to the first '.' which is how the protocol designates an end of a text response block.</li>
<li>Class Abstraction: Currently all funtionality is in the Command Class, we have one class containing all command methods to communicate with the Dictionary. Ideally we could have some helper classes to clean each method or even separate each command in its own class. For the scope of this project it didn't seem necessary.</li>
</ol>
