import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileCommander {
    private String rootDirPath;

    public FileCommander(String dir){
        this.rootDirPath = dir;
        try {
            createDir(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDir(String dirPath) throws IOException{
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir))
            Files.createDirectory(dir);
    }

    public File createUserDir(int userId) {
        File dir = new File(rootDirPath, Integer.toString(userId));
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    public List getUsersFileList(int userId) throws IOException {
        File dir = new File(rootDirPath, Integer.toString(userId));
        if (!dir.exists())
            createUserDir(userId);
        if (!dir.canRead())
            throw new IOException("no_readable_dir");
        return new ArrayList<String>(Arrays.asList(dir.list()));
    }

    public void saveFileToUsersDir(int userId, File file) throws IOException {
        File dir = new File(rootDirPath, Integer.toString(userId));
        if (!dir.exists())
            createUserDir(userId);
        if (!dir.canWrite())
            throw new IOException("no_writable_dir");

        Path path = Paths.get(dir.getPath() + File.separator + file.getName());
        Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteUsersFile(int userId, String file_name) throws IOException {
        File dir = new File(rootDirPath, Integer.toString(userId));
        if (!dir.exists())
            createUserDir(userId);
        if (!dir.canWrite())
            throw new IOException("no_writable_dir");

        Path path = Paths.get(dir.getPath() + File.separator + file_name);

        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    public File getUserFile(int userId, String file_name) throws IOException {
        File dir = new File(rootDirPath, Integer.toString(userId));
        if (!dir.exists())
            createUserDir(userId);
        if (!dir.canRead())
            throw new IOException("no_writable_dir");

        return new File(dir.getPath() + File.separator + file_name);
    }
}
