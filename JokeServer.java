/* 

1. Name: Aaron Seo

2. Date: 2023-10-08

3. Java version: 20 (build 20+36-2344)
  Amazon Corretto\jdk20.0.2_10

4. Precise command-line compilation examples / instructions: 

> javac JokeServer.java 

5. Precise examples / instructions to run this program:

In separate shell windows:

Run these classes without any arguments.  Doing so will only run the default ports 4545 for JokeClient
and 5050 for JokeClientAdmin.  Only the primary server will run in JokeServer.

> java JokeServer
> java JokeClient
> java JokeClientAdmin


To run on a different host name or ip address instead of localhost without enabling a second server, 
do the following.

> java JokeClient <IPaddr> 
> java JokeClientAdmin <IPaddr> 

To create a connection with a secondary server for each class while running on a primary server under a
specific domain name or ip address for both servers, do the following:

> java JokeClient <IPaddr> <IPaddr> 
> java JokeClientAdmin <IPaddr> <IPaddr>

  ...If you wish to switch between these two servers, simply type s in the console after you ran 
  these specific java classes with these command arguments.

It is required to run both servers on different windows, otherwise, the clients and admins will not 
be able to reach either of them.  Running the JokeServer without any arguments will run primary ports 4545
and 5050 for the JokeWorker thread and AdminWorker thread respectively.  Ruunning the JokerServer
with the argument "secondary" will run the secondary server ports 4546 and 5051 for
the JokeWorker thread and AdminWorker thread respectively.
> java JokeServer 
> java JokeServer secondary


All acceptable console input commands are displayed on the various consoles.

Client and server can run on the same machine in different processes (in separate terminal windows), or on
different machines across the Internet depending on the argument passed to the JokeClient and JokeClientAdmin
 programs.

To run on localhost:

Terminal/CMD window 1> java JokeServer
Terminal/CMD window 2> java JokeClient
Terminal/CMD window 3> java JokeClient
[...]
Terminal/CMD window N> java ColorClient

Alternatively, to run over the Internet:

Terminal/CMD window 1> java JokeServer
Terminal/CMD window 2> java JokeServer secondary
Terminal/CMD window 3> java JokeClient <IP address>
Terminal/CMD window 4> java JokeClient <IP address> <IP address>
Terminal/CMD window 5> java JokeClientAdmin <IP address>
Terminal/CMD window 6> java JokeClientAdmin <IP address> <IP address>

[...]

6. Full list of files needed for running the program: 

 a. JokeServer.java



*/

//java.net is used for operations related for sockets and connections.
//java.util.Scanner uses a console to allow the user to make inputs
//when the program is running.
/*
 * java.util.Random is used to get four unique jokes or proverbs in any random order.
 * 
 */
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Collections;
import java.util.List;
import java.util.Random;


class JokeClient {

  //These integer variables are used to keep track of how many jokes or proverbs for each server
  //port have been sent and requested by the client.  Once any of them reaches 4, they get the
  //message that the cycle is complete and the numbers are resetted.
  private static int CycleCount = 0;
  private static int JokeCount = 0;
  private static int ProverbCount = 0;
  private static int SecondJokeCount = 0;
  private static int SecondProverbCount = 0;

  //The integer array variables are used to so that the jokes and proverbs in each cycle
  //are unique to each other and that the order of them are random each time the cycle is 
  //complete.  The joke worker thread will use these randomly sorted arrays to know
  //which index value should come first to last.
  private static int [] indexes = {0,1,2,3};
  private static int [] secondIndexes = {0,1,2,3};
  private static int [] indexforJokes;
  private static int [] indexsForProverbs;
  private static int [] indexforJokesSecond;
  private static int [] indexsForProverbsSecond;
  private static int secondServer = 0;
  private static int portNumber = 4545;
  private static String SecondServerName;

  //This varibale is for when the client wants to have a second server running and connecting.
  private static boolean switchEnable = false;

  
  public static void main(String argv[]) {
    JokeClient cc = new JokeClient(argv);
    //JokeClient cc is to create an empty symbol table for the client process.  This is
    //important for the setup in the beginning, otherwise you won't be able to store data
    // and values under a specific class.
    cc.run(argv);
    //cc.run is to help execute the run function below. The variable cc under class ColorClient
    //is required so that the function will know that it needs to be executed.
  }

