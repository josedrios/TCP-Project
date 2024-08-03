import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class second {
    public static void main(String[] args) throws Exception{
        int entryValue;
        String clientMessage;
        String serverResponse;
        String usernameList;
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            showOptions();
            entryValue = getUserInput(fromUser);
            switch (entryValue) {
                case 0:
                    System.out.println("Connecting...");
                    break;
                case 1:
                case 2:
                case 3:
                    System.out.println("Error: Not connected to SERVER (Select OPTION: 0 to connect to SERVER)");
                    break;
                case 4:
                    System.out.println("Exiting program now...");
                    System.exit(0);
            }
            if(entryValue == 0){
                break;
            }
        }
        
        Socket clientSideSocket = new Socket("127.0.0.1", 6789);
        System.out.println("Successfully connected to SERVER");
        DataOutputStream toServer = new DataOutputStream(clientSideSocket.getOutputStream()); 
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSideSocket.getInputStream())); 

        toServer.writeByte(entryValue);
        while(true){
            System.out.print("Please Enter the username: ");
            String username = fromUser.readLine();
            toServer.writeBytes(username + "\n");
            System.out.print("Please Enter the password: ");
            String password = fromUser.readLine();
            toServer.writeBytes(password + "\n");
            System.out.println();

            if(fromServer.read() == 1){
                System.out.println("Access Granted");
                break;
            }else{
                System.out.println("Access Denied - Username/Password Incorrect");
            }
        }

        while(true){
            showOptions();
            entryValue = getUserInput(fromUser);
            switch (entryValue) {
                case 0:
                    System.out.println("Already successfully connected to SERVER");
                    break;
                case 1:
                    toServer.writeByte(entryValue);
                    usernameList = fromServer.readLine();
                    usernameList = usernameList.replace(",", "\n");
                    // BREAK THE STRING BY DELIMINATOR AND PRINT WITH NEW LINES
                    System.out.println("Usernames from SERVER:");
                    System.out.println(usernameList);
                    break;
                case 2:
                    toServer.writeByte(entryValue);
                    while(true){
                        String username;
                        while(true){
                            System.out.print("Enter a username you want to send a message to: ");
                            username = fromUser.readLine();
                            toServer.writeBytes(username + "\n");
                            if(fromServer.read() == 1){
                                break;
                            }else{
                                System.out.println("Could not find user, enter a valid username");
                            }
                        }
                        System.out.print("Enter the message you want to send: ");
                        username = fromUser.readLine();
                        toServer.writeBytes(username + "\n");
                        System.out.println("Status: Message sent successfully");
                        break;
                    }
                    break;
                case 3:
                    toServer.writeByte(entryValue);
                    System.out.println("Here are your messages: ");
                    while(true){
                        int iterNum = fromServer.read();
                        for (int i = 0; i < iterNum; i++) {
                            System.out.println(fromServer.readLine());
                        }
                        break;
                    }
                    break;
                case 4:
                    System.out.println("Exiting program now...");
                    clientSideSocket.close();
                    System.exit(0);
                    break;
            }
        }
    }

    public static void showOptions(){
        System.out.println("0. Connect to the server");
        System.out.println("1. Get the user list");
        System.out.println("2. Send a message");
        System.out.println("3. Get my messages");
        System.out.println("4. Exit");
        System.out.print("Please enter a choice: ");
    }

    public static int getUserInput(BufferedReader test) throws IOException{
        int entry = 100;
        while(true){
            String input = test.readLine();
            try{
                entry = Integer.parseInt(input);
            }catch(NumberFormatException e){                
            }
            if(entry >= 0 && entry <=4){
                break;
            }else{
                System.out.println("Invalid entry. Enter a value from 0-4.");
                showOptions();
            }
        }
        return entry;
    }
}
    
    