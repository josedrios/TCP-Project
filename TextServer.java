import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.BufferOverflowException;

public class TextServer{
    public static void main(String[] args) throws Exception{
        int clientOption;

        // Log in related variables
        String usernameAttempt;
        String passwordAttempt;
        boolean[] loggedIn = {false};
        int logID = -1;

        // Server stored variables
        String[] usernameList = {"Alice", "Bob"};
        String[] passwordList = {"1234", "5678"};
        // Holds messages for respective individuals
        String[][] messages = new String[10][10];

        // Variables coming in from client and out from server
        String clientMessage;
        String serverResponse;

        // Establishing connection
        ServerSocket serverSideSocket = new ServerSocket(6789);
        System.out.println("SERVER is running...");

        while(true){
            Socket connectSocket = serverSideSocket.accept();
            System.out.println("CLIENT connected to server");
            System.out.println();

            // Defining variables to use out/to client from the server
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectSocket.getInputStream())); 
            DataOutputStream toClient = new DataOutputStream(connectSocket.getOutputStream());

            // This loop takes the clients option constantly in a loop fashion
            while(true){
                System.out.println("Waiting for CLIENT response...");
                System.out.println();
                clientOption = fromClient.read();
                System.out.println("User's choice is " + clientOption);
                System.out.println();

                switch(clientOption){
                    case 0:
                        // Log in option
                        while (true) {
                            usernameAttempt = fromClient.readLine();
                            passwordAttempt = fromClient.readLine();

                            // Confirm proper log in information loop
                            for(int i=0; i < usernameList.length; i++){
                                if(usernameList[i].equals(usernameAttempt) && passwordList[i].equals(passwordAttempt)){
                                    System.out.println("Username: " + usernameAttempt);
                                    System.out.println("Password: " + passwordAttempt);
                                    System.out.println("Access Granted");
                                    System.out.println();
                                    loggedIn[0] = true;
                                    logID = i;
                                    toClient.writeByte(1);
                                    break;
                                }
                            }
                            if(loggedIn[0] == true){
                                loggedIn[0] = false;
                                break;
                            }else{
                                toClient.writeByte(0);
                            }
                        }
                        break;
                    case 1:
                        // Server username list option
                        System.out.println("Returning list of users...");
                        System.out.println();
                        for (int i = 0; i < usernameList.length; i++) {
                            System.out.println(usernameList[i]);
                        }
                        System.out.println();
                        serverResponse = String.join(",", usernameList);
                        toClient.writeBytes(serverResponse + "\n");
                        break;
                    case 2:
                        // Send a message to a user option
                        boolean foundUser = false;
                        while (true) {
                            clientMessage = fromClient.readLine();
                            System.out.println();
                            for (int i = 0; i < usernameList.length; i++) {
                                if(clientMessage.equals(usernameList[i])){
                                    toClient.writeByte(1);
                                    foundUser = true;
                                    clientMessage = fromClient.readLine();
                                    for(int j=0; j >= 0;j++){
                                        if (messages[i][j] == null) {
                                            messages[i][j] = usernameList[logID] + ": " + clientMessage;
                                            System.out.println("Received a message from " + usernameList[logID]);
                                            System.out.println();
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            if(foundUser == false){
                                System.out.println("User not found");
                                System.out.println();
                                toClient.writeByte(0);
                            }else{
                                break;
                            }
                        }
                        break;
                    case 3:
                        // Show me my messages option
                        int iterations = 0;
                        for(int i = 0; i >= 0; i++){
                            if(messages[logID][i] != null){
                                iterations++;
                            }else{
                                break;
                            }
                        }
                        toClient.writeByte(iterations);
                        for (int i = 0; i < iterations; i++) {
                            toClient.writeBytes(messages[logID][i]+"\n");
                        }
                        System.out.println("Returning messages for " + usernameList[logID]);
                        System.out.println();

                        break;
                    default:
                        System.out.println(usernameList[logID] + " logged out");
                        System.out.println();
                        connectSocket.close();
                }
                if(clientOption < 0 || clientOption >= 4){
                    break;
                }
            }
        }
    }
}