  public JokeClient(String argv[]) { 
    System.out.println("\nThis is the Joke client constructor if you want to use it.\n");
  }
//The function 'run' is where any inputs made by the client user are sent to the server socket 
//for processing and that same server returns the output data to that client.
  public void run(String argv[]) {
    
      String serverName;
      String SecondServername;
      if (argv.length < 1) {
        serverName = "localhost"; 
        System.out.println("Server one: " + serverName + ", port 4545" );
      }
      else {
        serverName = argv[0];
        if (argv.length > 1){
          //System.out.println("You now have two servers to run on.");
          SecondServerName = argv[1];
          switchEnable = true;
          System.out.println("Server one: " + serverName + ", port 4545  Server two: " + SecondServerName + " port 4546");
        }
        else{
          System.out.println("Server one: " + serverName + ", port 4545" );
        }
        
      }
      
      // if(argv.length > 1){
      //   serverName = "140.192.1.9";
      // }

	String requestFromClient = "";


  
	//colorFromClient is the variable used to store the very color name inputed by the user.
	Scanner consoleIn = new Scanner(System.in);
	// The variable consoleIn under the class Scanner is used to allow the user to type any inputs.  It can be 
	// used for multiple inputs like user name or colors.  This input will already be open for the
	//input stream thanks to System.in.
	System.out.print("Enter your name: ");
	System.out.flush ();
	//.flush is used to clear the memory buffer system into an output stream.  This is to help
	//improve the performance of the program.
	String userName = consoleIn.nextLine();
	//userName string is where input for the user name is stored. consoleIn.nextLine is used to 
	//type in the input by the user.  Next time when consoleIn.nextline is used, it will
	//be blank to allow a brand new input from the user.
	System.out.println("Hi " + userName);
  System.out.print("Do you want a joke or proverb? \nJust press enter for one.\n To switch ports, press 's'. or type quit to end: ");
	do {

    
    if(CycleCount == 0){
	    //System.out.print("Do you want a joke or proverb? To switch ports, press 's'. \nJust press enter for one or type quit to end: ");
    }
    else{
      System.out.print("Do you want to hear another joke or proverb? Type yes, or type quit to stop: ");
    }
	  requestFromClient = consoleIn.nextLine();

    if(requestFromClient.equalsIgnoreCase("s") && switchEnable == false){
      System.out.println("No secondary server being used.\n");
    }
    if(requestFromClient.equalsIgnoreCase("s") && switchEnable == true){
      if(portNumber == 4545){
        
        portNumber = 4546;
        System.out.println("Now communicating with: " + SecondServerName + ", port " + portNumber + "\n");
      }
      else{
        portNumber = 4545;
        System.out.println("Now communicating with: " + serverName + ", port " + portNumber + "\n");
      }
      
    }
    
	  //Here, the variable colorFromclient will change from being an empty string into another word inputed
	  //by the user. In this case, it will be a color word. consoleIn.nextLine() may have been used
	  //for the username, but it start out blank, allowing a brand new input for colorFromClient.

      if(requestFromClient.equals("quit")){
        getJoke(userName,requestFromClient,serverName,JokeCount,ProverbCount,indexforJokes,indexsForProverbs,portNumber);
      }
	  
    else if(requestFromClient.equalsIgnoreCase("yes") || requestFromClient.equals("")){
      //CycleCount++;


      

      if(portNumber == 4545){

      if(JokeCount == 0){
        int[] array = indexes;
        Random rand = new Random();
        int length = array.length;
        for (int i = 0; i < length; i++){
          int randomIndexSwitch = rand.nextInt(length);
          int temp = array[randomIndexSwitch];
          array[randomIndexSwitch] = array[i];
          array[i] = temp;
        }

        //System.out.println(Arrays.toString(array));
        indexforJokes = array;
      }
      //System.out.println("Joke index is " + Arrays.toString(indexforJokes));
      if (ProverbCount == 0){
        int[] array = secondIndexes;
        Random rand = new Random();
        int length = array.length;
        for (int i = 0; i < length; i++){
          int randomIndexSwitch = rand.nextInt(length);
          int temp = array[randomIndexSwitch];
          array[randomIndexSwitch] = array[i];
          array[i] = temp;
        }
        //System.out.println(Arrays.toString(array));
        indexsForProverbs = array;
      }
      //System.out.println("Proverb indexes is " + Arrays.toString(indexsForProverbs));

      getJoke(userName,requestFromClient,serverName,JokeCount,ProverbCount, indexforJokes,indexsForProverbs,portNumber);
    }


    else if (switchEnable == true && portNumber == 4546){
      if(SecondJokeCount == 0){
        int[] array = indexes;
        Random rand = new Random();
        int length = array.length;
        for (int i = 0; i < length; i++){
          int randomIndexSwitch = rand.nextInt(length);
          int temp = array[randomIndexSwitch];
          array[randomIndexSwitch] = array[i];
          array[i] = temp;
        }

        //System.out.println(Arrays.toString(array));
        indexforJokesSecond = array;
      }
      //System.out.println("Joke index is " + Arrays.toString(indexforJokesSecond));
      if (SecondProverbCount == 0){
        int[] array = secondIndexes;
        Random rand = new Random();
        int length = array.length;
        for (int i = 0; i < length; i++){
          int randomIndexSwitch = rand.nextInt(length);
          int temp = array[randomIndexSwitch];
          array[randomIndexSwitch] = array[i];
          array[i] = temp;
        }
        //System.out.println(Arrays.toString(array));
        indexsForProverbsSecond = array;
      }
      //System.out.println("Proverb indexes is " + Arrays.toString(indexsForProverbsSecond));

      getJoke(userName,requestFromClient,SecondServerName,SecondJokeCount,SecondProverbCount, indexforJokesSecond,indexsForProverbsSecond,portNumber);
    }
    }

    if(CycleCount == 4){
        CycleCount =0;
      }
	} while (requestFromClient.indexOf("quit") < 0 );
	//for this do-while loop, It will keep asking the user for a color until the user inputs the 
	//the word quit, which will end the loop and move on to the next step of the program below.
  //getJoke(userName,"quit",serverName);
	System.out.println ("Connection has ended.");
	

	//These print statements are printed after the user chose to quit, thus ending the client
	//process entirely.
  consoleIn.close();
  //A modification added by me where it closes consoleIn once
  //the user has quitted.  This is to prevent resource leaks.
  }

