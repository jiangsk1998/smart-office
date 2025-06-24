package com.zkyzn.project_manager.cron;

import com.zkyzn.project_manager.crons.FileStoreCron;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Zhang Fan
 */
public class FileStoreTest {

    public static void main(String[] args) {
        // 生成测试数据
        Path testBaseDir = Paths.get(FileStoreTest.class.getResource("").getPath().substring(1)).getParent().resolve("FileStoreTest.java");

        FileStoreCron fileStoreCron = new FileStoreCron();
        fileStoreCron.setAutoRemoveDays(3);
        fileStoreCron.setAutoRemoveBaseDirs(Set.of(testBaseDir.toString()));

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        List<String> expectedDeletedDirs = new ArrayList<>();

        for (int i = 0; i <= 10; i++) {
            LocalDate date = today.minusDays(i);
            String dirName = date.format(formatter);
            Path dirPath = testBaseDir.resolve(dirName);
            try {
                Files.createDirectories(dirPath);
            } catch (IOException ignored) {}

            List<Path> filesInDir = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                Path filePath = dirPath.resolve("testfile_" + j + ".tmp");
                try {
                    Files.createFile(filePath);
                } catch (IOException ignored) {}
                filesInDir.add(filePath);
            }

            if (date.isBefore(today.minusDays(fileStoreCron.getAutoRemoveDays()))) {
                expectedDeletedDirs.add(dirPath.toString());
                expectedDeletedDirs.addAll(filesInDir.stream().map(Path::toString).toList());
            }
        }

        // 开始测试删除功能
        List<String> deletedDirs = fileStoreCron.removeExpiredFiles(today.minusDays(fileStoreCron.getAutoRemoveDays()), formatter);

        boolean passed = true;
        for (String expectedDeletedDir : expectedDeletedDirs) {
            if (!deletedDirs.contains(expectedDeletedDir)) {
                System.out.println("未正确删除目录或文件：" + expectedDeletedDir);
                passed = false;
            }
        }

        for (String deletedDir : deletedDirs) {
            if (!expectedDeletedDirs.contains(deletedDir)) {
                System.out.println("误删目录或文件：" + deletedDir);
                passed = false;
            }
        }

        assert passed;
    }
}
