/*************************************
 * Filename:  EmailClient.java
 * Names: Bradley Dodd
 * Student-IDs: 201030851
 * Date: 11/10/15
 *************************************/

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class EmailClient extends Frame {
	/* The stuff for the GUI. */
	private Button btSend = new Button("Send");
	private Button btClear = new Button("Clear");
	private Button btQuit = new Button("Quit");
	private Label serverLabel = new Label("Local mailserver:");
	//private TextField serverField = new TextField("mail.csc.liv.ac.uk", 40);
	private String localHostName;
	private TextField serverField = new TextField("mail.csc.liv.ac.uk", 40);
	private Label fromLabel = new Label("From:");
	private TextField fromField = new TextField("", 40);
	private Label toLabel = new Label("To:"); 
	private TextField toField = new TextField("", 40);
	private Label ccLabel = new Label("CC:"); 
	private TextField ccField = new TextField("", 40);
	private Label subjectLabel = new Label("Subject:");
	private TextField subjectField = new TextField("", 40);
	private Label messageLabel = new Label("Message:");
	private TextArea messageText = new TextArea(30, 80);
	private Label urlLabel = new Label("HTTP://");
	private TextField urlField = new TextField("cgi.csc.liv.ac.uk/~gairing/test.txt", 40);
	private Button btGet = new Button("Get");
	private Button localHostGet = new Button("Get");
    /**
     * Create a new EmailClient window with fields for entering all
     * the relevant information (From, To, Subject, and message).
     */
    public EmailClient() {
		super("Java Emailclient");
	
	/* Create panels for holding the fields. To make it look nice,
	   create an extra panel for holding all the child panels. */
	Panel serverPanel = new Panel(new BorderLayout());
	Panel fromPanel = new Panel(new BorderLayout());
	Panel toPanel = new Panel(new BorderLayout());
	Panel ccPanel = new Panel(new BorderLayout());
	Panel subjectPanel = new Panel(new BorderLayout());
	Panel messagePanel = new Panel(new BorderLayout());
	serverPanel.add(serverLabel, BorderLayout.WEST);
	serverPanel.add(serverField, BorderLayout.CENTER);
	/*Add get local host button the the server panel */
	serverPanel.add(localHostGet, BorderLayout.EAST);
	localHostGet.addActionListener(new LocalHostGetListener());
	
	fromPanel.add(fromLabel, BorderLayout.WEST);
	fromPanel.add(fromField, BorderLayout.CENTER);
	toPanel.add(toLabel, BorderLayout.WEST);
	toPanel.add(toField, BorderLayout.CENTER);
	ccPanel.add(ccLabel, BorderLayout.WEST);
	ccPanel.add(ccField, BorderLayout.CENTER);
	subjectPanel.add(subjectLabel, BorderLayout.WEST);
	subjectPanel.add(subjectField, BorderLayout.CENTER);
	messagePanel.add(messageLabel, BorderLayout.NORTH);	
	messagePanel.add(messageText, BorderLayout.CENTER);
	Panel fieldPanel = new Panel(new GridLayout(0, 1));
	fieldPanel.add(serverPanel);
	fieldPanel.add(fromPanel);
	fieldPanel.add(toPanel);
	fieldPanel.add(ccPanel);
	fieldPanel.add(subjectPanel);
		
	/* Create a panel for the URL field and add listener to the GET 
	   button. */
	Panel urlPanel = new Panel(new BorderLayout());
	urlPanel.add(urlLabel, BorderLayout.WEST);
	urlPanel.add(urlField, BorderLayout.CENTER);
	urlPanel.add(btGet, BorderLayout.EAST);
	fieldPanel.add(urlPanel);
	btGet.addActionListener(new GetListener());
	
	
	/* Create a panel for the buttons and add listeners to the
	   buttons. */
	Panel buttonPanel = new Panel(new GridLayout(1, 0));
	btSend.addActionListener(new SendListener());
	btClear.addActionListener(new ClearListener());
	btQuit.addActionListener(new QuitListener());
	buttonPanel.add(btSend);
	buttonPanel.add(btClear);
	buttonPanel.add(btQuit);
	
	/* Add, pack, and show. */
	add(fieldPanel, BorderLayout.NORTH);
	add(messagePanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
	pack();
	setVisible(true);
    }

    static public void main(String argv[]) {
	new EmailClient();
    }

    /* Handler for the Send-button. */
    class SendListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    System.out.println("Sending mail");
	    
	    /* Check that we have the local mailserver */
	    if ((serverField.getText()).equals("")) {
			System.out.println("Need name of local mailserver!");
		return;
	    }

	    /* Check that we have the sender and recipient. */
	    if((fromField.getText()).equals("")) {
			System.out.println("Need sender!");
		return;
	    }
	    if((toField.getText()).equals("")) {
			System.out.println("Need recipient!");
		return;
	    }

	    /* Create the message */
	    EmailMessage mailMessage;
        try {
	    mailMessage = new EmailMessage(fromField.getText(), 
					      toField.getText(), 
						  ccField.getText(),
					      subjectField.getText(), 
					      messageText.getText(), serverField.getText());
        } catch (UnknownHostException e) {
    		/* If there is an error, do not go further */
    		return;
    	    }
        
	    /* Check that the message is valid, i.e., sender and
	       recipient addresses look ok. */
	    if(!mailMessage.isValid()) {
		return;
	    }
		
		 try {
			SMTPConnect connection = new SMTPConnect(mailMessage);
			connection.send(mailMessage);
			connection.close();
	    } catch (IOException error) {
		System.out.println("Sending failed: " + error);
		return;
	    }
	    System.out.println("Email sent succesfully!");
		
		/* Check if ccField isn't empty. If so call mailMessage with ccField as the recipient.*/
		if (ccField.getText() != ""){
			String[] ccRecipientsArray = new String[] { };
			ccRecipientsArray = ccField.getText().split("[;]");
			EmailMessage mailMessageCC;
			int arrayLength = ccRecipientsArray.length; 
			for(int i = 0; i < arrayLength; i = i + 1){
				String ccAddress = ccRecipientsArray[i];
				try {
				mailMessageCC = new EmailMessage(fromField.getText(), 
								  ccAddress,
								  ccField.getText(),
								  subjectField.getText(), 
								  messageText.getText(), serverField.getText());
				} catch (UnknownHostException e) {
					/* If there is an error, do not go further */
					return;
				}
				try {
					SMTPConnect connection = new SMTPConnect(mailMessageCC);
					connection.send(mailMessageCC);
					connection.close();
				} catch (IOException error) {
				System.out.println("Sending failed: " + error);
				return;
				}
			}
				
			System.out.println("Email sent succesfully!");
		}
	}
    }
    /* Get URL if specified. */

    class GetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			String receivedText;

			/* Check if URL field is empty. */
			if((urlField.getText()).equals("")) {
				System.out.println("Need URL!");
				return;
			}			
			/* Pass string from URL field to HTTPGet (trimmed);
			   returned string is either requested object 
			   or some error message. */
			  
			HttpInteract request = new HttpInteract(urlField.getText().trim());
			
			// Send http request. Returned String holds object 
			try {
				receivedText=request.send();
			}
			catch (IOException error) {
				messageText.setText("Downloading File failed.\r\nIOException: " + error);
				return;
			}
			// Change message text 
			messageText.setText(receivedText);
		
		}
    }
    /* Clear the fields on the GUI. */
    class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("Clearing fields");
			fromField.setText("");
			toField.setText("");
			ccField.setText("");
			subjectField.setText("");
			messageText.setText("");
		}
	}

    /* Quit. */
    class QuitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
    }

	 /* Set the server field to address of local host */
    class LocalHostGetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				localHostName = InetAddress.getLocalHost().getHostName();
				serverField.setText(localHostName);
				} catch (IOException exception) {
					System.out.println("Unable to close connection: " + exception);
				}
			return;

		}
	}
}