  void getJoke(String username, String jokeFromClient, String serverName, int jC, int pC, int[] JokeArray, int []ProverbArray, int Portnumber){
    
    /*
     * We write all of the user's input into this symbol table jokeObj to send to
     * the server for processing.
     */
    JokeData jokeObj = new JokeData();
    int port;
    
    try {
    
    jokeObj.userName = username;
    jokeObj.JokeToServer = jokeFromClient;
    jokeObj.JokeIndex = jC;
    jokeObj.ProverbIndex = pC;
    jokeObj.JokeIndexArray = JokeArray;
    jokeObj.ProverbIndexArray = ProverbArray;
    //jokeObj.JokeIndex = CycleCount;

      //This is where we create out output stream to send every object of jokeObj to the server.
      //System.out.println("Port number " + Portnumber);
      Socket sock = new Socket(serverName, Portnumber);
      OutputStream OS = sock.getOutputStream();
      ObjectOutputStream OOS = new ObjectOutputStream(OS); 
      
      OOS.writeObject(jokeObj); 
      //We then write all of the user's input data,jokeObj, into the ObjectOutputStream variable oos.
      

      InputStream InStream = sock.getInputStream();
      //This class is to help gain any input streams from the very same socket we are using.
      //We use this to gain whatever data has been sent by the server and into our client socket.
      ObjectInputStream ois = new ObjectInputStream(InStream);

      JokeData InJokeObject = (JokeData) ois.readObject();
      /*
       * We read all of the data sent by the server using readObject into the variable InJokeObject
       */
      

      /*
       * When the user types quit, they will receive one final message from the server telling
       * them to have a nice day which shows that the connection has ended for good.
       */
      if(jokeFromClient.equals("quit")){
        System.out.println(InJokeObject.messageToClient + "\n");
      }

      /*
       * Any of these integer variables will be incremented by 1  if the user has pressed enter.
       * Only one of them will be will incremented based on the port number that they are using.
       * That number will be sent to the server afterwards.  The
       */
      else{
        System.out.println(InJokeObject.JokeToClient+ "\n");

        /*
         * These two variables are for when the current mode of the server is in joke mode.
         * One of them is for port 4545, the other is for the second server port 4546.
         */
        if(InJokeObject.modeNumber == 0){
          //System.out.println("The current mode right now is jokes" );
          if (portNumber == 4545){
            JokeCount++;
            InJokeObject.JokeIndex = JokeCount;
          }
          else{
            SecondJokeCount++;
            InJokeObject.SecondJokeIndex = SecondJokeCount;
          }
          
        }

        /*
         * These two variables that are being incremented are for when the Server's mode is in proverb
         * mode.  One for the primary mode prot 4545, the other is secondary port 4546
         */
        else{
          
          //System.out.println("The current mode right now is proverbs" );
          if(portNumber == 4545){
            ProverbCount++;
            InJokeObject.ProverbIndex = ProverbCount;
          }
          else{
            SecondProverbCount++;
            InJokeObject.SecondProverbIndex =SecondProverbCount;
          }
          //InObject.ProverbIndex = ProverbCount;
        }
        
        //System.out.println("Test to see if user name is here ... " + InObject.userName);

        //All of these four if statements are for when all four jokes or proverbs in a specific
        //port server has been completed.  They will send the message that the JOKE CYCLE IS COMPLETE
        //or the the PROVERB CYCLE IS COMPLETE.
        if(InJokeObject.JokeIndex == 4){
          System.out.println(InJokeObject.messageToClient+"\n");
          JokeCount = 0;
        }

        if(InJokeObject.SecondJokeIndex == 4){
          System.out.println(InJokeObject.messageToClient + "\n");
          SecondJokeCount = 0;
        }

        if(InJokeObject.ProverbIndex == 4){
          System.out.println(InJokeObject.messageToClient+ "\n");
          ProverbCount = 0;
        }

        if(InJokeObject.SecondProverbIndex == 4){
          System.out.println(InJokeObject.messageToClient + "\n");
          SecondProverbCount = 0;
        }

        //System.out.println("The current admintoServer is " + InObject.adminToServer);
      }
      
      
      sock.close();

    } 
    catch (ConnectException CE){
      //This error message occurs if a server is not running, which will fail the connnection.
      System.out.println("\nJokeServer connection has failed.  It may be because the server itself is not running\n");
      CE.printStackTrace();
      CycleCount = 0;
      //This error message if the hostname or IP address written does not exist 
    } catch (UnknownHostException UH){
      System.out.println("\n No existing host name for "+serverName + "\n"); 
      UH.printStackTrace();
      
    } catch(ClassNotFoundException CNF){
	CNF.printStackTrace();
	//this error message occurs when it fails to find the specific class the program is trying
	//to call for.
    }
     catch (IOException IOE){
    	
      IOE.printStackTrace(); 
      //This error message occurs when I/O operations have failed or been interrupted.
    }
    
    
  
    
  }

  

}

//This is the class where an admin signs in and makes a request for the server to change to either
//Joke or proverbs which will cause the clients to receive either a proverb or joke instead of the other.
class JokeClientAdmin{
  
  //Default port number when arguments have not been type into the command line.
  private static int portNumber = 5050;

  //Boolean variable is used if the admin requests a second server to be connected.
  private static boolean Switch = false;
  
  public static void main(String argv[]) {
    JokeClientAdmin cc = new JokeClientAdmin(argv);
    
    cc.run(argv);
    //cc.run is to help execute the run function below. 
  }

