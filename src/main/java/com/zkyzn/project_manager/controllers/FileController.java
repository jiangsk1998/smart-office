package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.file.MergeFileReq;
import com.zkyzn.project_manager.stories.FileStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "/file", description = "文件相关")
@RequestMapping("/file")
public class FileController {

    @Value("${file.base.url}")
    private String baseUrl;

    @Resource
    private FileStory fileStory;

    @Operation(summary = "上传至ai附件")
    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> postFileToAiTempFolder(
            @RequestParam("file") MultipartFile formFile
    ) throws IOException {
        var relativePathStr = fileStory.saveToAiTemp(formFile);
        return ResUtil.ok(Paths.get(baseUrl, relativePathStr).toString());
    }

    @Operation(summary = "上传至个人空间附件")
    @PostMapping(value = "/person", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> postFileToPersonTempFolder(
            @RequestParam("file") MultipartFile formFile
    ) throws IOException {
        var relativePathStr = fileStory.saveToPerson(1, formFile);
        return ResUtil.ok(Paths.get(baseUrl, relativePathStr).toString());
    }

    @Operation(summary = "上传至项目临时空间附件")
    @PostMapping(value = "/project/temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> postFileToProjectFolder(
            @RequestParam("file") MultipartFile formFile
    ) throws IOException {
        var relativePathStr = fileStory.saveToProjectTemp(formFile);
        return ResUtil.ok(Paths.get(baseUrl, relativePathStr).toString());
    }

    @Operation(summary = "获取文件")
    @GetMapping(value = "/**")
    public ResponseEntity<StreamingResponseBody> postFileToProjectFolder(
            HttpServletRequest request
    ) {
        // TODO: 可能会有部分越权的内容 需要过滤部分非法字符
        String path = ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
                .replaceFirst("^/file/", "");
        Path relativePath = Paths.get(baseUrl, path);

        File file = relativePath.toFile();
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody body = outputStream -> {
            try (InputStream is = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + URLEncoder.encode(file.getName(),
                                StandardCharsets.UTF_8)
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }

    @Operation(summary = "合并文件")
    @PostMapping(value = "/merge")
    public Result<String> mergedFile(
            @RequestBody MergeFileReq mergeFileReq
    ) throws Exception {
        // TODO: 需要完善检查路径是否符合规范 以及是否符合合并要求的后缀
        var relativePathStr = fileStory.mergeDocs(mergeFileReq.getFiles());
        return ResUtil.ok(Paths.get(baseUrl, relativePathStr).toString());
    }
}
