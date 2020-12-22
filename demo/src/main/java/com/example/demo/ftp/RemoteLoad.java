package com.example.demo.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RemoteLoad {

    public static void main(String[] args) {
        OutputStream out = null;
        try {
            String dir = "C:\\Users\\xuzhaoqian\\AppData\\Roaming\\Microsoft\\Windows\\Network Shortcuts\\grab";
            File dirFile = new File(dir);
            System.out.println(dir + "存在：" + dirFile.exists());
            System.out.println(dir + "可写：" + dirFile.canWrite());
            File[] files = dirFile.listFiles();
            for (File file : files) {
                System.out.println("目录下文件：" + file.getName());
            }
            String path = "C:\\Users\\xuzhaoqian\\AppData\\Roaming\\Microsoft\\Windows\\Network Shortcuts\\grab\\5.txt";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.getName() + "存在：" + file.exists());
            out = new FileOutputStream(file);
            out.write("测试写入".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
