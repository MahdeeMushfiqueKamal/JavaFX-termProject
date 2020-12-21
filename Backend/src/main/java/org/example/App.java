package org.example;

import SharedClass.Car;
import SharedClass.FileOperation;
import SharedClass.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ServerSocket ss = null;
        
        //UserMap
        final HashMap<String, String> userMap;
        userMap = new HashMap<>();
        userMap.put("user-a", "a");
        userMap.put("user-b", "b");
        userMap.put("user-c", "c");
        userMap.put("user-d", "d");
        userMap.put("user-e", "e");

        //Entire Car List
        final List<Car> lst = new ArrayList();
        //File Operation
        FileOperation.initialize(lst);
        for(Car x: lst){
            x.DisplayInfo();
        }
        System.out.println("File reading complete");

        try{
            ss = new ServerSocket(55555);
            System.out.println("Waiting for Client..");
        }
        catch(IOException e){
            e.printStackTrace();
        }
        while(true){
            try{
                final Socket sckt = ss.accept();
                System.out.println("Client Joined");
                 new Thread(){
                     @Override
                     public void run() {
                         try {
                             InputStream is = sckt.getInputStream();
                             OutputStream os = sckt.getOutputStream();

                             ObjectOutputStream oos= new ObjectOutputStream(os);
                             ObjectInputStream objectInputStream = new ObjectInputStream(sckt.getInputStream());

                             User user = (User) objectInputStream.readObject();

                             System.out.println("User Object Received" + user.getUserName() + " Password: "+ user.getPassword());
                             //authentication
                             if(user.getUserName().equals("viewer")){
                                 //client is a viewer.

                                 oos.writeObject("viewer detected");
                                 oos.flush();


                                 oos.writeObject(lst);
                                 oos.flush();

                             }
                             else {
                                 String passwordfromhashmap = userMap.get(user.getUserName());
                                 user.setstatus(user.getPassword().equals(passwordfromhashmap));

                                 if(user.getstatus()){
                                     //client is a authenticated manufacturer
                                     oos.writeObject("manufacturer detected");
                                     oos.flush();
                                 }
                                 else{
                                     //client entered wrong username or password
                                     oos.writeObject("wrong authentication");
                                     oos.flush();
                                     run();
                                 }

                             }



                         }
                         catch (IOException | ClassNotFoundException e) {
                             e.printStackTrace();
                         }
                     }
                 }.start();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