  public JokeClientAdmin(String argv[]) { 
    System.out.println("\nConstructor for the JokeClientAdmin.  Here, you can change the current mode of JokeServer\n");
  }
//The function 'run' is where any inputs made by the client user are sent to the server socket 
//for processing and that same server returns the output data to that client.
  public void run(String argv[]) {
    
      String serverName;
      String secondServer = "";

      /*
       * This occurs if no arguments have been typed into the command line when running the class.
       */
      if (argv.length < 1) {
        serverName = "localhost"; 
        System.out.println("Server One: "+ serverName + ", port 5050");
      }
      else {
        //This part for the first argument typed into the command line.
        serverName = argv[0];
        if (argv.length > 1){
          //This is where if a second argument has been typed into the command line. Once there is
          //This will turn the boolean variable Switch into true which will allow the user
          //to switch ports.
          secondServer = argv[1];
          System.out.println("Server One: "+ serverName + ", port 5050  Server Two: " + secondServer + " port 5051");
          Switch = true;
          
          
        }
        else{
          System.out.println("Server One: "+ serverName + ", port 5050");
        }
        //serverName = "localhost";
        //secondServer = 1;
      }
      //System.out.println(serverName);
	String colorFromClient = "";
	
	Scanner consoleIn = new Scanner(System.in);
	// The variable consoleIn under the class Scanner is used to allow the user to type any inputs.  It can be 
	// used for multiple inputs like user name or colors.  This input will already be open for the
	//input stream thanks to System.in.
	System.out.print("Type a user name for admin client: ");
	System.out.flush ();
	//flush is used to clear the memory buffer system into an output stream.  This is to help
	//improve the performance of the program.
	String userName = consoleIn.nextLine();
	//userName string is where input for the user name is stored. consoleIn.nextLine is used to 
	//type in the input by the user.  Next time when consoleIn.nextline is used, it will
	//be blank to allow a brand new input from the user.
	System.out.println("Welcome " + userName);
  //System.out.println("The current server port you are connecting to is " + portNumber);
  System.out.print("Would you like to change the mode? \nIf yes, simply press enter.  \nIf you want to switch ports, type s.  \nIf you want to quit, type quit: ");
	do {
	  
	  colorFromClient = consoleIn.nextLine();
	  //Here, the variable colorFromclient will change from being an empty string into another word inputed
	  //by the user. In this case, it will be a color word. consoleIn.nextLine() may have been used
	  //for the username, but it start out blank, allowing a brand new input for colorFromClient.
	  
		  //colorFromClient.indexOf("quit") < 0 is to check if the user's input is equal to quit.
		  //If it's not the word quit, then it will execute the function "getColor", using
		  // the local variables of this function, userName, colorFromClient and serverName, as
		  // the arguments for it.
	    //getColor(userName, colorFromClient, serverName);

      //This will occur if no second argument has been added to the command line.
      if (colorFromClient.equalsIgnoreCase("s") && Switch == false){
        System.out.println("No secondary server being used\n");
      }

      /*
       * this will occur if the Switch is equal to true, thus we are allowed to switch port numbers.
       * by typing s into the input console.
       */
      if (Switch == true && colorFromClient.equalsIgnoreCase("s")){
        //This will switch to server port 5051
        if (portNumber == 5050){
          portNumber = 5051;
          System.out.println("Now communicating with: " + secondServer +" ,port " + portNumber);
        }
        //This wil switch to server port 5050
        else{
          portNumber = 5050;
          System.out.println("Now communicating with: " + serverName + " ,port "+portNumber);
        }

        
      }

      /*
       * This will end running the jokeclientadmin class if the user has typed in quit.
       * This is a safe way to prevent any crashes from occur between the server and admin client.
       */
    if(colorFromClient.equals("quit")){
        toggleMode(userName,colorFromClient,serverName,portNumber);
      }
	  
    else if (colorFromClient.equals("")){
      if (portNumber == 5051){
        toggleMode(userName, colorFromClient, secondServer,portNumber);
      }
      else{
        toggleMode(userName, colorFromClient, serverName,portNumber);
      }
      
    }
	} while (colorFromClient.indexOf("quit") < 0 );
	//for this do-while loop, It will keep asking the user for a color until the user inputs the 
	//the word quit, which will end the loop and move on to the next step of the program below.
  //getJoke(userName,"quit",serverName);
	

	//These print statements are printed after the user chose to quit, thus ending the client
	//process entirely.
  consoleIn.close();
  //A modification added by me where it closes consoleIn once
  //the user has quitted.  This is to prevent resource leaks.
  }

  /*This function is where we send the message to toggle the mode of the server. */
  void toggleMode(String username, String responseFromClient, String serverName, int port){
    //This empty symbol table is where we store all of the user's input and the current server
    //names and port numbers.
    JokeData jokeObj = new JokeData();
    

    try {
      // int port;
      // if(Server == 0){
      //   port = 5050;
      // }
      // else{
      //   port = 5051;
      // }

      /*This is where we write all of the users input data to send to the server for a change in
       * mode. The port number is needed to let the server no which port number the admin is connecting with.
      */
      jokeObj.userName = username;
      jokeObj.adminToServer = responseFromClient;
      jokeObj.PortNumber = port;
      Socket sock = new Socket(serverName,port);
      OutputStream OutputStream = sock.getOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(OutputStream); 
      
      oos.writeObject(jokeObj); 
      //We then write all of the user's input data,jokeObj, into the ObjectOutputStream variable oos.
      //This is how we send the client's input data to the server's socket.
      //System.out.println("Connection between server and admin client is successful");

      InputStream InStream = sock.getInputStream();
      //This class is to help gain any input streams from the very same socket we are using.
      //We use this to gain whatever data has been sent by the server and into our client socket.
      ObjectInputStream ois = new ObjectInputStream(InStream);

      JokeData InObject = (JokeData) ois.readObject();
      int mode = InObject.modeNumber;

      /*If the user's input is equal to quit, they receive a message thanking them for using
       * the joke admin server.
       */
      if(responseFromClient.equals("quit")){
        InObject.adminToServer = String.format("%s has quit the admin worker.", jokeObj.userName);
        System.out.println(InObject.messageToAdmin);
      }
      /*
       * These messages will appear depending on the port number.
       */
      else{
        if (port == 5051){
          System.out.println(InObject.messageToAdmin + " for port 4546 of host " + serverName);
        }
        else{
          System.out.println(InObject.messageToAdmin + " for port 4545 of host " + serverName);
        }
        //System.out.println(InObject.messageToAdmin);
        
        oos.writeObject(InObject);
      }
      ;
      
      sock.close();

    } 
    catch (ConnectException CE){
      System.out.println("\nConnection has been refused.  Likly due to the fact that server is not running\n");
      CE.printStackTrace();
      //This error message is triggered if attempts of connecting to a socket have failed.
    } catch (UnknownHostException UH){
      System.out.println("\nNo such host exists, try another host name, or type carefully\n"); 
      UH.printStackTrace();
      //this error message is triggered if the IP address of a host is not determined.
    } catch(ClassNotFoundException CNF){
	CNF.printStackTrace();
	//this error message occurs when it fails to find the specific class the program is trying
	//to call for.
    }
     catch (IOException IOE){
    	
      IOE.printStackTrace(); 
      //This error message occurs when I/O operations have failed or been interrupted.
    }
    
    
  
    
  }
}
//This class is used mostly for the server and client socket to store specific values such as the user name,
//color retrieved, color being sent, and even a string message for the client. Its also serialized
//to send the data from an input stream to an output stream, which is needed for the server socket,
// to send data to the client socket and vice versa.


