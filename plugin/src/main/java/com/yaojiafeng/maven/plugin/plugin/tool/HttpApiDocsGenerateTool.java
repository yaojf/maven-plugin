package com.yaojiafeng.maven.plugin.plugin.tool;

import com.yaojiafeng.maven.plugin.plugin.bean.HttpApiInfo;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import io.swagger.annotations.ApiResponse;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: yaojiafeng
 * Date: 2018/6/7
 * Time: 下午5:46
 * Description:
 */
@Data
public class HttpApiDocsGenerateTool implements ApiDocsGenerateTool {

    private Log log;

    private String classpath;

    private List<HttpApiInfo> httpApiInfoList = new ArrayList<>();

    private Set<Class> classSet = new HashSet<>();

    private List<URL> dependencyArtifactsURL = new ArrayList<>();

    @Override
    public void generate() throws MalformedURLException, ClassNotFoundException {
        dependencyArtifactsURL.add(new File(classpath).toURL());

        URLClassLoader urlClassLoader = new URLClassLoader(dependencyArtifactsURL.toArray(new URL[dependencyArtifactsURL.size()]), Thread.currentThread().getContextClassLoader());

        File searchFile = new File(classpath);

        doGenerate(searchFile, urlClassLoader);
    }

    private void doGenerate(File searchFile, URLClassLoader urlClassLoader) throws ClassNotFoundException {
        if (searchFile.isDirectory()) {
            File[] files = searchFile.listFiles();
            if (ArrayUtils.isNotEmpty(files)) {
                for (File file : files) {
                    doGenerate(file, urlClassLoader);
                }
            }
        } else if (searchFile.isFile() && searchFile.getPath().endsWith(Constants.CLASS_SUFFIX)) {
            String clazzName = searchFile.getPath().substring(classpath.length() + 1).replace("/", ".");
            Class<?> clazz = Class.forName(clazzName.substring(0, clazzName.length() - Constants.CLASS_SUFFIX.length()), true, urlClassLoader);
            // 只识别Controller类或接口
            if (AnnotationUtils.findAnnotation(clazz, Controller.class) != null) {
                HttpApiInfo httpApiInfo = new HttpApiInfo();
                httpApiInfo.visitApi(clazz);
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    HttpApiInfo.HttpApiMethodInfo httpApiMethodInfo = httpApiInfo.visitApiMethod(requestMapping, method);
                    if (httpApiMethodInfo != null) {
                        Parameter[] parameters = method.getParameters();
                        if (ArrayUtils.isNotEmpty(parameters)) {
                            for (int i = 0; i < parameters.length; i++) {
                                httpApiMethodInfo.visitApiParam(parameters[i], urlClassLoader);
                            }
                        }

                        ApiResponse apiResponse = method.getAnnotation(ApiResponse.class);
                        httpApiMethodInfo.visitApiResponse(apiResponse, method, urlClassLoader);
                    }
                }
                httpApiInfoList.add(httpApiInfo);
            }
        }
    }

}
