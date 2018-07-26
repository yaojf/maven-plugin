package com.yaojiafeng.maven.plugin.plugin.bean;

import com.yaojiafeng.maven.plugin.annotations.*;
import com.yaojiafeng.maven.plugin.plugin.util.GenericTypeUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLClassLoader;
import java.util.*;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 上午11:50
 * Description:
 */
@Data
public class DubboApiInfo {

    private String name;// 接口名称

    private String desc;// 接口描述

    private String version;// 接口版本

    private String group;// 接口分组

    private String serviceName;// 接口全限定名

    private List<DubboApiMethodInfo> methodInfoList = new ArrayList<>();// 方法列表

    public void visitApi(Api api, Class<?> clazz) {
        this.name = StringUtils.defaultString(api.name(), clazz.getSimpleName());
        this.desc = api.description();
        this.version = api.version();
        this.group = api.group();
        this.serviceName = clazz.getName();
    }

    public DubboApiMethodInfo visitApiMethod(ApiMethod apiMethod, Method method) {
        DubboApiMethodInfo dubboApiMethodInfo = new DubboApiMethodInfo();
        if (apiMethod != null) {
            dubboApiMethodInfo.name = StringUtils.defaultString(apiMethod.name(), method.getName());
            dubboApiMethodInfo.desc = apiMethod.description();
        } else {
            dubboApiMethodInfo.name = method.getName();
        }
        dubboApiMethodInfo.methodName = method.getName();
        methodInfoList.add(dubboApiMethodInfo);
        return dubboApiMethodInfo;
    }

    @Data
    public static class DubboApiMethodInfo {
        private String name;// 方法名称

        private String desc;// 方法描述

        private String methodName;// 方法名

        private Set<ApiModelInfo> modelInfoList = new HashSet<>();// 自定义bean

        private List<DubboApiParamInfo> paramInfoList = new ArrayList<>();

        private DubboApiResponseInfo responseInfo;

        private Set<DubboExceptionInfo> dubboExceptionInfoList = new HashSet<>();

        public void visitApiParam(ApiParam apiParam, Parameter parameter, URLClassLoader urlClassLoader) throws ClassNotFoundException {
            DubboApiParamInfo dubboApiParamInfo = new DubboApiParamInfo();

            if (apiParam != null) {
                dubboApiParamInfo.name = apiParam.name();
                dubboApiParamInfo.desc = apiParam.description();
                dubboApiParamInfo.required = apiParam.required();
            } else {
                dubboApiParamInfo.name = parameter.getName();
            }

            dubboApiParamInfo.type = GenericTypeUtils.resolve(parameter, urlClassLoader, this.modelInfoList);

            paramInfoList.add(dubboApiParamInfo);
        }

        public void visitApiResponse(ApiResponse apiResponse, Method method, URLClassLoader urlClassLoader) throws ClassNotFoundException {
            this.responseInfo = new DubboApiResponseInfo();
            if (apiResponse != null) {
                this.responseInfo.desc = apiResponse.description();
                this.responseInfo.allowNullValue = apiResponse.allowNullValue();
            }

            this.responseInfo.type = GenericTypeUtils.resolve(method, urlClassLoader, this.modelInfoList);
        }

        public void visitApiException(Throws aThrows, Throw aThrow) {
            List<Throw> throwList = new ArrayList<>();
            if (aThrows != null) {
                throwList.addAll(Arrays.asList(aThrows.value()));
            }
            if (aThrow != null) {
                throwList.add(aThrow);
            }
            if (!throwList.isEmpty()) {
                for (Throw th : throwList) {
                    DubboExceptionInfo dubboExceptionInfo = new DubboExceptionInfo();
                    dubboExceptionInfo.setExceptionType(th.type().getName());
                    dubboExceptionInfo.setDesc(th.description());
                    dubboExceptionInfoList.add(dubboExceptionInfo);
                }
            }
        }

        public void visitExceptionTypes(Class<?>[] exceptionTypes) {
            for (Class<?> exceptionType : exceptionTypes) {
                ExceptionDesc exceptionDesc = exceptionType.getAnnotation(ExceptionDesc.class);
                if (exceptionDesc != null) {
                    DubboExceptionInfo dubboExceptionInfo = new DubboExceptionInfo();
                    dubboExceptionInfo.setExceptionType(exceptionType.getName());
                    dubboExceptionInfo.setDesc(exceptionDesc.description());
                    dubboExceptionInfoList.add(dubboExceptionInfo);
                }
            }
        }
    }

    @Data
    public static class DubboApiParamInfo {
        private String name; // 参数名称

        private String desc;// 参数描述

        private boolean required;// 参数是否必传

        private String type;// 字段类型，带范型信息
    }

    @Data
    public static class DubboApiResponseInfo {
        private String desc;// 返回值描述

        private String type;// 字段类型，带范型信息

        private Boolean allowNullValue = false;// 是否为null
    }

    @Data
    public static class DubboExceptionInfo {
        private String exceptionType;// 异常类型，类名

        private String desc;// 异常描述

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DubboExceptionInfo that = (DubboExceptionInfo) o;
            return Objects.equals(exceptionType, that.exceptionType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(exceptionType);
        }
    }
}