class JokeData implements Serializable{
  String userName;
  String messageToClient;
  String messageToAdmin;
  String adminToServer;
  String JokeToServer;
  String JokeToClient;
  int PortNumber;
  int modeNumber;
  int JokeIndex;
  int ProverbIndex;
  int SecondJokeIndex;
  int SecondProverbIndex;
  int JokeCycleNumber;
  int ProverbCycleNumber;
  int [] JokeIndexArray;
  int [] ProverbIndexArray;
}
//JokeWorker class's main purpose is to create mutiple threads, which are multiple processes,
// being run at the same time for the server socket to send data to mutiple client sockets at the
//same time.  This is where most of the server socket's data is received from the client and
//new data is sent to the client in return. It's where the jokes and proverbs are stored and
//ready to be sent to the client.

class JokeWorker extends Thread{
  Socket sock;
  
  JokeWorker (Socket s) {sock = s;}

  public void run(){
    InputStream InStream;
    int jokeNumber = 0;
    int proverbNumber = 0;

    try {

      //We get all of the data sent by the client here.  All of that into InObject.  
      InStream = sock.getInputStream();
      ObjectInputStream ObjectIS = new ObjectInputStream(InStream);
      JokeData InObject = (JokeData) ObjectIS.readObject();

      //This is where we set up the output stream to send data to the client.  To send it, do write object.
      OutputStream outStream = sock.getOutputStream();
      ObjectOutputStream objectOS = new ObjectOutputStream(outStream);

      //InObject.JokeToClient = ("Welcome! Here's a joke for you client!");

      //InObject.modeNumber = 0;
      //System.out.println("Current mode number is " + modeNumber.GetMode());

      System.out.println("\n THE CLIENT:" + InObject.userName + "\n");
      
      
      
      if(JokeServer.serverMode.equals("joke")){
        InObject.modeNumber = 0;
      }
      else{
        InObject.modeNumber = 1;
      }

      /*If the user has typed quit, the server gets notified of that specific client and send them
       * a farewell message to them.
       */
      if(InObject.JokeToServer.equals("quit") || InObject.JokeToServer.equals("Quit")){
        String nameofClient = InObject.userName;
        System.out.printf("%s has just quit the server.\n", nameofClient);
        //System.out.println();
        InObject.messageToClient = String.format("Have a nice day %s", InObject.userName);
        objectOS.writeObject(InObject); 
      }
      else{
        //System.out.println("Client's Joke request: " + InObject.JokeToServer);
        System.out.println("Sending " + JokeServer.serverMode +" to " + InObject.userName);
        String nameofClient = InObject.userName;
        //int cycleIndex = InObject.JokeIndex;
      
        /*This is where we get the jokes by executing the function of the same name, if the mode is
         * currently in joke mode.
         */
        if(JokeServer.serverMode.equals("joke")){
          //jokeNumber++;
          InObject.JokeToClient = jokes(nameofClient, InObject.JokeIndex, InObject.JokeIndexArray);
          
        }
        /*This is when the mode is in proverb mode and we execute the proverbs function to get
         * a specific proverb.
         */
        else{
          //proverbNumber++;
          InObject.JokeToClient = proverbs(nameofClient,InObject.ProverbIndex, InObject.ProverbIndexArray);
          
        }

        /*These two if statements will be true if the client has reached the last joke or proverb in
         * the cycle.  It will notify the server that the client's cycle is complete as well as send the
         * message to the client as well.
         */
        
        if(InObject.JokeIndex == 3){
          System.out.println("JOKE CYCLE COMPLETE for " +InObject.userName);
          InObject.messageToClient = String.format("JOKE CYCLE COMPLETE");
        }
        if(InObject.ProverbIndex == 3){
          System.out.println("PROVERB CYCLE COMPLETE for " +InObject.userName);
          InObject.messageToClient = String.format("PROVERB CYCLE COMPLETE");
        }
        objectOS.writeObject(InObject); 
      }

     
      
      sock.close();
      


    } catch (IOException e) {
      
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      
      e.printStackTrace();
    }
  
  }

  /*This functions purpose is that it creates a string format to send back to the client.  It contains
   * labels for the current joke number and a list of jokes to choose from.  It also includes the
   * user's name as an argument for the string format, uses JokeArray which contains the list of
   * indices to receive a joke in that order sent by the client.Cycle count is the current order of
   * the cycle the user is currently on which is used to get a specific label.
   */

