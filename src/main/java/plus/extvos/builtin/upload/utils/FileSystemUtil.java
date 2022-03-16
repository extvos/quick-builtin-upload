package plus.extvos.builtin.upload.utils;

import org.springframework.lang.NonNull;
import plus.extvos.builtin.upload.enums.FileType;

import java.io.File;

public class FileSystemUtil {

    public static FileType detectFileType(String root, @NonNull String filename) {
        String fullFilename = filename;
        if (null != root && !root.isEmpty()) {
            fullFilename = String.join("/", root, filename);
        }
        return detectFileType(fullFilename);
    }

    public static FileType detectFileType(@NonNull String filename) {
        return detectFileType(new File(filename));
    }

    public static FileType detectFileType(@NonNull File file) {
        if (file.isFile()) {
            return FileType.FILE;
        }
        if (file.isDirectory()) {
            return FileType.DIRECTORY;
        }
        return FileType.INVALID;
    }

    public static void makeDirectory() {

    }
}
