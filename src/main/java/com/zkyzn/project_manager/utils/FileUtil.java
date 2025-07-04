package com.zkyzn.project_manager.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件操作类
 */
public class FileUtil {

    /**
     * 通过Uri得到本地目录的相对路径
     * @param Uri Uri地址
     * @return 返回数据
     */
    public static String getRelativePathByUri(String Uri) {
        // TODO: 可能会有部分越权的内容 需要过滤部分非法字符
        String absUri = Uri.replaceFirst("^/api/file/", "");
        return URLDecoder.decode(absUri, StandardCharsets.UTF_8);
    }

    /**
     * 通过全路径Url得到本地目录的绝对路径
     * @param url Uri地址
     * @return 返回数据
     */
    public static String getAbsolutePathByUrlAndRootPath(String url, String rootPath){
        // TODO: 可能会有部分越权的内容 需要过滤部分非法字符
        String absUri = url.replaceFirst("^.*?/api/file", rootPath);
        return URLDecoder.decode(absUri, StandardCharsets.UTF_8);
    }

    /**
     * 通过完整路径访问得到文件流
     * @param uri 文件访问地址
     * @return 文件访问流
     */
    public static FileInputStream getFileInputStreamByUri(String uri) throws IOException {
        String absolutePath = getRelativePathByUri(uri);
        return new FileInputStream(getRelativePathByUri(absolutePath));
    }
}
