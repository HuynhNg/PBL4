package org.pbl4.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server is on port 3000...");

        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " Client connected");
                Test clientHandler = new Test(socket);
                clientHandler.start();
            } catch (IOException e) {
                System.out.println("Error while accepting client connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

class Test extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Test(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            // Đọc yêu cầu từ client
            String message = dis.readUTF();
            System.out.println("Received from client: " + message);

            // Xử lý yêu cầu "UploadFile"
            if ("UploadFile".equals(message)) {
                UploadFile();
            }

        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng kết nối sau khi xử lý xong
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public void UploadFile() {
        try {
            // Đọc thông tin file từ client
            System.out.println("Đang chờ nhận thông tin file...");
            String MSSV = dis.readUTF(); // MSSV
            System.out.println("MSSV: " + MSSV);
            String fileName = dis.readUTF(); // Tên file
            System.out.println("Tên file: " + fileName);
            long fileSize = Long.parseLong(dis.readUTF()); // Kích thước file
            System.out.println("Kích thước file: " + fileSize + " bytes");

            String savePath = "D:\\2024\\PBL4\\FileData\\" + fileName;
            System.out.println("Đang xử lý file: " + fileName);

            // Bắt đầu nhận file
            System.out.println("Bắt đầu nhận file và lưu tại: " + savePath);
            try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                 FileOutputStream fos = new FileOutputStream(savePath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[8192]; // Đồng bộ với buffer client
                long totalBytesRead = 0;
                int bytesRead;

                while (totalBytesRead < fileSize && (bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    System.out.println("Đã nhận được: " + totalBytesRead + " / " + fileSize + " bytes");
                }

                bos.flush();

                if (totalBytesRead == fileSize) {
                    System.out.println("Đã nhận đầy đủ file và lưu tại: " + savePath);
                    dos.writeUTF("Upload successful");
                } else {
                    System.out.println("Cảnh báo: File nhận không đầy đủ! Đã nhận: " + totalBytesRead + " / " + fileSize);
                    dos.writeUTF("Upload incomplete");
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
            e.printStackTrace();
            try {
                dos.writeUTF("Upload failed");
            } catch (IOException ex) {
                System.out.println("Lỗi khi gửi phản hồi lỗi: " + ex.getMessage());
            }
        }
    }
}
