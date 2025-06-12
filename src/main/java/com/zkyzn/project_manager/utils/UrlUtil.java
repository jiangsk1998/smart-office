package com.zkyzn.project_manager.utils;


import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * @ClassName: UrlUtil
 * @Description: Url工具类
 * @author: Mr-ti
 * Date: 2025/6/12 11:28
 */
public class UrlUtil {

    /**
     * 通过相对路径得到完整路径Url
     * @param baseUrl
     * @param relativePathStr
     * @return
     * @throws IOException
     */
    public static URI getUrlByRelativePath(String baseUrl, String relativePathStr) throws IOException {
        return UriComponentsBuilder.fromUriString(baseUrl).buildAndExpand(relativePathStr).toUri();
    }
}
