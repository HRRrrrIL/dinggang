package com.dinggang.crm.common.config;

import com.dinggang.crm.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 简化版：本地存储，生产环境应替换为MinIO/OSS
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 按日期分目录
        String datePath = LocalDate.now().toString().replace("-", "/");
        File dir = new File(uploadDir + File.separator + datePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID().toString() + ext;
        String filePath = datePath + "/" + fileName;

        // 保存文件
        file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));

        Map<String, String> data = new HashMap<>();
        data.put("fileName", originalName);
        data.put("filePath", filePath);
        data.put("fileUrl", "/uploads/" + filePath);
        data.put("fileSize", String.valueOf(file.getSize()));

        return Result.success(data);
    }
}
