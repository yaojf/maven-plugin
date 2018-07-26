package com.yaojiafeng.maven.plugin.plugin.tool;

import com.yaojiafeng.maven.plugin.annotations.*;
import com.yaojiafeng.maven.plugin.plugin.bean.DubboApiInfo;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.logging.Log;

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
 * Date: 2018/5/9
 * Time: 上午11:47
 * Description:
 */
@Data
public class DubboApiDocsGenerateTool implements ApiDocsGenerateTool {

    private Log log;

    private String classpath;

    private List<DubboApiInfo> dubboApiInfoList = new ArrayList<>();

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
            if (clazz.isInterface()) {
                Api api = clazz.getAnnotation(Api.class);
                if (api != null) {
                    DubboApiInfo dubboApiInfo = new DubboApiInfo();
                    dubboApiInfo.visitApi(api, clazz);
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        ApiMethod apiMethod = method.getAnnotation(ApiMethod.class);

                        DubboApiInfo.DubboApiMethodInfo dubboApiMethodInfo = dubboApiInfo.visitApiMethod(apiMethod, method);

                        Parameter[] parameters = method.getParameters();
                        if (ArrayUtils.isNotEmpty(parameters)) {
                            for (int i = 0; i < parameters.length; i++) {
                                Parameter parameter = parameters[i];
                                ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
                                dubboApiMethodInfo.visitApiParam(apiParam, parameter, urlClassLoader);
                            }
                        }

                        ApiResponse apiResponse = method.getAnnotation(ApiResponse.class);
                        dubboApiMethodInfo.visitApiResponse(apiResponse, method, urlClassLoader);

                        // 异常信息
                        Throws aThrows = method.getAnnotation(Throws.class);
                        Throw aThrow = method.getAnnotation(Throw.class);
                        dubboApiMethodInfo.visitApiException(aThrows, aThrow);

                        // 声明异常
                        Class<?>[] exceptionTypes = method.getExceptionTypes();
                        if (ArrayUtils.isNotEmpty(exceptionTypes)) {
                            dubboApiMethodInfo.visitExceptionTypes(exceptionTypes);
                        }
                    }
                    dubboApiInfoList.add(dubboApiInfo);
                }
            }
        }
    }

}
