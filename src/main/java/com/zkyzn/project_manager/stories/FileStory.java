package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.so.file.FileResp;
import com.zkyzn.project_manager.utils.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.org.apache.poi.util.IOUtils;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 文件相关故事
 * 作用是将复杂业务都堆积在此处
 */
@Slf4j
@Service
public class FileStory {

    @Value("${file.base.url}")
    private String baseUrl;

    @Value("${file.root.path:./}")
    private String fileRootPath;


    /**
     * 构建临时文件的相对路径
     *
     * @param folder 目录
     * @param file   文件流
     * @return 返回相对路径
     */
    private String generateRelativeTempPath(
            String folder,
            MultipartFile file
    ) {
        // 生成唯一Id 防止文件同名冲突
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Path filePath = Paths.get(folder, dateString, fileName);
        return filePath.toString();
    }

    /**
     * 将文件存至Ai的临时目录
     *
     * @param file 文件信息
     * @return 存储的文件相对路径
     */
    public String saveToAiTemp(MultipartFile file) throws IOException {
        String relativeTempPath = generateRelativeTempPath("ai", file);
        Path path = Paths.get(fileRootPath, relativeTempPath);
        // 保存文件到本地
        Files.createDirectories(path.getParent()); // 创建目录（如果不存在）
        file.transferTo(path.toFile());
        return relativeTempPath;
    }


    /**
     * 将文件上传至个人空间
     *
     * @param id   用户Id
     * @param file 文件流
     * @return 文件存储相对路径
     */
    public String saveToPerson(Long id, MultipartFile file) throws IOException {
        String timeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timeString + "_" + file.getOriginalFilename();
        String relativeTempPath = Paths.get("person", id.toString(), fileName).toString();
        Path path = Paths.get(fileRootPath, relativeTempPath);

        // 保存文件到本地
        Files.createDirectories(path.getParent()); // 创建目录（如果不存在）
        file.transferTo(path.toFile());
        return relativeTempPath;
    }

    /**
     * 将文件上传至项目临时空间
     *
     * @param file 文件流
     * @return 文件存储相对路径
     */
    public String saveToProjectTemp(MultipartFile file) throws IOException {
        String relativeTempPath = generateRelativeTempPath("project/temp", file);
        Path path = Paths.get(fileRootPath, relativeTempPath);

        // 保存文件到本地
        Files.createDirectories(path.getParent()); // 创建目录（如果不存在）
        file.transferTo(path.toFile());
        return relativeTempPath;
    }

    /**
     * 将临时文件移动至项目目录文件夹下
     *
     * @param id            项目Id
     * @param relativePaths 临时文件目录
     * @return 是否成功
     */
    public boolean moveFilesToProject(Long id, List<String> relativePaths) {
        boolean result = true;

        for (String relativePath : relativePaths) {
            try {
                // 分解原路径各部分
                Path path = Paths.get(relativePath);
                String dateLevelDir = path.getName(path.getNameCount() - 2).toString();
                String fileName = path.getFileName().toString();

                // 构建源路径
                Path sourcePath = Paths.get(fileRootPath, relativePath);
                // 构建目标路径
                Path targetDir = Paths.get(fileRootPath, "project", id.toString(), dateLevelDir);
                Path targetPath = targetDir.resolve(fileName);

                // 创建目标目录
                Files.createDirectories(targetDir);
                // 移动文件
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("移动临时文件至项目目录文件夹下失败：" + relativePath, e);
                result = false;
            }
        }
        return result;
    }

    /**
     * 合并Docs文件
     *
     * @param relativePaths 相对路径集合
     * @return 合并后的临时文件
     */
    public String mergeDocs(List<String> relativePaths) throws Exception {
        String timeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timeString + "_" + "merged.docx";
        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String relativeTempPath = Paths.get("merged", dateString, fileName).toString();
        Path mergedFilePath = Paths.get(fileRootPath, relativeTempPath);
        Files.createDirectories(mergedFilePath.getParent()); // 创建目录（如果不存在）
        WordprocessingMLPackage target = null;
        final File merged = mergedFilePath.toFile();
        int chunkId = 0;

        // 处理每一个doc文件
        for (String path : relativePaths) {
            InputStream is = new FileInputStream(path);
            if (target == null) {
                OutputStream os = new FileOutputStream(merged);
                os.write(IOUtils.toByteArray(is));
                os.close();
                target = WordprocessingMLPackage.load(merged);
            } else {
                MainDocumentPart main = target.getMainDocumentPart();
                AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + chunkId++ + ".docx"));
                afiPart.setContentType(new ContentType(HttpHeaders.CONTENT_TYPE));
                afiPart.setBinaryData(IOUtils.toByteArray(is));
                Relationship altChunkRel = main.addTargetPart(afiPart);
                CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
                chunk.setId(altChunkRel.getId());
                main.addObject(chunk);
            }
            IOUtils.closeQuietly(is);
        }

        if (target != null) {
            target.save(merged);
        }

        return relativeTempPath;
    }

    /**
     * 查询当前用户空间下的文件
     *
     * @param id     用户Id
     * @param folder 用户目录
     * @return 文件列表
     */
    public List<FileResp> dirPersonFolder(Long id, String folder) throws Exception {
        // 验证输入参数
        if (id == null || folder == null || folder.isBlank()) {
            throw new IllegalArgumentException("参数错误: id或folder为空");
        }

        // 构建文件路径
        String relativeTempPath = Paths.get("person", id.toString(), folder).toString();
        Path absolutePath = Paths.get(fileRootPath, relativeTempPath);

        // 检查路径是否存在且是目录
        if (!Files.exists(absolutePath)) {
            return Collections.emptyList();
        }
        if (!Files.isDirectory(absolutePath)) {
            throw new Exception("路径不是目录: " + absolutePath);
        }

        // 流处理资源管理
        try (Stream<Path> stream = Files.list(absolutePath)) {
            return stream.map(path -> {
                try {
                    FileResp file = new FileResp();
                    file.setFileName(path.getFileName().toString());
                    file.setIsDirectory(Files.isDirectory(path));
                    file.setSize(Files.size(path));

                    // 构建URL
                    String relativeFilePath = Paths.get(relativeTempPath, file.getFileName()).toString();
                    URI fileUrl = UrlUtil.getUrlByRelativePath(baseUrl, relativeFilePath);
                    file.setUri(fileUrl.toString());

                    return file;
                } catch (IOException e) {
                    // 文件操作异常时返回部分信息
                    FileResp partialFile = new FileResp();
                    partialFile.setFileName(path.getFileName().toString());
                    partialFile.setIsDirectory(false);
                    partialFile.setSize(-1L);
                    partialFile.setUri("[无法获取完整信息]");
                    return partialFile;
                }
            }).collect(Collectors.toList()); // 确保兼容性
        } catch (IOException | SecurityException e) {
            throw new Exception("无法访问目录: " + absolutePath, e);
        }
    }

    /**
     * 通过相对路径获取完整路径
     * @param path 相对路径
     * @return 绝对路径类
     */
    public Path get(String path) {
        return Paths.get(fileRootPath, path);
    }
}
