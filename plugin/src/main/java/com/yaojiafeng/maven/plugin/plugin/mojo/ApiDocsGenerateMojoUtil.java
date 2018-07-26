package com.yaojiafeng.maven.plugin.plugin.mojo;

import com.alibaba.fastjson.JSONObject;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import com.yaojiafeng.maven.plugin.plugin.tool.ApiDocsGenerateTool;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: yaojiafeng
 * Date: 2018/7/24
 * Time: 上午11:27
 * Description:
 */
public class ApiDocsGenerateMojoUtil {

    public static void generate(MavenProject project, ApiDocsGenerateTool apiDocsGenerateTool) throws Exception {
        // 依赖jar类路径
        Set<Artifact> artifacts = project.getArtifacts();
        if (artifacts != null && !artifacts.isEmpty()) {
            List<URL> dependencyArtifactsURL = new ArrayList<>();
            for (Artifact artifact : artifacts) {
                dependencyArtifactsURL.add(artifact.getFile().toURL());
            }
            apiDocsGenerateTool.setDependencyArtifactsURL(dependencyArtifactsURL);
        }

        apiDocsGenerateTool.generate();
    }

    public static void genJsonFile(List apiInfoList, MavenProject project, File jsonFile) throws Exception {
        if (!CollectionUtils.isEmpty(apiInfoList)) {
            JSONObject json = new JSONObject();
            json.put(Constants.DEPENDENCY_KEY, ApiDocsGenerateMojoUtil.genDependency(project));
            json.put(Constants.SERVICES_KEY, apiInfoList);

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8"));
            writer.write(json.toJSONString());
            writer.flush();
            writer.close();
        }
    }

    public static void genJsonFile(List apiInfoList, MavenProject project, File jsonFile, String springApplicationName) throws Exception {
        if (!CollectionUtils.isEmpty(apiInfoList)) {
            JSONObject json = new JSONObject();
            json.put(Constants.DEPENDENCY_KEY, ApiDocsGenerateMojoUtil.genDependency(project));
            json.put(Constants.SERVICES_KEY, apiInfoList);
            json.put(Constants.SPRING_APPLICATION_NAME, springApplicationName);

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8"));
            writer.write(json.toJSONString());
            writer.flush();
            writer.close();
        }
    }

    public static JSONObject genDependency(MavenProject project) {
        JSONObject dependency = new JSONObject();
        dependency.put("groupId", project.getGroupId());
        dependency.put("artifactId", project.getArtifactId());
        dependency.put("version", project.getVersion());
        return dependency;
    }

}
