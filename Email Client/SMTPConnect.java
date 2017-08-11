/*************************************
 * Filename:  SMTPConnect.java
 * Names: Bradley Dodd
 * Student-IDs: 201030851
 * Date: 11/10/15
 *************************************/
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to mailserver and send one mail.
 *
 */
public class SMTPConnect {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading from and writing to the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnect object. Create the socket and the 
       associated streams. Initialise SMTP connection. */
    public SMTPConnect(EmailMessage mailmessage) throws IOException {
	connection = new Socket(mailmessage.DestHost, SMTP_PORT);
	fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	toServer =   new DataOutputStream(connection.getOutputStream());
	
	/* Read a line from server and check that the reply code is 220.
	   If not, throw an IOException. */
	String serverResponse = fromServer.readLine();
	System.out.println(serverResponse);
	if (!serverResponse.startsWith("220")) {
		throw new IOException("220 reply not received from server.");
	}

	/* SMTP handshake. We need the name of the local machine.
	   Send the appropriate SMTP handshake command. */
	String localhost = InetAddress.getLocalHost().getHostName();
	sendCommand("HELO " + localhost, 250);

	isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(EmailMessage mailmessage) throws IOException {
	/* Send all the necessary commands to send a message. Call
	   sendCommand() to do the dirty work. Do _not_ catch the
	   exception thrown from sendCommand(). */
	    sendCommand("MAIL FROM:<" + mailmessage.Sender + ">", 250);
		sendCommand("RCPT TO:<" + mailmessage.Recipient + ">", 250);
		sendCommand("DATA", 354);
		sendCommand("SUBJECT:" + mailmessage.Subject + CRLF + mailmessage.Body + CRLF + ".", 250);
    }

    /* Close SMTP connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
	isConnected = false;
	try {
	    sendCommand("QUIT", 221);
	    connection.close();
	} catch (IOException e) {
	    System.out.println("Unable to close connection: " + e);
	    isConnected = true;
	}
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
	/* Write command to server and read reply from server. */
	System.out.print(command + CRLF);
	toServer.writeBytes(command + CRLF);

	/* Check that the server's reply code is the same as the parameter
	   rc. If not, throw an IOException. */
	String serverResponse = null;
	serverResponse = fromServer.readLine();
	int serverResponseCode = Integer.parseInt(serverResponse.substring(0, 3));
	System.out.println(serverResponseCode);
	if (serverResponseCode != rc) {
		throw new IOException("Error: Reply " + serverResponseCode + " Recieved");
	}
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
	if(isConnected) {
	    close();
	}
	super.finalize();
    }
} 
