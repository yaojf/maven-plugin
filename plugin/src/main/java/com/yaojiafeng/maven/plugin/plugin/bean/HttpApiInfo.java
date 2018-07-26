package com.yaojiafeng.maven.plugin.plugin.bean;

import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import com.yaojiafeng.maven.plugin.plugin.enums.HttpApiParamInEnum;
import com.yaojiafeng.maven.plugin.plugin.util.GenericTypeUtils;
import com.yaojiafeng.maven.plugin.plugin.util.TypeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URLClassLoader;
import java.util.*;

/**
 * User: yaojiafeng
 * Date: 2018/6/7
 * Time: 下午7:43
 * Description:
 */
@Data
public class HttpApiInfo {

    private String name;// 接口名称

    private String desc;// 接口描述

    private String serviceName;// 接口全限定名

    private List<HttpApiMethodInfo> methodInfoList = new ArrayList<>();// 方法列表

    public void visitApi(Class<?> clazz) {
        Api api = clazz.getAnnotation(Api.class);
        if (api != null) {
            this.name = StringUtils.defaultString(api.value(), clazz.getSimpleName());
            this.desc = api.description();
        } else {
            this.name = clazz.getSimpleName();
        }
        this.serviceName = clazz.getName();
    }

    public HttpApiMethodInfo visitApiMethod(RequestMapping requestMapping, Method method) {
        String prefixUri = "";
        if (requestMapping != null) {
            prefixUri = firstUri(requestMapping.value());
        }
        HttpApiMethodInfo httpApiMethodInfo = new HttpApiMethodInfo();

        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation != null) {
            httpApiMethodInfo.setName(apiOperation.value());
            httpApiMethodInfo.setDesc(apiOperation.notes());
        } else {
            httpApiMethodInfo.setName(method.getName());
        }

