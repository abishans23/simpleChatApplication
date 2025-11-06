// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import client.AbstractClient;
import edu.seg2105.client.common.*;
import edu.seg2105.client.ui.ClientConsole;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
    sendToServer("#login <"+ loginID +">");
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  
    clientUI.display(msg.toString());
    
    
  }
  
  public String getLoginID() {
	  return this.loginID;
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if(message.startsWith("#")) {
		  handleCommand(message);
	  }
	  
	  else{
		    try
		    {
		      sendToServer(message);
		    }
		    catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.");
		      quit();
		    }
	   }
  }
  
  
  private void handleCommand(String message) {
	  
	if(message.equals("#quit")){
		try {
			this.sendToServer("#quit");
			quit();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed client disconnection");
		}

		
	}
	
	else if(message.equals("#logoff")) {
		try {
			this.sendToServer("#logoff");

			closeConnection();	


		} catch (IOException e) {
			
			System.out.println("Failed to close connection");
		}
	}
	
	else if(message.contains("#sethost <")) {
		if(!isConnected()) {
			int index = 10;
			String s = "";
			while(true) {
				if(message.charAt(index)==( '>'))break;
					
				s+=message.charAt(index);
				index++;
				
			}
			
			setHost(s);
		}
		
		else {
			System.out.println("Client is still logged in");
		}
		
	}
	
	else if(message.contains("#setport <")) {
		if(!isConnected()) {
			int index = 10;
			String s = "";
			while(true) {
				if(message.charAt(index)==( '>'))break;
					
				s+=message.charAt(index);
				index++;
				
			}
			
			setPort(Integer.parseInt(s));
		}
		
		else {
			System.out.println("Client is still logged in");
		}
	}
	
	else if(message.equals("#login")) {
		if(!isConnected()) {
			try {
				openConnection();
				sendToServer("#login <" + loginID + ">");
				
			} catch (IOException e) {
				System.out.println("Failed to open connection");
			}
		}
		else {
			try {
				sendToServer("#login <" + loginID + ">");
			} catch (IOException e) {
				System.out.println("Client is still logged in");

			}

		}
	}
	
	else if(message.equals("#gethost")) {
		System.out.println("Host is: " + getHost());
	}
	
	else if(message.equals("#getport")) {
		System.out.println("Port is: " + getPort());
	} 
	
	
	 
	
  }


/**
	 * Implement hook method to be called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
  protected void connectionException(Exception exception) {
		clientUI.display("The server has shut down.");

		quit();
		System.exit(1);
	
  }
  
  /**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
		clientUI.display("Connection closed");
	  
	  
	}
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
