import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatbotServer {
	// Networking-related
	private int serverPort = 1500;
	private final int maxNumOfClients = 2;
	private int numOfClients = 0;
	private Socket[] clientSockets = new Socket[maxNumOfClients];
	private PrintWriter[] clientWriters = new PrintWriter[maxNumOfClients];
	private BufferedReader[] clientReaders = new BufferedReader[maxNumOfClients];

	public void go() {
	    try {
	    	ServerSocket serverSocket = new ServerSocket(this.serverPort);
	    	System.out.println("[Server - main] Server started. Waiting for connection.");
	    	
			// Keep listening to new connection requests
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("[Server - main] Accept connection from client.");

				// Reader to get inputs from client
				BufferedReader clientReader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
				);

				// Writer to send messages to client
				PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream());

				// Get index of current client
				int currentClientIdx = this.numOfClients;
				if (this.numOfClients >= this.maxNumOfClients) {
					// If the server is full, return a fail message
					this.sendMessage(
						clientWriter, 
						"Connection unsuccessful: server is full."
					);
					// Close the socket
					clientSocket.close();
					continue;
				}

				// Send message to client
				this.sendMessage(clientWriter, "[Server] ===== COMP2396 Questionnaire =====");
				this.sendMessage(clientWriter, "[Server] Please wait.");

				// Start the chatbot procedure in a new thread for a new client
				Thread thread = new Thread(new ClientHandler(currentClientIdx));
				thread.start();

				// Keep track of the reader, writer and other items about the current client
				this.clientSockets[currentClientIdx] = clientSocket;
				this.clientReaders[currentClientIdx] = clientReader;
				this.clientWriters[currentClientIdx] = clientWriter;
				this.numOfClients += 1;
			}
	        
	    } catch(IOException ex) {
	        ex.printStackTrace();
	    }
	}

	private class ClientHandler implements Runnable {
		private int clientIdx;

		public ClientHandler(int clientIdx) {
			this.clientIdx = clientIdx;
		} 

		// implementation of method from the Runnable interface
		public void run() {
			try {
				// Start the chatbot procedure
				startChatbot(this.clientIdx);

				// Process collected response 
				// This method may take some time...
				processResponse(this.clientIdx);

				// Close connection when done
				disconnect(this.clientIdx);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	} 

	public void sendMessage(PrintWriter writer, String message) {
		writer.println(message);
		writer.flush();
	}

	// Method to wait for ONE message from client
	public String waitForMessage(BufferedReader reader, Integer clientIdx) {
		String message;
		String clientName = "Client";
		if (clientIdx != null) { clientName+=clientIdx;}
		
		// Wait till a message is received
		try {
			while((message = reader.readLine()) != null) {
				// Log the received message on server terminal
				System.out.println("["+clientName+"] " + message); 

				// Once a non-null message is received,
				// stop waiting for the next message.
				break;
			}
			
		} catch(IOException ex) {
			ex.printStackTrace();
			// NOTE: Error is not handled here.
			message = null;
		}
		
		return message;
	}

	public synchronized void startChatbot(int clientIdx) {
		String message;
		PrintWriter writer = this.clientWriters[clientIdx];
		BufferedReader reader = this.clientReaders[clientIdx];


		System.out.println("====="); // Print on server terminal

		// Welcome message.
		this.sendMessage(writer, "[Server] Please input 'start' to begin.");

		// Wait for response
		while (true) {
			message = this.waitForMessage(reader, clientIdx);
			if (message.equals("start")) {
				// Wait till 'start' is received
				break;
			}
		}

		// Q1
		this.sendMessage(writer, "[Server] Are you taking COMP2396? (yes/no): ");
		
		// Wait for response
		message = this.waitForMessage(reader, clientIdx);
		
		if (!message.equals("yes")) {
			// Exit
			this.sendMessage(writer, "[Server] Thank you very much for your help.");
			System.out.println("====="); // Print on server terminal
			return;
		}

		// Q2
		this.sendMessage(writer, "[Server] Do you have any comments about COMP2396? (Input 'done' to stop)");
		
		// Allow user to input multiple lines of comments
		while (true) {
			message = this.waitForMessage(reader, clientIdx);
			if (message.equals("done")) {
				this.sendMessage(writer,"[Server] Thank you very much for your help. Have a nice day!");
				break;
			}
		}

		System.out.println("====="); // Print on server terminal
	}

	public synchronized void processResponse(int clientIdx) {
		// clientIdx is not used in this method, just to pretend this method
		// is processing responses from a specific client

		// Simulate processing of user response by 
		// printing messages on terminal and putting the thread to sleep.
		System.out.println("[Server] ===== Start Processing Response =====");
		System.out.println("[Server] Progress: 0%");

		try {
			Thread.sleep(5000);
		} catch (Exception ex) { ex.printStackTrace(); }
		
		System.out.println("[Server] Progress: 50%");

		try {
			Thread.sleep(5000);
		} catch (Exception ex) { ex.printStackTrace(); }
		System.out.println("[Server] Progress: 100%");

		System.out.println("[Server] Done.");
	}

	public void disconnect(int clientIdx) {
		try {
			// Close socket
			this.clientSockets[clientIdx].close();

			// Clear objects
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ChatbotServer server = new ChatbotServer();
		// Start server
		server.go();		
	}
}

