package com.yaojiafeng.maven.plugin.plugin.mojo;

import com.yaojiafeng.maven.plugin.plugin.bean.HttpApiInfo;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import com.yaojiafeng.maven.plugin.plugin.tool.HttpApiDocsGenerateTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * User: yaojiafeng
 * Date: 2018/6/7
 * Time: 下午5:46
 * Description: 依赖spring-web和swagger的注解
 */
@Mojo(name = "generateHttp", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.TEST)
public class HttpApiDocsGenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter
    private String springApplicationName;

    @Override
    public void execute() {
        File applicationYml = new File(project.getBasedir(), "src/main/resources/application.yml");
        String springApplicationName = null;
        try {
            if (applicationYml.exists()) {
                Map map = new Yaml().loadAs(new FileInputStream(applicationYml), Map.class);
                springApplicationName = (String) ((Map) ((Map) map.get("spring")).get("application")).get("name");
            }
        } catch (Exception e) {
            getLog().error(e);
        }

        if (StringUtils.isBlank(springApplicationName)) {
            springApplicationName = this.springApplicationName;
        }

        String outputDirectory = StringUtils.defaultString(System.getenv(Constants.OUTPUT_DIRECTORY),
                System.getProperty(Constants.OUTPUT_DIRECTORY, project.getBasedir().getAbsolutePath()));

        getLog().info("outputDirectory=" + outputDirectory);

        Build build = project.getBuild();
        String classOutputDirectory = build.getOutputDirectory();

        File apiDocsGenerateDir = new File(outputDirectory);
        apiDocsGenerateDir.mkdirs();

        String jsonFileName = project.getGroupId() + Constants.BAR + project.getArtifactId() + Constants.BAR + project.getVersion() + Constants.BAR + Constants.HTTP_KEY + Constants.API_DOCS_SUFFIX;
        File jsonFile = new File(apiDocsGenerateDir, jsonFileName);

        try {
            HttpApiDocsGenerateTool httpApiDocsGenerateTool = new HttpApiDocsGenerateTool();
            httpApiDocsGenerateTool.setLog(getLog());
            httpApiDocsGenerateTool.setClasspath(classOutputDirectory);

            ApiDocsGenerateMojoUtil.generate(project, httpApiDocsGenerateTool);

            List<HttpApiInfo> apiInfoList = httpApiDocsGenerateTool.getHttpApiInfoList();
            ApiDocsGenerateMojoUtil.genJsonFile(apiInfoList, project, jsonFile, springApplicationName);

        } catch (Throwable e) {
            getLog().error(e);
        }

        getLog().info("generate " + jsonFile.getAbsolutePath() + "!");
    }

}
