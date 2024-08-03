import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.BufferOverflowException;

public class main{
    public static void main(String[] args) throws Exception{
        int clientOption;

        String usernameAttempt;
        String passwordAttempt;
        boolean[] loggedIn = {false};
        int logID = -1;

        String[] usernameList = {"Alice", "Bob"};
        String[] passwordList = {"1234", "5678"};
        String[][] messages = new String[usernameList.length][usernameList.length];

        

        String clientMessage;
        String serverResponse;

        ServerSocket serverSideSocket = new ServerSocket(6789);
        System.out.println("SERVER is runnning...");

        while(true){
            Socket connectSocket = serverSideSocket.accept();
            System.out.println("CLIENT connected to server");
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectSocket.getInputStream())); 
            DataOutputStream toClient = new DataOutputStream(connectSocket.getOutputStream());

            while(true){
                System.out.println("Waiting for CLIENT response...");
                clientOption = fromClient.read();
                System.out.println("User's choice is " + clientOption);
                switch(clientOption){
                    case 0:
                        while (true) {
                            usernameAttempt = fromClient.readLine();
                            passwordAttempt = fromClient.readLine();
                            for(int i=0; i < usernameList.length; i++){
                                if(usernameList[i].equals(usernameAttempt) && passwordList[i].equals(passwordAttempt)){
                                    System.out.println("Log in successful");
                                    loggedIn[0] = true;
                                    logID = i;
                                    toClient.writeByte(1);
                                    break;
                                }
                            }
                            if(loggedIn[0] == true){
                                break;
                            }
                        }
                        break;
                    case 1:
                        serverResponse = String.join(",", usernameList);
                        toClient.writeBytes(serverResponse + "\n");
                        break;
                    case 2:
                        System.out.println("OPTION 2 SELECTED");
                        boolean foundUser = false;
                        while (true) {
                            clientMessage = fromClient.readLine();
                            System.out.println("Received Username: " + clientMessage);
                            for (int i = 0; i < usernameList.length; i++) {
                                if(clientMessage.equals(usernameList[i])){
                                    System.out.println("We found the user");
                                    toClient.writeByte(1);
                                    foundUser = true;
                                    clientMessage = fromClient.readLine();
                                    for(int j=0; j >= 0;j++){
                                        if (messages[i][j] == null) {
                                            messages[i][j] = clientMessage;
                                            break;
                                        }
                                    }
                                    break;
                                }else{
                                    System.out.println("SKIPPED");
                                }
                            }
                            if(foundUser == false){
                                System.out.println("User not found");
                                toClient.writeByte(0);
                            }else{
                                break;
                            }
                        }
                        break;
                    case 3:
                        toClient.writeByte(messages[logID].length);
                        for (int i = 0; i < messages.length; i++) {
                            toClient.writeBytes(messages[logID][i]+"\n");
                        }
                        break;
                    default:
                        System.out.println("CLIENT disconnected");
                }
                if(clientOption == -1){
                    break;
                }
            }
        }
    }
}