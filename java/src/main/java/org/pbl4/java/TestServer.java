package org.pbl4.java;

import java.io.File;
import java.io.IOException;

public class TestServer {

    public static void main(String[] args) throws IOException {
        String path = "D:\\2024\\cnpm"; // Đường dẫn thư mục
        File directory = new File(path);

        // Gọi hàm in cây thư mục
        printDirectoryTree(directory, 0);
    }

    public static void printDirectoryTree(File dir, int level) {
        if (!dir.exists()) {
            System.out.println("Thư mục không tồn tại: " + dir.getAbsolutePath());
            return;
        }

        // In dấu thụt lề theo cấp độ
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }

        // Tính kích thước thư mục hoặc file
        long size = calculateSize(dir);

        // In tên thư mục hoặc file kèm kích thước
        if (dir.isDirectory()) {
            System.out.println("|-- " + dir.getName() + "  --  " + size + " bytes (Thư mục)");
        } else {
            System.out.println("|-- " + dir.getName() + "  --  " + size + " bytes (File)");
        }

        // Nếu là thư mục, duyệt tiếp các file và thư mục con
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    printDirectoryTree(file, level + 1);
                }
            }
        }
    }

    public static long calculateSize(File file) {
        if (file.isFile()) {
            return file.length(); // Kích thước của file
        }

        long totalSize = 0;

        // Nếu là thư mục, tính tổng kích thước của các file/thư mục con
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                totalSize += calculateSize(subFile); // Đệ quy tính kích thước
            }
        }

        return totalSize;
    }
}
