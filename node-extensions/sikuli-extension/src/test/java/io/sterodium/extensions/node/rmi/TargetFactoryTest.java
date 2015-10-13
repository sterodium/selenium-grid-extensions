package io.sterodium.extensions.node.rmi;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20/09/2015
 */
public class TargetFactoryTest {

    public static final String NODE_FOLDER = "src/main/java/io/sterodium/extensions/node";

    public static final String FILE_PATH = "rmi/TargetFactory.java";
    public static final String FILE_PATH_BACKSLASHED = "rmi\\TargetFactory.java";

    private TargetFactory targetFactory;

    @Before
    public void setUp() {
        targetFactory = new TargetFactory();
        targetFactory.setImagePrefix(NODE_FOLDER);
    }

    @Test
    public void setImagePrefixWithFile() {
        try {
            targetFactory.setImagePrefix(NODE_FOLDER + "/" + FILE_PATH);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("is not a folder"));
        }
    }

    @Test
    public void setImagePrefixWithNonExistingFolder() {
        try {
            targetFactory.setImagePrefix(NODE_FOLDER + "_NON_EXISTING");
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("does not exist"));
        }
    }

    @Test
    public void shouldImageFindFile_FullPath() {
        // user is very accurate and precise
        File f1 = targetFactory.findImageFile(FILE_PATH);
        assertTrue(f1.exists());

        File f2 = targetFactory.findImageFile(FILE_PATH_BACKSLASHED);
        assertTrue(f2.exists());
    }

    @Test
    public void shouldFindFile_SlashAtFullPath() {
        // user mistakenly added "/" in front of relative file name
        File f1 = targetFactory.findImageFile("/" + FILE_PATH);
        assertTrue(f1.exists());

        File f2 = targetFactory.findImageFile("\\" + FILE_PATH_BACKSLASHED);
        assertTrue(f2.exists());
    }

    @Test
    public void shouldFindFile_MissingFoldersFromFullPath() {
        // user forgets some folders from full file path
        File f1 = targetFactory.findImageFile("rmi/TargetFactory.java");
        assertTrue(f1.exists());

        File f2 = targetFactory.findImageFile("rmi\\TargetFactory.java");
        assertTrue(f2.exists());

    }

    @Test
    public void shouldFindFile_RandomFolderFromFullFilePath() {
        targetFactory.setImagePrefix("src/test/resources");
        // user selects random folder from full file path
        File f1 = targetFactory.findImageFile("level1/search_goal.txt");
        assertTrue(f1.exists());
        // user selects random folder from full file path
        File f2 = targetFactory.findImageFile("level2\\search_goal.txt");
        assertTrue(f2.exists());
    }

    @Test
    public void shouldNotFindFile_WhenNotImageFile() {
        File f1 = targetFactory.findImageFile("sikuli");
        assertFalse(f1.exists());
        // user selects random folder from full file path
        File f2 = targetFactory.findImageFile("sikuli/rmi");
        assertFalse(f2.exists());
    }

    @Test
    public void shouldFindFile_ByFileName() {
        // user can also search just by file name
        File f1 = targetFactory.findImageFile("TargetFactory.java");
        assertTrue(f1.exists());
    }

    @Test
    public void shouldNotFindFile_FileNameWrong() {
        // wrong file - nothing is found
        File f1 = targetFactory.findImageFile("NonExisting.java");
        assertFalse(f1.exists());

        // wrong folder - nothing is found
        f1 = targetFactory.findImageFile("NON_EXISTING_FOLDER/TargetFactory.java");
        assertFalse(f1.exists());


        // wrong folder - nothing is found
        f1 = targetFactory.findImageFile("NON_EXISTING_FOLDER\\TargetFactory.java");
        assertFalse(f1.exists());

        // similar folder - nothing is found
        f1 = targetFactory.findImageFile("ikuli/TargetFactory.java");
        assertFalse(f1.exists());

        // similar folder - nothing is found
        f1 = targetFactory.findImageFile("ikuli\\TargetFactory.java");
        assertFalse(f1.exists());
    }

}
