package com.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
public class ManagebackendApplication implements CommandLineRunner {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    public static void main(String[] args) {
        SpringApplication.run(ManagebackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动时创建上传目录
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            File uploadDir = Paths.get(uploadPath).toFile();
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    System.out.println("上传目录创建成功: " + uploadDir.getAbsolutePath());
                } else {
                    System.err.println("上传目录创建失败: " + uploadDir.getAbsolutePath());
                }
            } else {
                System.out.println("上传目录已存在: " + uploadDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("创建上传目录时出错: " + e.getMessage());
        }
    }
}
