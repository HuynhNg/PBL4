package org.pbl4.java;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Paths;

public class MyClient {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    public void  startClient() {
        try {
            socket = new Socket("127.0.0.1", 3000);
            dis = new DataInputStream(socket.getInputStream()); 
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Err connect to Server: " +  e.getMessage());
        }
    }
    public static void main(String[] args) {
        MyClient  client = new MyClient();
        client.startClient();
//        client.Register();
//        client.GetFile();
//        client.GetData();
//        client.UpdateData();
//        client.UploadFile();
        String savePath = "D:\\2024\\PBL4\\FileData\\received_file.zip";
        client.ReceiveFile(savePath);
//        client.GetAllGuest();
    }
    public void Login(){
        try {
            dos.writeUTF("Login");
            dos.writeUTF("102220011");
            dos.writeUTF("123");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            System.out.println("Login Err: " + e.getMessage());
        }
    }
    public void Register(){
        try {
            dos.writeUTF("Register");
            dos.writeUTF("102220050");
            dos.writeUTF("Nguyen Van D");
            dos.writeUTF("22T_DT2");
            dos.writeUTF("123");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetFile() {
    	try {
            dos.writeUTF("GetInformation");
            dos.writeUTF("102220024");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetData() {
    	try {
            dos.writeUTF("GetData");
            dos.writeUTF("102220024");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetAllGuest() {
    	try {
            dos.writeUTF("GetAllGuest");
            dos.writeUTF("1");
    		
//            dos.writeUTF("AddGuest");
//            dos.writeUTF("102220050");
//            dos.writeUTF("1");
            
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void UpdateData() {
    	try {
            dos.writeUTF("UpdateData");
            dos.writeUTF("102220024");
            dos.writeUTF("80.8");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void UploadFile() {
    	try {
            dos.writeUTF("UploadFile");
            dos.writeUTF("102220024");
            dos.writeUTF("a.docx");
            dos.writeUTF("80.8");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void ReceiveFile(String savePath) {
        try {
        	dos.writeUTF("DownloadFile");
        	dos.writeUTF("1");
            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(Paths.get(savePath).toString());
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            
            fos.flush();
            fos.close();
            System.out.println("File đã được nhận và lưu tại: " + savePath);
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
        }
    }
}

