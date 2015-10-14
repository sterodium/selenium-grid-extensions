package io.sterodium.extensions.node.rmi;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.sikuli.api.ImageTarget;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Collections2.filter;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.listFilesAndDirs;
import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20/09/2015
 */
class TargetFactory {

    private File folderToScan = new File(".");

    public void setImagePrefix(String imagePathPrefix) {
        String imageFolder = !imagePathPrefix.endsWith("/") ? imagePathPrefix + "/" : imagePathPrefix;
        this.folderToScan = new File(imageFolder);
        checkState(folderToScan.exists(), "Folder %s does not exist", folderToScan.getAbsolutePath());
        checkState(folderToScan.isDirectory(), "Folder %s is not a folder", folderToScan.getAbsolutePath());
    }

    public ImageTarget createImageTarget(String imageFile) {
        return new ImageTarget(findImageFile(imageFile));
    }

    @VisibleForTesting
    protected File findImageFile(String imageFile) {
        File foundImageFile = findImageFile(folderToScan, imageFile);
        // creating non existing file to avoid NPE
        return foundImageFile == null ? new File(imageFile) : foundImageFile;
    }

    private File findImageFile(File lookupFolder, String lookupTarget) {
        // given file can contain sub-folders in its name
        String[] paths = lookupTarget.split("[/\\\\]");
        String[] foldersToFind = Arrays.copyOfRange(paths, 0, paths.length - 1);

        for (String folderToFind : foldersToFind) {
            lookupFolder = findFolderRecursively(lookupFolder, folderToFind);
            if (lookupFolder == null) {
                return null;
            }
        }
        lookupTarget = paths[paths.length - 1];

        // finally searching for file name recursively
        Collection<File> files = listFiles(lookupFolder, new NameFileFilter(lookupTarget), TRUE);
        return files.isEmpty() ? null : files.iterator().next();
    }

    private File findFolderRecursively(File folderToScan, final String folderToFind) {
        // filtering out empty arguments
        if (folderToFind.isEmpty()) {
            return folderToScan;
        }

        // collecting all the sub-folders recursively
        Collection<File> foundFolders = listFilesAndDirs(folderToScan, DIRECTORY, TRUE);

        // filtering all the folders by folder name
        foundFolders = filter(foundFolders, new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                String fileName = file.getAbsolutePath();
                return fileName.endsWith("/" + folderToFind) || fileName.endsWith("\\" + folderToFind);
            }
        });
        return foundFolders.isEmpty() ? null : foundFolders.iterator().next();
    }

}