        // 遍历 GetMapping PostMapping DeleteMapping PatchMapping PutMapping 注解
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);

        if (getMapping != null) {
            httpApiMethodInfo.addMethod(RequestMethod.GET.name());
            httpApiMethodInfo.setUri(prefixUri + firstUri(getMapping.value()));
        } else if (postMapping != null) {
            httpApiMethodInfo.addMethod(RequestMethod.POST.name());
            httpApiMethodInfo.setUri(prefixUri + firstUri(postMapping.value()));
        } else if (deleteMapping != null) {
            httpApiMethodInfo.addMethod(RequestMethod.DELETE.name());
            httpApiMethodInfo.setUri(prefixUri + firstUri(deleteMapping.value()));
        } else if (patchMapping != null) {
            httpApiMethodInfo.addMethod(RequestMethod.PATCH.name());
            httpApiMethodInfo.setUri(prefixUri + firstUri(patchMapping.value()));
        } else if (putMapping != null) {
            httpApiMethodInfo.addMethod(RequestMethod.PUT.name());
            httpApiMethodInfo.setUri(prefixUri + firstUri(putMapping.value()));
        } else if (methodRequestMapping != null) {
            RequestMethod[] requestMethods = methodRequestMapping.method();
            if (requestMethods.length == 0) {
                httpApiMethodInfo.setMethodList(Arrays.asList(RequestMethod.GET.name(),
                        RequestMethod.POST.name(), RequestMethod.DELETE.name(),
                        RequestMethod.PATCH.name(), RequestMethod.PUT.name()));
            } else {
                for (RequestMethod rm : requestMethods) {
                    httpApiMethodInfo.addMethod(rm.name());
                }
            }
            httpApiMethodInfo.setUri(prefixUri + firstUri(methodRequestMapping.value()));
        } else {
            return null;
        }

        methodInfoList.add(httpApiMethodInfo);
        return httpApiMethodInfo;
    }


    private String firstUri(String[] path) {
        if (ArrayUtils.isNotEmpty(path)) {
            return path[0];
        }
        return Constants.EMPTY_STRING;
    }

    @Data
    public static class HttpApiMethodInfo {
        private String name;// 方法名称

        private String desc;// 方法描述

        private String uri;// http uri

        private List<String> methodList = new ArrayList<>(); // http请求method列表

        private Map<String, String> requestHeader = new HashMap<>();// http请求头

        private Set<ApiModelInfo> modelInfoList = new HashSet<>();// 自定义bean

        private List<HttpApiParamInfo> paramInfoList = new ArrayList<>();// 参数信息

        private HttpApiResponseInfo responseInfo;

        public void addMethod(String method) {
            methodList.add(method);
        }

        public void visitApiParam(Parameter parameter, URLClassLoader urlClassLoader) throws ClassNotFoundException {
            HttpApiParamInfo apiParamInfo = new HttpApiParamInfo();
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            ModelAttribute modelAttribute = parameter.getAnnotation(ModelAttribute.class);
            ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
            if (pathVariable != null) {
                apiParamInfo.setIn(HttpApiParamInEnum.PATH.getValue());
                apiParamInfo.setName(pathVariable.value());
                apiParamInfo.setRequired(pathVariable.required());
            } else if (requestParam != null) {
                apiParamInfo.setIn(HttpApiParamInEnum.QUERY.getValue());
                apiParamInfo.setName(requestParam.value());
                apiParamInfo.setRequired(requestParam.required());
            } else if (requestBody != null) {
                apiParamInfo.setIn(HttpApiParamInEnum.BODY.getValue());
                apiParamInfo.setName(RequestBody.class.getName());
                apiParamInfo.setRequired(requestBody.required());
                requestHeader.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            } else if (modelAttribute != null) {
                // 参数拆解，内部属性目前暂定全是基本类型
                Class<?> type = parameter.getType();
                if (TypeUtils.isCustom(type, urlClassLoader)) {
                    Class<?> clazz = type;
                    while (clazz != null) {
                        paramInfoList.addAll(getHttpApiParamInfo(clazz, urlClassLoader));
                        clazz = clazz.getSuperclass();
                    }
                }
                return;
            } else {
                // javax.servlet包的参数排除
                Class<?> type = parameter.getType();
                if (!type.getName().startsWith("javax.servlet")) {
                    // 参数拆解，内部属性目前暂定全是基本类型
                    if (TypeUtils.isCustom(type, urlClassLoader)) {
                        Class<?> clazz = type;
                        while (clazz != null) {
                            paramInfoList.addAll(getHttpApiParamInfo(clazz, urlClassLoader));
                            clazz = clazz.getSuperclass();
                        }
                    }
                }
                return;
            }
            if (apiParam != null) {
                apiParamInfo.setDesc(apiParam.value());
            }

            apiParamInfo.type = GenericTypeUtils.resolve(parameter, urlClassLoader, modelInfoList);

            paramInfoList.add(apiParamInfo);
        }

        /**
         * 只取基本类型
         *
         * @param clazz
         * @param urlClassLoader
         * @return
         */
        private List<HttpApiParamInfo> getHttpApiParamInfo(Class<?> clazz, URLClassLoader urlClassLoader) {
            List<HttpApiParamInfo> httpApiParamInfoList = new ArrayList<>();
            Field[] declaredFields = clazz.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(declaredFields)) {
                for (Field field : declaredFields) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        HttpApiParamInfo apiParamInfo = new HttpApiParamInfo();
                        apiParamInfo.setIn(HttpApiParamInEnum.QUERY.getValue());
                        apiParamInfo.setName(field.getName());
                        apiParamInfo.setType(field.getType().getName());
                        httpApiParamInfoList.add(apiParamInfo);
                    }
                }
            }
            return httpApiParamInfoList;
        }

        public void visitApiResponse(ApiResponse apiResponse, Method method, URLClassLoader urlClassLoader) throws ClassNotFoundException {
            this.responseInfo = new HttpApiResponseInfo();
            if (apiResponse != null) {
                this.responseInfo.setDesc(apiResponse.message());
            }

            this.responseInfo.type = GenericTypeUtils.resolve(method, urlClassLoader, modelInfoList);
        }

        @Data
        public static class HttpApiParamInfo {
            private String name; // 参数名称

            private String desc;// 参数描述

            private boolean required = false;// 参数是否必传

            private String type;// 字段类型，带范型信息

            private String in;// 参数类型  "body" | "query" | "path"
        }

        @Data
        public static class HttpApiResponseInfo {
            private String desc;// 返回值描述

            private String type;// 字段类型，带范型信息

        }

    }

}
