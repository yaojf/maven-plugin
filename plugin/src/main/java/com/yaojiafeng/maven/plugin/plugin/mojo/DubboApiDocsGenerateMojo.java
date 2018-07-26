package com.yaojiafeng.maven.plugin.plugin.mojo;

import com.yaojiafeng.maven.plugin.plugin.bean.DubboApiInfo;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import com.yaojiafeng.maven.plugin.plugin.tool.DubboApiDocsGenerateTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 上午11:10
 * Description:
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.TEST)
public class DubboApiDocsGenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() {
        String outputDirectory = StringUtils.defaultString(System.getenv(Constants.OUTPUT_DIRECTORY),
                System.getProperty(Constants.OUTPUT_DIRECTORY, project.getBasedir().getAbsolutePath()));

        getLog().info("outputDirectory=" + outputDirectory);

        Build build = project.getBuild();
        String classOutputDirectory = build.getOutputDirectory();

        File apiDocsGenerateDir = new File(outputDirectory);
        apiDocsGenerateDir.mkdirs();

        String jsonFileName = project.getGroupId() + Constants.BAR + project.getArtifactId() + Constants.BAR + project.getVersion() + Constants.BAR + Constants.DUBBO_KEY + Constants.API_DOCS_SUFFIX;
        File jsonFile = new File(apiDocsGenerateDir, jsonFileName);

        try {
            DubboApiDocsGenerateTool dubboApiDocsGenerateTool = new DubboApiDocsGenerateTool();
            dubboApiDocsGenerateTool.setLog(getLog());
            dubboApiDocsGenerateTool.setClasspath(classOutputDirectory);

            ApiDocsGenerateMojoUtil.generate(project, dubboApiDocsGenerateTool);

            List<DubboApiInfo> dubboApiInfoList = dubboApiDocsGenerateTool.getDubboApiInfoList();
            ApiDocsGenerateMojoUtil.genJsonFile(dubboApiInfoList, project, jsonFile);
        } catch (Throwable e) {
            getLog().error(e);
        }

        getLog().info("generate " + jsonFile.getAbsolutePath() + "!");
    }


}