  String jokes(String username,int cycleCount, int[] JokeArray){
    //String result = "";
    String []jokeArray ={"JA","JB","JC", "JD"};
    int length = JokeArray.length;
    String JokeNumber = jokeArray[cycleCount];

    String [] JokeList = {
      "Why did the chicken cross the road. To get to the other side!",
      "I invented a new word! Plagiarism!",
      "Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.",
      "Did you hear about the actor who fell through the floorboards? He was just going through a stage."
    };

    /*This is where the you get a specific joke from the jokelist from the JokeArray's order */
    String Joke = "";
    for(int i = 0; i < length; i ++){
      if (JokeArray[i] == cycleCount){
        Joke = JokeList[i];
      }
    }
    
    /*This is when the server is running on the secondary port of 4546. */
    if(JokeServer.SecondServer == true){
      String result = String.format("<S2> %s %s: %s", JokeNumber, username,Joke);
      return result;
    }
    
    String result = String.format("%s %s: %s", JokeNumber, username,Joke);
    return result;
    
    
		
  }
  /*This functions purpose is that it creates a string format to send back to the client.  It contains
   * labels for the current proverb number and a list of proverbs to choose from.  It also includes the
   * user's name as an argument for the string format, uses ProverbIndexArray which contains the list of
   * indices to receive a proverb in that order sent by the client. Cycle count is the current order of
   * the cycle the user is currently on which is used to get a specific label.
   */
  String proverbs(String username, int cycleCount, int [] ProverbIndexArray){
    String []proverbArray = {"PA","PB","PC","PD"};
    String ProverbNumber =  proverbArray[cycleCount];
    int length = ProverbIndexArray.length;
    String []ProverbList = {
      "Actions speak louder than words",
      "Better late than never.",
      "Look before you leap.",
      "All good things must come to an end."
    };
    //String Proverb = ProverbList[cycleCount];

    String Proverb = "";
    for(int i = 0; i < length; i ++){
      if (ProverbIndexArray[i] == cycleCount){
        Proverb = ProverbList[i];
      }
    }

    if(JokeServer.SecondServer == true){
      String result = String.format("<S2> %s %s: %s", ProverbNumber, username, Proverb);
      return result;
    }
    String result = String.format("%s %s: %s", ProverbNumber, username, Proverb);
    return result;
  }
}

/*This thread is created after connection with the client admin was a success.  This is we change
 * the mode of the server so that the client can receive either the joke or proverb instead of the other.
 * It runs on an aysynchronus thread before the Jokeworker thread in the main section of the server.
 */

class AdminWorker extends Thread{
  Socket sock;
  AdminWorker (Socket s) {sock = s;}
  
  public void run(){
    InputStream InStream;
    
    try{
      /*We set up the threads input stream and read every data from the client admin. */
      InStream = sock.getInputStream();
      ObjectInputStream ObjectIS = new ObjectInputStream(InStream);
      JokeData InObject = (JokeData) ObjectIS.readObject();
      /*Not only do we use InObject to read what was sent by the admin, we also use the same
       * variable to send data back to the admin as well.  That will be under different
       * serliable names instead of stuff like username or admin to server.
       */

      /*The thread sends it's data to the client with this output stream set up. */
      OutputStream outStream = sock.getOutputStream();
      ObjectOutputStream objectOS = new ObjectOutputStream(outStream);
      
      
      System.out.println("\nCLIENT ADMIN:"+ InObject.userName+ "\n");

      /*This occurs if the client admin has quit.  The server gets notified and the admin receives 
       * a farewell message.
       */
      if (InObject.adminToServer.equals("quit")){
        System.out.printf("%s has quit the admin.\n", InObject.userName);
        InObject.messageToAdmin = String.format("Thank you for using the admin.  The current mode is now %s", JokeServer.serverMode);
      }

      /*This is where the client request for a change in the mode occurs.  If the public static variable
       * is in joke mode, then it changes into proverb and vice versa.
       */
      else{
        System.out.printf("%s has switched the mode.\n", InObject.userName);
        if(JokeServer.serverMode.equals("joke")){
          JokeServer.serverMode = "proverb";
          
        }
        else{
          JokeServer.serverMode = "joke";
        }
        
        System.out.println("The mode is now "+ JokeServer.serverMode +" for port number " + InObject.PortNumber);
        //InObject.modeNumber = ModeInstance.GetMode();
        //InObject.modeNumber = ModeInstance.SetMode();
        InObject.messageToAdmin = String.format("\nThe current mode is now %s", JokeServer.serverMode);
        //InObject.modeNumber = ModeInstance.GetMode();
      }

      /*We then send the messages back with InObject to notify the admin the current mode of the server
       * from a specific port number.
       */

      objectOS.writeObject(InObject); 
      
    }

    catch (IOException e) {
      
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      
      e.printStackTrace();
    }
  }
  public int setMode(){
    return 0;
  }
}
//This is where the server socket is created.  Only one will be created unlike the client sockets.

//This is the asynchonrus thread to run the port 5050 or 5051 for the admin client.  This 
//is important so that the server can run the port for AdminClient while running the other port for JokeClient.
//It allows the port to accept connections from the admin without blocking the other port for the client.
class Adminlooper implements Runnable{
  public static boolean adminControlSwitch = true;

  public void run(){ // RUNning the Admin listen loop
    
    
    int q_len = 6; /* Number of requests for OpSys to queue */
    int port = 5050;  // We are listening at a different port for Admin clients

    //port wil change to 5051 if the JokeServer is trying to run on the second server
    //instead of the primary one.
    if (JokeServer.SecondServer == true){
      port = 5051;
    }
    //int secondport = 5051;
    Socket sock;

    try{
      /*This is where the server waits for the connection to an admin has been called for.  It will
       * start the adminwork thread where the functions of toggling the mode occurs.  Like all
       * accept functions, they continue in a while loop that never ends.
       */
      ServerSocket servsock = new ServerSocket(port, q_len);
      while (adminControlSwitch) {
	// wait for the next ADMIN client connection:
	    sock = servsock.accept();
	    new AdminWorker(sock).start(); 
      }
    }catch (IOException ioe) {System.out.println(ioe);}

}
}

/*
 * This class is the server it self.  It contains the threads that does the tasks for jokeclient and
 * JokeClientAdmin respectively.  It holds the mode, the jokes and proverbs to be sent, and establishes 
 * the connections to client sockets.  It basically does most of the work, while Joke Client and 
 * Client Admins are merely used to send requests for the server to do certain tasks and send that
 * data back to them. 
 */
public class JokeServer {
  
