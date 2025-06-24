package com.zkyzn.project_manager.crons;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

/**
 * 文件存储定时任务
 *
 * @author Zhang Fan
 */
@Data
@Slf4j
@Component
public class FileStoreCron {

    // 自动清理文件天数
    @Value("${file.auto-remove.days:3}")
    private Integer autoRemoveDays;

    // 自动清理文件目录
    @Value("${file.auto-remove.baseDirs:}")
    private Set<String> autoRemoveBaseDirs = new HashSet<>();

    @Scheduled(cron = "${file.auto-remove.cron:0 0 2 * * ?}")
    public void removeExpiredFilesTask() {
        log.info("定时任务：开始清除过期文件...");
        LocalDate autoRemoveDate = LocalDate.now().minusDays(autoRemoveDays);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 2. 检查任务延期
        removeExpiredFiles(autoRemoveDate, dateFormatter);

        log.info("定时任务：自动清除过期文件完成，过期文件范围为：{} 之前的数据。", dateFormatter.format(autoRemoveDate));
    }

    @VisibleForTesting
    public List<String> removeExpiredFiles(LocalDate autoRemoveDate, DateTimeFormatter dateFormatter) {
        List<String> deletedFiles = new ArrayList<>();

        for (String autoRemoveBaseDir : autoRemoveBaseDirs) {
            Path baseDir = Paths.get(autoRemoveBaseDir);

            if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) {
                continue;
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir)) {
                for (Path dir : stream) {
                    if (Files.isDirectory(dir)) {
                        String dirName = dir.getFileName().toString();

                        try {
                            LocalDate dirDate = LocalDate.parse(dirName, dateFormatter);

                            if (dirDate.isBefore(autoRemoveDate)) {
                                deleteFilesInDirectory(dir, deletedFiles);
                            }
                        } catch (DateTimeParseException ignored) {
                        }
                    }
                }
            } catch (IOException e) {
                log.error("定时任务：自动清除过期文件出错，baseDir：" + autoRemoveBaseDir, e);
            }
        }
        return deletedFiles;
    }

    private void deleteFilesInDirectory(Path dir, List<String> deletedFiles) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            deletedFiles.add(path.toString());
                        } catch (IOException e) {
                            log.error("定时任务：自动清除过期文件出错<UNK>" + path.toString(), e);
                        }
                    });
        }
    }

}
