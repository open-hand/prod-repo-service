package org.hrds.rdupm.nexus.infra.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author weisen.yang@hand-china.com 2020/4/1
 */
public class VelocityUtils {
	public static final String SET_SERVER_FILE_NAME = "SettingServer.vm";
	public static final String POM_REPO_FILE_NAME = "PomRepository.vm";
	public static final String POM_MANGE_FILE_NAME = "PomManagement.vm";


	public static String getJsonString(Map<String, Object> map, String fileName){
		VelocityContext context = new VelocityContext(map);
		try (StringWriter sw = new StringWriter()) {
			Properties prop = new Properties();
			prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			Velocity.init(prop);

			Template tpl = Velocity.getTemplate("template/" + fileName, "UTF-8");
			tpl.merge(context, sw);
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