  //This variable is used to define the current mode of the whole server.  It's what used for the 
  //connection between the JokeWorker and AdminWorker threads since it's impossible to make
  //direct communications between the two.  This variable can be reached in both threads and can 
  //be changed  to either joke or proverb.  This change will can occur if the admin worker sends
  //a message to the server for that change.  The admin worker thread reads that message and then
  //does the task of changing the variable into the other mode.  This changed variable can be reached
  //in the joke worker thread in the same exact running process.  The joke worker thread checks 
  //to see what the current status of the variable is, it will then send joke or proverb related 
  //messages to the client.  This changed variable will stay that way until the server stops running.
  //Side note, when starting the JokeServer, it will immediatly be on joke mode as a default. 
  public static String serverMode = "joke";
  

  //This is the variable used to determine if the server want ot run on either primary server
  //or the secondary server.  It will start off as false as a default, which means the server will
  //run on the primary server this way.  To change it to true, an argument that's typed in as "secondary"
  //in the command line to run the server will cahnge this variable to true, which will run with 
  //the secondary server.
  public static boolean SecondServer = false;

    public static void main(String[] args) throws Exception
	  {
		//The q_len is the maximum number of client sockets allowed for the server, while
		//the serverPort is the server socket's ID number.  The ID number is needed for the
		// client socket to access the very same server socket to work with.
	    int q_len = 6; 
      int mode = 0;
      int serverPortWork = 4545;
      
	    
      // for(String val: args){
      //   System.out.println(val);
      //   if (val.equalsIgnoreCase("secondary")){
      //     System.out.println("Argument secondary has been found");
      //   }
      // }

      //This checks if the command argument to running JokeServer has the argument "secondary" in it.
      //If it does, then the severPortWork will change to 4546 for the client class.

      //System.out.println("Argument secondary has been found");
      if (args.length == 1){
        //System.out.println("Argument secondary has been found");
        for(String val: args){
          if (val.equalsIgnoreCase("secondary")){
          System.out.println("\nSecondary server is now running");
          SecondServer = true;
          serverPortWork = 4546;
          }
        }
      }
      
      
      
	    Socket sock;
      
      //int secondserverPortWork = 4546;
      JokeData serverObj = new JokeData();
      //serverObj.modeNumber = 0;

      Adminlooper AL = new Adminlooper();
      Thread t = new Thread(AL);
      t.start();
	    
	    System.out.println
	      ("Aaron Seo's Joke Server 1.0 starting up, listening at port " + serverPortWork + ".\n");
	    
	    //This is where the server socket is created. To create one, it requires an ID port number 
	    //for the first argument and maximum number of connections that can be made for the second.
	    ServerSocket servSock = new ServerSocket(serverPortWork, q_len);
      //ServerSocket servSocksecond = new ServerSocket(secondserverPortWork, q_len);
	    System.out.println("ServerSocket awaiting connections..."); 
	    //Here the server socket will wait for a client to connect with it, which will keep running
	    // in a while loop with true being it's argument.
      //Scanner consoleIn = new Scanner(System.in);
      //String SwitchServer;
	    while (true) { 
	    	//the accept function is what allows the connection between the client and server
	    	// sockets.  This method will halt the whole process, until a connection with
        // a client socket has been made.
        
        
        
        //System.out.println("Primary server starting");
        sock = servSock.accept();
      
	         
	      System.out.println("Connection from " + sock); 
	      
        new JokeWorker(sock).start();
	    }
	  }
}
/*

JokeLog text file outputs:

...This is the joke client running with two servers.  First 8 outputs shows the jokes being unique
to each other in each set of 4.  They are also in different when a cycle is complete.  The next
8 outputs show them in proverb mode.  Follwing that are eight  outputs that are interleaving
between jokes and proverbs.  The rest shows the outputs from different servers when.  S is being
typed to switch between servers.


This is the Joke client constructor if you want to use it.

Server one: localhost, port 4545  Server two: localhost port 4546
Enter your name: Aaron
Hi Aaron
Do you want a joke or proverb?
Just press enter for one.
 To switch ports, press 's'. or type quit to end:
JA Aaron: I invented a new word! Plagiarism!


JB Aaron: Why did the chicken cross the road. To get to the other side!


JC Aaron: Did you hear about the actor who fell through the floorboards? He was just going through a stage.


JD Aaron: Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.

JOKE CYCLE COMPLETE


JA Aaron: Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.


JB Aaron: Did you hear about the actor who fell through the floorboards? He was just going through a stage.


JC Aaron: Why did the chicken cross the road. To get to the other side!


JD Aaron: I invented a new word! Plagiarism!

JOKE CYCLE COMPLETE


PA Aaron: Better late than never.


PB Aaron: Look before you leap.


PC Aaron: All good things must come to an end.


PD Aaron: Actions speak louder than words

PROVERB CYCLE COMPLETE


PA Aaron: Better late than never.


PB Aaron: All good things must come to an end.


PC Aaron: Look before you leap.


PD Aaron: Actions speak louder than words

PROVERB CYCLE COMPLETE


PA Aaron: Look before you leap.


PB Aaron: Better late than never.


JA Aaron: Why did the chicken cross the road. To get to the other side!


JB Aaron: Did you hear about the actor who fell through the floorboards? He was just going through a stage.


JC Aaron: I invented a new word! Plagiarism!


JD Aaron: Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.

JOKE CYCLE COMPLETE


PC Aaron: All good things must come to an end.


PD Aaron: Actions speak louder than words

PROVERB CYCLE COMPLETE

s
Now communicating with: localhost, port 4546


<S2> JA Aaron: Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.


<S2> JB Aaron: Did you hear about the actor who fell through the floorboards? He was just going through a stage.

s
Now communicating with: localhost, port 4545


PA Aaron: Actions speak louder than words


PB Aaron: Better late than never.

s
Now communicating with: localhost, port 4546


<S2> JC Aaron: Why did the chicken cross the road. To get to the other side!


<S2> JD Aaron: Hear about a new restraunt called Karma?  There's no menu: You get what you deserve.

JOKE CYCLE COMPLETE


<S2> PA Aaron: Actions speak louder than words


<S2> PB Aaron: Better late than never.


<S2> PC Aaron: Look before you leap.


<S2> PD Aaron: All good things must come to an end.

PROVERB CYCLE COMPLETE

..This is the sample output of Joke Client admin console

Welcome Aaron
Would you like to change the mode?    
If yes, simply press enter.  
If you want to switch ports, type s.  
If you want to quit, type quit: s     
Now communicating with: localhost ,port 5051


The current mode is now proverb for port 4546 of host localhost
s
Now communicating with: localhost ,port 5050



Constructor for the JokeClientAdmin.  Here, you can change the current mode of JokeServer

Server One: localhost, port 5050  Server Two: localhost port 5051
Type a user name for admin client: Aaron
Welcome Aaron
Would you like to change the mode?
If yes, simply press enter.
If you want to switch ports, type s.
If you want to quit, type quit:

The current mode is now proverb for port 4545 of host localhost


The current mode is now joke for port 4545 of host localhost


The current mode is now proverb for port 4545 of host localhost
s
Now communicating with: localhost ,port 5051


The current mode is now proverb for port 4546 of host localhost


...Here's a sample output of the server console

Secondary server is now running
Aaron Seo's Joke Server 1.0 starting up, listening at port 4546.

ServerSocket awaiting connections...
Connection from Socket[addr=/127.0.0.1,port=55653,localport=4546]

 THE CLIENT:Aaron

Sending joke to Aaron
Connection from Socket[addr=/127.0.0.1,port=55659,localport=4546]

 THE CLIENT:Aaron

Sending joke to Aaron
Connection from Socket[addr=/127.0.0.1,port=55670,localport=4546]

 THE CLIENT:Aaron

Sending joke to Aaron
Connection from Socket[addr=/127.0.0.1,port=55672,localport=4546]

 THE CLIENT:Aaron

Sending joke to Aaron
JOKE CYCLE COMPLETE for Aaron

CLIENT ADMIN:Aaron

Aaron has switched the mode.
The mode is now proverb for port number 5051
Connection from Socket[addr=/127.0.0.1,port=55692,localport=4546]

 THE CLIENT:Aaron

Sending proverb to Aaron
Connection from Socket[addr=/127.0.0.1,port=55695,localport=4546]

 THE CLIENT:Aaron

Sending proverb to Aaron
Connection from Socket[addr=/127.0.0.1,port=55696,localport=4546]

 THE CLIENT:Aaron

Sending proverb to Aaron
Connection from Socket[addr=/127.0.0.1,port=55700,localport=4546]

 THE CLIENT:Aaron

Sending proverb to Aaron
PROVERB CYCLE COMPLETE for Aaron



MY D2L JOKESERVER DISCUSSION FORUM POSTINGS:

You said the changes from the admin client are only seen by the server correct.  That means 
you are only missing one half of the puzzle.  Since the server itself knows that the mode has 
changed, now you need to notify the client that the mode has changed.  To do that, you need to 
do it through the server itself with the thread it makes for the joke client, JokeWorker.  One 
easy way for me would be to use a public static variable as an indicator for the server to know
 what the mode is.  This has to be created before the main function that runs the server. This 
 variable is interactable in the JokeWorker and AdminWorker threads and can be changed in them 
 as well.  Try it see if it works for you.


 Here’s how you can use public static variable to communicate between joke client and admin client.  
 First you need to set the public static variable right before the main function in the Jokeserver. 
 Have it be equal to a default mode like say string variable “joke”. If you want the mode to change,
  have that variable be equal to “proverb”.  This can be done through the admin worker thread, if the
 client admin class has sent a message for that change to occur. Once the public static variable has 
 changed, then use that modified variable as a comparison for the joke worker thread so that it can 
 start sending proverb related messages instead of jokes to the joke client.


 Are we required to switch port numbers when we run either the client or client admins with the second
  arguments in the command line.  Because I already made a feature where you are given the option to 
  switch ports when running the client admin.  That way, you can just immediately switch modes of each
 port server without having to run the client admin again to connect with the other port.  I did
  something similar to my client class where I allow the client the option to switch ports as well.
  We don't need to switch ports in the JokeServer correct.  I really don't see how that can be done
 while maintaining the server connections between the admin and clients.  I just want to know if all 
 that is required. 

 I'm a little confused on the part that says "Finally, (c) modify the code to return a single joke 
 from the your server as soon as the client connects " from step 2.  Does it mean to say that we have
 to return a message from the client to our server? I just want to make sure if that's correct.


 If you are still struggling on forming a communication between the two threads, then try other methods 
 besides toggle code.  There are other ways of doing this.  Try stuff like global variables.  How and
where should you make one?

The joker server class itself does all the work, including holding the jokes and proverbs to be sent to
 the client class.  If you have already figured out how to set up the second asynchronous thread in the
  joker server, then you should know by now that the joke server class itself is where the communications
   between the admin worker and the joker worker threads are connected.  It's where the joke worker
    and admin worker threads are created.  The joke client and the joke client admins are mainly used
 to send requests to the server for certain tasks.  Neither of the classes holds the mode.  Which class
 should be one the one to hold it, and how can it be changed by the client admin worker?  Try thinking 
 on that.

 I just want to be sure if I understand this statement correctly, "Can start JokeServer[s], 
 JokeClient[s], and JokeClientAdmin in any order ".  For example, I ran both the joke clients 
 and jokeclient admin classes before running the joke servers.  Of course, when I tried to run 
 the client and client admins by pressing enter, the connection failed because the joke server 
 wasn't running at that time.  Instead of stopping those two processes, I let them still run and 
 tried enter again, but this time I ran the servers, which made the connection successful.  Is this 
 what it means to start any of those three processes in any order?  Please let me know if I'm doing 
 this correctly.
*/