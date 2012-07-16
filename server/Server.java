/*
 *
 * Based on the server here: www.prasannatech.net/2008/10/simple-http-server-java.html
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	
	Socket connectedClient = null;	
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;
    Map<String, String> passStore = new HashMap<String, String>();	
    ArrayList<String> assignments = new ArrayList<String>();
			
	public Server(Socket client) {
        initPassStore();
        initAssignments();
		connectedClient = client;
	}			

    public void initPassStore()
    {
        try{
            File f = new File("passes.txt");
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine();
                if(line != null && !line.equals(""))
                {
                    String[] parts = line.split(" - ");
                    passStore.put(parts[0].trim(),parts[1].trim());
                }
            }
        }
        catch(Exception e){ 
            log("Couldn't read from passes.txt");
            System.exit(1); 
        }
    }

    public void initAssignments()
    {
        try{
            File f = new File("assignments.txt");
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine();
                if(line != null && !line.equals(""))
                {
                    assignments.add(line.trim());
                }
            }
        }
        catch(Exception e){ 
            log("Couldn't read from passes.txt");
            System.exit(1); 
        }
    }


    public String verifyLogin(String pass)
    {
        log("PASS IS: " + pass);
        if(pass != null)
        {
            String user = passStore.get(pass);
            if(user != null)
            {
                return "pass="+pass + ";user="+user;
            }
        }
        return null;
    }

    public boolean validLogin(String cookies)
    {
        log("Testing login for " + cookies);
        try
        {
            String passStr = (((cookies.split(";")[0]).split("="))[1]).trim();
            return (verifyLogin(passStr) != null);
        }
        catch(Exception e)
        {
        }
        return false;
    }
			
	public void run() {
		
		try {
		
			//log( "The Client "+ connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");
            
            inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));                  
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            String requestString = inFromClient.readLine();
            String headerLine = requestString;

            if(headerLine == null || headerLine.equals(""))
            {
                log("Error: headerLine was null");
                log("-1");
                sendResponse(404, "<b>The Requested resource was not found ....</b>", false);	
                return;
            }

                
            String[] headerLineArr = headerLine.split(" ");
            String httpMethod = headerLineArr[0];
            String httpQueryString = headerLineArr[1];


            String cookies = null;            
                
            log("The HTTP request string is ....");
            while (inFromClient.ready())
            {
                log(requestString);
                String line = inFromClient.readLine();
                requestString = line;
                if(line.contains("Cookie:"))
                {
                    cookies = line.split(":")[1].trim();
                }

            }        

            httpQueryString = httpQueryString.substring(1);
            log("noramlized query was: " + httpQueryString);
			if (httpMethod.equals("GET")) 
            {
                log("1");
                if(httpQueryString.equals("login.html") || httpQueryString.equals("") )
                {
                    log("2");
                    sendResponse(200, "login.html", true);
                    return;
                }
                else if(httpQueryString.equals("index.js"))
                {
                    log("4");
                    sendResponse(200, "index.js", true);
                    return;
                }
                else if(httpQueryString.equals("favicon.ico"))
                {
                    log("5");
                    sendResponse(200, "favicon.ico", true);
                    return;
                }
                else if(httpQueryString.contains("proccessLogin"))
                {
                    log("5.5");
                    String cookiesToSet = verifyLogin((httpQueryString.split("\\?")[1]).split("=")[1]);
                    if(cookiesToSet != null)
                    {
                        sendResponse(200, "index.html", true, cookiesToSet);
                    }
                    else
                    {
                        sendResponse(200, "login.html", true);
                    }
                    return;
                }
                else if(httpQueryString.equals("index.css"))
                {
                    log("6");
                    sendResponse(200, "index.css", true);
                    return;
                }
                else if(validLogin(cookies))
                {
                    log("7");
                    if(httpQueryString.equals("index.html"))
                    {
                        log("8");
                        sendResponse(200, "index.html", true);
                        return;
                    }
                    else if(httpQueryString.equals("submit.html"))
                    {
                        log("9");
                        sendResponse(200, "submit.html", true);
                        return;
                    }
                    else if(httpQueryString.equals("submissions.html"))
                    {
                        log("10");
                        sendResponse(200, "submissions.html", true);
                        return;
                    }
                    else if(httpQueryString.contains("logout"))
                    {
                        log("11");
                        sendResponse(200, "login.html", true, cookies, true);
                        return;
                    }
                    else 
                    {
                        log("12");
                        sendResponse(404, "<b>The Requested resource was not found ....</b>", false);	
                        return;
                    }
                }
                else
                {
                    log("13");
                    sendResponse(200, "login.html", true);
                    return;
                }
			} 
            else if(httpMethod.equals("POST"))
            {
                if(httpQueryString.contains("upload"))
                {
                    log("14");
                    return;
                }
/*
                log("14.222");
                else if(httpQueryString.contains("proccessLogin"))
                {
                    String cookiesToSet = verifyLogin((httpQueryString.split("\\?")[1]).split("=")[1]);
                    if(cookiesToSet != null)
                    {
                        sendResponse(200, "index.html", true, cookiesToSet);
                    }
                    else
                    {
                        sendResponse(200, "login.html", true);
                    }
                    return;
                }
*/
            }

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void sendResponse (int statusCode, String responseString, boolean isFile) throws Exception {
        sendResponse(statusCode, responseString, isFile, null, false);
    }
	
	public void sendResponse (int statusCode, String responseString, boolean isFile, String cookies) throws Exception {
        sendResponse(statusCode, responseString, isFile, cookies, false);
    }

	public void sendResponse (int statusCode, String responseString, boolean isFile, String cookies, boolean expireCookies) throws Exception {
	    log("SENDING RESPONSE FOR: " + responseString);	
		String statusLine = null;
		String serverdetails = "Server: Autograder\r\n";
		String contentLengthLine = null;
		String fileName = null;		
        String cookieLine = "";
		String contentTypeLine = "Content-Type: text/html" + "\r\n";
		FileInputStream fin = null;
		
		if (statusCode == 200)
			statusLine = "HTTP/1.1 200 OK" + "\r\n";
		else
			statusLine = "HTTP/1.1 404 Not Found" + "\r\n";	
			
		if (isFile) {
			fileName = responseString;			
			fin = new FileInputStream(fileName);
			contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
			if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
				contentTypeLine = "Content-Type: \r\n";	
		}						
		else {
			responseString = responseString;
			contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";	
		}			

        if(cookies != null)
        {
            String[] cookieArr = cookies.split(";");
            for(int i = 0; i < cookieArr.length; i++)
            {
                cookieLine += "Set-Cookie: " +  cookieArr[i];
                if(expireCookies)
                {
                    cookieLine +="; Expires=Wed, 09 Jun 1999 10:18:14 GMT"; // at least we party like it does
                }
                cookieLine += "\r\n";
            }
        }

		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(serverdetails);
		outToClient.writeBytes(contentTypeLine);
		outToClient.writeBytes(contentLengthLine);
		outToClient.writeBytes(cookieLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");
		
		if(isFile)
        {
            sendFile(fin, outToClient);
        }
		else
        {
            outToClient.writeBytes(responseString);
        }
		
		outToClient.close();
	}
	
	public void sendFile (FileInputStream fin, DataOutputStream out) throws Exception {
        log("SENDING FILE!");
		byte[] buffer = new byte[1024] ;
		int bytesRead;
	
		while ((bytesRead = fin.read(buffer)) != -1 ) {
		out.write(buffer, 0, bytesRead);
	    }
	    fin.close();
	}

    public static void log(String s)
    {
        System.out.println(s);
    }
			
	public static void main (String args[]) throws Exception {
        //String ipStr = "http://50.116.13.217";
        int port = 3030;
		ServerSocket serversock = new ServerSocket (port);
		//ServerSocket Server = new ServerSocket (port, 10, InetAddress.getByName(ipStr));
		log ("Autograder Waiting for client on port " + port);
								
		while(true) {	                	   	      	
				Socket connected = serversock.accept();
	            (new Server(connected)).start();
        }      
	}
}
