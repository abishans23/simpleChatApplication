package edu.seg2105.server.ui;

import java.io.IOException;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.client.ui.ClientConsole;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF{
	public final static int DEFAULT_PORT = 5555;
	EchoServer server;
	Scanner fromConsole;

	@Override
	public void display(String message) {
		System.out.println(message);
		
	}
	
	public ServerConsole(int port) {
		try {
			server = new EchoServer(port, this);
			server.listen();
		}catch(Exception exception) {
			System.out.println("Error: Can't setup connection!\"\r\n"
					+ "Terminating server.");
			
			System.exit(1);
		}
		fromConsole = new Scanner(System.in); 
	}
	
	public void accept() {
		try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	}
	
	
	public static void main(String[] args) 
	  {
	    int port = 0;

	    try
	    {
	      port = Integer.parseInt(args[0]);
	    }
	    catch(ArrayIndexOutOfBoundsException e)
	    {
	      port = DEFAULT_PORT;
	    }
	    catch(NumberFormatException e) {
	    	port = DEFAULT_PORT;
	    }

	    ServerConsole chat= new ServerConsole(port);
	    chat.accept();  //Wait for console data
	  
	}

}

