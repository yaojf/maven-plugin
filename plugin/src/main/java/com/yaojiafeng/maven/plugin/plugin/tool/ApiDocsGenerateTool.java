package com.yaojiafeng.maven.plugin.plugin.tool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * User: yaojiafeng
 * Date: 2018/7/24
 * Time: 上午11:31
 * Description:
 */
public interface ApiDocsGenerateTool {

    void setDependencyArtifactsURL(List<URL> dependencyArtifactsURL);

    void generate() throws MalformedURLException, ClassNotFoundException;
}
