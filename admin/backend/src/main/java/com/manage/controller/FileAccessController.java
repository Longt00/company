package com.manage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件访问控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileAccessController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 访问上传的文件（支持Range请求，优化视频播放）
     *
     * @param request HTTP请求
     * @return 文件内容
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> accessFile(HttpServletRequest request) {
        try {
            // 获取请求路径
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String basePath = contextPath + "/api/files/";

            if (!requestURI.startsWith(basePath)) {
                return ResponseEntity.notFound().build();
            }

            String filePath = requestURI.substring(basePath.length());

            // 构建文件完整路径
            Path fullPath = Paths.get(uploadPath, filePath);
            File file = fullPath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("文件不存在：{}", fullPath);
                return ResponseEntity.notFound().build();
            }

            // 获取文件MIME类型
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            long fileLength = file.length();
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);

            // 如果没有Range请求，返回完整文件
            if (rangeHeader == null || rangeHeader.trim().isEmpty()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes") // 告诉浏览器支持Range请求
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // 缓存1年
                        .header(HttpHeaders.EXPIRES, String.valueOf(System.currentTimeMillis() + 31536000000L))
                        .header(HttpHeaders.LAST_MODIFIED, String.valueOf(file.lastModified()))
                        .contentLength(fileLength)
                        .body(resource);
            }

            // 解析Range请求头（格式：bytes=start-end）
            long start = 0;
            long end = fileLength - 1;

            try {
                String range = rangeHeader.replace("bytes=", "");
                String[] ranges = range.split("-");
                
                if (ranges.length > 0 && !ranges[0].isEmpty()) {
                    start = Long.parseLong(ranges[0]);
                }
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
                
                // 验证范围
                if (start > end || start < 0 || end >= fileLength) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                            .build();
                }
            } catch (NumberFormatException e) {
                log.warn("无效的Range请求头：{}", rangeHeader);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                        .build();
            }

            // 使用流式传输，避免大文件占用内存
            final long contentLength = end - start + 1;
            final File finalFile = file;
            final long finalStart = start;
            
            // 创建一个自定义的InputStreamResource，延迟打开文件
            InputStreamResource resource = new InputStreamResource(new java.io.InputStream() {
                private RandomAccessFile raf;
                private long remaining = contentLength;
                private boolean initialized = false;
                
                @Override
                public int read() throws IOException {
                    if (!initialized) {
                        raf = new RandomAccessFile(finalFile, "r");
                        raf.seek(finalStart);
                        initialized = true;
                    }
                    if (remaining <= 0) {
                        return -1;
                    }
                    int result = raf.read();
                    if (result != -1) {
                        remaining--;
                    }
                    return result;
                }
                
                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (!initialized) {
                        raf = new RandomAccessFile(finalFile, "r");
                        raf.seek(finalStart);
                        initialized = true;
                    }
                    if (remaining <= 0) {
                        return -1;
                    }
                    int toRead = (int) Math.min(len, remaining);
                    int bytesRead = raf.read(b, off, toRead);
                    if (bytesRead > 0) {
                        remaining -= bytesRead;
                    }
                    return bytesRead;
                }
                
                @Override
                public void close() throws IOException {
                    if (raf != null) {
                        raf.close();
                    }
                }
            });

            // 返回206 Partial Content
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .header(HttpHeaders.EXPIRES, String.valueOf(System.currentTimeMillis() + 31536000000L))
                    .header(HttpHeaders.LAST_MODIFIED, String.valueOf(file.lastModified()))
                    .contentLength(contentLength)
                    .body(resource);

        } catch (Exception e) {
            log.error("访问文件失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 下载文件
     *
     * @param request HTTP请求
     * @return 文件下载响应
     */
    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        try {
            // 获取请求路径
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String basePath = contextPath + "/api/files/download/";

            if (!requestURI.startsWith(basePath)) {
                return ResponseEntity.notFound().build();
            }

            String filePath = requestURI.substring(basePath.length());

            // 构建文件完整路径
            Path fullPath = Paths.get(uploadPath, filePath);
            File file = fullPath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("文件不存在：{}", fullPath);
                return ResponseEntity.notFound().build();
            }

            // 创建文件资源
            Resource resource = new FileSystemResource(file);

            // 获取文件MIME类型
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 设置响应头（强制下载）
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("下载文件失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取文件信息
     *
     * @param request HTTP请求
     * @return 文件信息
     */
    @GetMapping("/info/**")
    public ResponseEntity<Map<String, Object>> getFileInfo(HttpServletRequest request) {
        try {
            // 获取请求路径
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String basePath = contextPath + "/api/files/info/";

            if (!requestURI.startsWith(basePath)) {
                return ResponseEntity.notFound().build();
            }

            String filePath = requestURI.substring(basePath.length());

            // 构建文件完整路径
            Path fullPath = Paths.get(uploadPath, filePath);
            File file = fullPath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("文件不存在：{}", fullPath);
                return ResponseEntity.notFound().build();
            }

            // 获取文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", file.getName());
            fileInfo.put("filePath", filePath);
            fileInfo.put("fileSize", file.length());
            fileInfo.put("lastModified", file.lastModified());
            fileInfo.put("contentType", Files.probeContentType(fullPath));

            return ResponseEntity.ok(fileInfo);

        } catch (Exception e) {
            log.error("获取文件信息失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}