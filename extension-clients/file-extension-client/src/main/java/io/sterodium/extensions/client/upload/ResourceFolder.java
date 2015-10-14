package io.sterodium.extensions.client.upload;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 08/10/2015
 */
class ResourceFolder {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceFolder.class);

    private String path;

    public ResourceFolder(String path) {
        this.path = path;
    }

    public File toZip() {
        LOGGER.debug("Zipping resource folder " + path);

        try {
            URL dirURL = Resources.getResource(path);
            if (dirURL == null) {
                throw new ResourcePackagingException("Failed to get resource at " + path);
            }

            File destZip = File.createTempFile("resources_", ".zip");

            LOGGER.debug("Created temporary zip at " + destZip.getAbsolutePath());
            LOGGER.debug("Resource protocol is " + dirURL.getProtocol());

            if ("file".equals(dirURL.getProtocol())) {
                mapFolderFromResources(dirURL, destZip);
            } else {
                mapFolderFromJar(dirURL, destZip);
            }

            return destZip;
        } catch (Exception e) {
            throw new ResourcePackagingException(e);
        }
    }

    private void mapFolderFromResources(URL dirURL, File destZip) throws URISyntaxException {
        File srcFolder = new File(dirURL.toURI());
        ZipUtil.pack(srcFolder, destZip, pathKeeper());
    }

    private void mapFolderFromJar(URL dirURL, File destZip) throws IOException {
        try (
                ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(destZip))) {

            String jarPath = substringJarPath(dirURL.getPath());
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));

            byte[] buffer = new byte[1024];
            int len;

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();

                if (name.startsWith(path)) {
                    LOGGER.debug("Zipping entry " + name);

                    ZipEntry zipEntry = jar.getEntry(name);
                    zip.putNextEntry(zipEntry);

                    URL resource = Resources.getResource(zipEntry.getName());
                    try (InputStream inputStream = resource.openStream()) {
                        while ((len = inputStream.read(buffer)) > 0) {
                            zip.write(buffer, 0, len);
                        }
                    }
                    zip.closeEntry();
                }
            }
        }
    }

    /**
     * Example resource folder path may look like file:/C:/testware/target/ARTIFACT-1.0.0-SNAPSHOT.jar!/img
     * <p/>
     * Method cuts <code>file:</code> as first 5 symbols,
     * then cut any path after jar!..
     *
     * @param resourceFolderPath
     * @return jar path
     */
    private String substringJarPath(String resourceFolderPath) {
        return resourceFolderPath.substring(5, resourceFolderPath.indexOf("!"));
    }

    private NameMapper pathKeeper() {
        return new NameMapper() {
            @Override
            public String map(String name) {
                return path + "/" + name;
            }
        };
    }
}
