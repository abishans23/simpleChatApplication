package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.server.ui.ServerConsole;
import server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ChatIF serverConsole;
  //Constructors ****************************************************
  private final static String key = "loginID";
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverConsole) 
  {
    super(port);
    this.serverConsole = serverConsole;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String m = (String )msg;
    serverConsole.display("Message received: " + msg + " from " + client.getInfo(key) + "");

    
    if(m.contains("#logoff") || m.contains("#quit")) {
    	try {
    		serverConsole.display("Closing client " + client);
			client.close();
		} catch (IOException e) {
			serverConsole.display("Failed to close connection");
		}
    }
    
    else if(m.startsWith("#login")) {
    	if(client.getInfo(key) == null) {
    		int index = 8;
    		String loginID = "";
    		while(true) {
    			if(m.charAt(index)==( '>'))break;
    				
    			loginID+=m.charAt(index);
    			index++;
    			
    		}
    		client.setInfo(key, loginID);
    	}
    	
    	else {
    		try {
    			System.out.println("FAILURE");
				client.close();
			} catch (IOException e) {
				serverConsole.display("Could not terminate client");;
			}
    	}
    }
    
    else {
        this.sendToAllClients(client.getInfo(key) + "> " + msg);

    }
    
    
  }
  
  public void handleCommand(String message) {
	  if(message.equals("#quit")) 
	  {
		  try {
			  //sendToAllClients("SERVER MSG> " + "Server has stopped listening for connections.");
			 
			stopListening();
			this.close();
			
		} catch (IOException e) {
			String msg = "Server failed to quit";
			serverConsole.display(msg);
			sendToAllClients("SERVER MSG> " + msg);
		}
		  
		//serverConsole.display("Server is quitting");
		System.exit(0);
	  }
	  
	  if(message.equals("#close")) {
		//  sendToAllClients("SERVER MSG> " + "Server is closing connections");
		  try {
			  //stopListening();

			this.close();
		} catch (IOException e) {
			String msg = "Server failed to quit";
			serverConsole.display(msg);
			sendToAllClients("SERVER MSG> " + msg);
		}
	  }
	  
	  else if(message.equals("#stop")) {
		 

		  stopListening();
		  
	  }
	  
	  else if(message.contains("#setport <") && !isListening()) {
		  int index = 10;
		  String s = "";
		  while(true) {
				if(message.charAt(index)==( '>'))break;
					
				s+=message.charAt(index);
				index++;
				
			}
		  setPort(Integer.parseInt(s));
	  }
	  
	  else if(message.equals("#start") && !isListening()) {
		  try {
			listen();
		} catch (IOException e) {
			serverConsole.display("Failed to listen");
		}
	  }
	  
	  else if(message.equals("#getport")) {
		  serverConsole.display(String.valueOf(getPort()));
	  }
	  
  }
  
  
  /**
   * This method handles any messages received from the server console.
   *
   * @param msg The message received from the server console.
   */
  public void handleMessageFromServerUI(String message) {
	  if(message.charAt(0)=='#') {
		  handleCommand(message);
	  }
	  else {
		  try {
			  
			  
			  
			  serverConsole.display("SERVER MSG> " + message);
			  this.sendToAllClients("SERVER MSG> " + message);
			  
			  
			  
		  }catch(Exception e) {
			  serverConsole.display("SERVER MSG> could not handle message from server console" );
		  }
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverConsole.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverConsole.display
      ("Server has stopped listening for connections.");
  }
  
  protected void serverClosed()
  {
//	  serverConsole.display
//      ("Server has shut down.");
  }  
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverConsole.display("A new client has connected to the server.");
  }
  
  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
	  
	  super.clientDisconnected(client);
	 
	  serverConsole.display(client.getInfo(key) + " has disconnected");
	  
	  
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
    ServerConsole console = new ServerConsole(port);
	
    EchoServer sv = new EchoServer(port, console);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
