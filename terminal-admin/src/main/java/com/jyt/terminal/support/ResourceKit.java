/**
 * 
 */
package com.jyt.terminal.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/**
 * @author lcl
 *
 */
public class ResourceKit extends ResourceUtils {

	/**
	 * <p>
	 * 查找指定pattern的资源
	 * </p>
	 * 
	 * @param locationPattern<br>
	 *            可以直接加载指定的文件,例如"file:C:/context.xml",也可以从classpath下加载,<br>
	 *            例如:
	 *            "classpath:/context.xml" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context ClassLoader
	 *            没有找到的情况下,使用当前类加载器.另外,还可以在文件系统中查找相对于classpath的文件,例如:
	 *            "classpathfile:src/test/datas/example.xlsx",注意使用classpathfile时,相对路径不要以'/'开头
	 * @return
	 * @throws IOException
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 * @author
	 */
	public static Resource[] getResources(String locationPattern) throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
				ClassUtils.getDefaultClassLoader());
		if (locationPattern.startsWith("classpathfile:")) {
			String path = locationPattern.substring("classpathfile:".length());
			URL url = ClassUtils.getDefaultClassLoader().getResource("");
			locationPattern = "file:" + url.getPath() + path;
		}
		return resolver.getResources(locationPattern);
	}

	/**
	 * <p>
	 * 查找文件资源
	 * </p>
	 * 
	 * @param locationPattern
	 *            可以直接加载指定的文件,例如"file:C:/context.xml",也可以从classpath下加载,例如
	 *            "classpath:/context.xml" 支持ANT语法 注意:从classpath下加载时,优先使用 thread
	 *            context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器.另外,还可以在文件系统中查找相对于classpath的文件,例如:
	 *            "classpathfile:src/test/datas/example.xlsx",注意使用classpathfile时,相对路径不要以'/'开头
	 * @return
	 * @throws IOException
	 * @author
	 */
	public static FileSystemResource getFileSystemResource(String locationPattern) throws IOException {
		File file = getFile(locationPattern);
		FileSystemResource fileSystemResource = new FileSystemResource(file);
		return fileSystemResource;
	}
}
