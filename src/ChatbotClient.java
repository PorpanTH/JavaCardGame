import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

public class ChatbotClient {
	// Networking-related
	private String serverIP = "127.0.0.1";
	private int serverPort = 1500;
	private Socket sock = null;
	private PrintWriter writer = null;
	
	private void connect() {
		try {
			// Create a socket to connect to the server
			this.sock = new Socket(this.serverIP, this.serverPort);
			
			// Output stream to send messages to the server
			this.writer = new PrintWriter(sock.getOutputStream());
 
			// Run a thread to listen to messages from server
			Thread readerThread = new Thread(new ServerHandler());
			readerThread.start(); // start the thread
			
			// Success message to be printed on the client side
			System.out.println("networking established");
		} catch(IOException ex) {
			ex.printStackTrace();
	 	}
	}

	public void go() {
		this.drawGUI(); // Draw client GUI
		this.connect(); // Connect to server
	} 
	
	public static void main(String[] args) {
		ChatbotClient client = new ChatbotClient();
		// Start client
		client.go();
	}

	// To handle inputs from server
	private class ServerHandler implements Runnable {
		private BufferedReader reader = null; 
		
		public ServerHandler() {
			try {
				this.reader = new BufferedReader(
					new InputStreamReader(sock.getInputStream())
					);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 
			
		// implementation of method from the Runnable interface
		public void run() {
			String message;
			try {
				while((message = reader.readLine()) != null) {
					messageArea.append(message+"\n");
				} // close while
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	} 
	
	// GUI-related
	private JTextArea messageArea = null;
	private JTextField messageTextField = null;

	private void drawGUI() {
		JFrame frame = new JFrame("Chatbot Client");
		
		//***** Message Panel - show text messages *****//
		JPanel messagePanel = new JPanel();

		// Message Area
		this.messageArea = new JTextArea(30, 30);
		messageArea.setEditable(false);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(messageArea); 
		messagePanel.add(scrollPane);
		
		frame.getContentPane().add(BorderLayout.CENTER, messagePanel);
		
		//***** Control Panel - input text field and send button ******//
		JPanel controlPanel = new JPanel();
		
		// Input text field
		this.messageTextField = new JTextField(20);
		controlPanel.add(this.messageTextField);
		
		// Send button
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		controlPanel.add(sendButton);

		frame.getContentPane().add(BorderLayout.SOUTH, controlPanel);
		frame.setSize(400,400);
		frame.setVisible(true);
	}

	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				// Attempt to get the text in text field
				String message = messageTextField.getText();
				writer.println(message);
				// Output the text
				writer.flush();

				// Display text on messageArea
				messageArea.append(message+"\n");
		
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			// Clear the text field after sending the message
			messageTextField.setText("");
			messageTextField.requestFocus();
		}
	}
}
