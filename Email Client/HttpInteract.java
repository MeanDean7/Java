/*************************************
 * Filename:  HttpInteract.java
 * Names: Bradley Dodd	
 * Student-IDs: 201030851
 * Date: 11/10/15
 *************************************/

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Class for downloading one object from http server.
 *
 */
public class HttpInteract {
	private String host;
	private String path;
	private String requestMessage;
		
	private static final int HTTP_PORT = 80;
	private static final String CRLF = "\r\n";
	private static final int BUF_SIZE = 4096; 
	private static final int MAX_OBJECT_SIZE = 102400;

 	/* Create a HttpInteract object. */
	public HttpInteract(String url) {
		
		/* Split "URL" into "host name" and "path name", and
		 * set host and path class variables. 
		 * if URL is only a host name, use "/" as path 
		 */
		try
		{
			String inputURL = "http://" + url;
			URL urlAddr = new URL(inputURL);
			String urlPath = urlAddr.getPath();
			host =  urlAddr.getHost();
			if (urlPath == ""){
				path =  "//";
			}else {
				path = urlPath;
			}; 
		}catch(IOException urlException)
		{
			 System.out.println("Error: Exception found");
		}
		/* Construct requestMessage, add a header line so that
		 * server closes connection after one response. */		
		requestMessage = "GET " + path + " HTTP/1.1" + CRLF;
		requestMessage += "Host: " + host + CRLF;
		requestMessage += "Connection: close" + CRLF;
		return;
	}	
	
	
	/* Send Http request, parse response and return requested object 
	 * as String (if no errors), 
	 * otherwise return meaningful error message. 
	 * Don't catch Exceptions. EmailClient will handle them. */		
	public String send() throws IOException {
		
		/* buffer to read object in 4kB chunks */
		char[] buf = new char[BUF_SIZE];

		/* Maximum size of object is 100kB, which should be enough for most objects. 
		 * Change constant if you need more. */		
		char[] body = new char[MAX_OBJECT_SIZE];
		
		String statusLine="";	// status line
		int status;		// status code
		String headers="";	// headers
		int bodyLength=-1;	// lenghth of body
				
		String[] tmp;
		
		/* The socket to the server */
		Socket connection;
		
		/* Streams for reading and writing the socket */
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		System.out.println("Connecting server: " + host+CRLF);
		
		/* Connect to http server on port 80.
		 * Assign input and output streams to connection. */
		connection = new Socket(host, HTTP_PORT);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer =   new DataOutputStream(connection.getOutputStream());
		
		System.out.println("Send request:\n" + requestMessage);

		/* Send requestMessage to http server */
		toServer.writeBytes(requestMessage + CRLF);
		
		/* Read the status line from response message */
		statusLine = fromServer.readLine();
		System.out.println("Status Line:\n" + statusLine + CRLF);
		
		/* Extract status code from status line. If status code is not 200,
		 * close connection and return an error message. 
		 * Do NOT throw an exception */		
		int serverResponseCode = Integer.parseInt(statusLine.substring(9, 12));
		if (serverResponseCode != 200) {
			System.out.println("Error: server response code is not 200");
		}

		/* Read header lines from response message, convert to a string, 
 		 * and assign to "headers" variable. 
		 * Recall that an empty line indicates end of headers.
		 * Extract length  from "Content-Length:" (or "Content-length:") 
		 * header line, if present, and assign to "bodyLength" variable. 
		*/
		StringBuilder responseHeaders = new StringBuilder();
		String responseLine = "";
		while (fromServer.ready() && !(responseLine = fromServer.readLine()).isEmpty()) {
			if(responseLine.startsWith("Content-Length:")) {
				bodyLength = Integer.parseInt(responseLine.substring(16, 18));
			}
			responseHeaders.append(responseLine + CRLF);
		}
		headers = responseHeaders.toString();
		System.out.println("Headers:\n"+headers+CRLF);

		/* If object is larger than MAX_OBJECT_SIZE, close the connection and 
		 * return meaningful message. */
		if (bodyLength > MAX_OBJECT_SIZE) {
			connection.close();
			return("Content Length is longer than maximum object size" +bodyLength);
		}
		
		/* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
		 * into body[]. Stop when either we have
		 * read Content-Length bytes or when the connection is
		 * closed (when there is no Content-Length in the response). 
		 * Use one of the read() methods of BufferedReader here, NOT readLine().
		 * Also make sure not to read more than MAX_OBJECT_SIZE characters.
		 */				
		int bytesRead = 0;
		while (bytesRead < bodyLength && (bodyLength != 0 || fromServer.ready())) {
			fromServer.read(buf, bytesRead, BUF_SIZE);
			bytesRead = bytesRead + BUF_SIZE;
			body = buf;
		}

		/* At this points body[] should hold to body of the downloaded object and 
		 * bytesRead should hold the number of bytes read from the BufferedReader
		 */
		
		/* Close connection and return object as String. */
		System.out.println("Done reading file. Closing connection.");
		connection.close();
		return(new String(body, 0, bytesRead));
	}
}
