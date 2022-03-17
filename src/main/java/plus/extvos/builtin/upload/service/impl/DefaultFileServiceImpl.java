package plus.extvos.builtin.upload.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import plus.extvos.builtin.upload.config.FileManagerConfig;
import plus.extvos.builtin.upload.dto.FileInfo;
import plus.extvos.builtin.upload.enums.FileType;
import plus.extvos.builtin.upload.enums.ResultCode;
import plus.extvos.builtin.upload.service.FileService;
import plus.extvos.builtin.upload.utils.FileSystemUtil;
import plus.extvos.common.Assert;
import plus.extvos.common.exception.ResultException;
import plus.extvos.common.utils.QuickHash;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "quick.builtin.filemanager", name = "default-fs", havingValue = "true")
public class DefaultFileServiceImpl implements FileService {

    @Autowired
    private FileManagerConfig fmConfig;

    private String makeFilePath(String... segments) {
        List<String> ss = new ArrayList<>();
        ss.add(fmConfig.getRoot());
        for (String s : segments) {
            ss.add(s);
        }
        return String.join("/", ss);
    }

    private String makeFileUrl(String... segments) {
        List<String> ss = new ArrayList<>();
        if (null != fmConfig.getBaseUrl() && !fmConfig.getBaseUrl().isEmpty()) {
            ss.add(fmConfig.getBaseUrl());
        }
        ss.add(fmConfig.getPrefix());
        for (String s : segments) {
            ss.add(s);
        }
        return String.join("/", ss);
    }

    private String makeChunkPath(int chunkNum, String... segments) {
        List<String> ss = new ArrayList<>();
        ss.add(fmConfig.getTemporary());
        String hex = QuickHash.md5().hash(String.join("-", segments)).hex();
        ss.add(hex);
        ss.add(chunkNum + ".tmp");
        return String.join("/", ss);
    }

    private String getExt(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            return filename.substring(i + 1);
        } else {
            return "";
        }
    }

    private boolean deleteDirectory(File dir, boolean recursive) {
        if (dir.isDirectory()) {
            File[] subs = dir.listFiles();
            if (null != subs && subs.length > 0) {
                if (recursive) {
                    for (File f : subs) {
                        if (!deleteDirectory(f, true)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public FileInfo stat(@NonNull String bucket, String path, String filename, boolean withMd5, boolean recursive) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        File f = new File(full);
        if (!f.exists()) {
            throw ResultException.notFound();
        }
        if (!f.canRead()) {
            throw new ResultException(ResultCode.FORBIDDEN_READ, "can not access");
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setType(FileSystemUtil.detectFileType(f));
        fileInfo.setPath(path);
        fileInfo.setFilename(filename);
        LocalDateTime modified = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        fileInfo.setModified(modified);
        if (fileInfo.getType().equals(FileType.FILE)) {
            fileInfo.setSize(f.length());
            fileInfo.setUrl(makeFileUrl(bucket, path, filename));
            fileInfo.setExt(getExt(filename));
            if (withMd5) {
                try {
                    fileInfo.setMd5(QuickHash.md5().hash(f).hex());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (fileInfo.getType().equals(FileType.DIRECTORY)) {
            fileInfo.setSize(0L);
            fileInfo.setMd5("");
            if (recursive) {
                for (String fname : f.list()) {
                    fileInfo.append(stat(bucket, String.join("/", path, filename), fname, withMd5, false));
                }
            }

        }
        return fileInfo;
    }

    @Override
    public FileInfo mkdir(@NonNull String bucket, String path, String filename) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        File f = new File(full);
        if (f.exists()) {
            throw ResultException.conflict("path already exists");
        }
        if (!f.mkdir()) {
            throw ResultException.forbidden("mkdir failed");
        }
        return new FileInfo(path, filename, 0L, FileType.DIRECTORY, LocalDateTime.now(), "");
    }

    @Override
    public File file(String bucket, String path, String filename) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        return new File(full);
    }

    @Override
    public InputStream read(@NonNull String bucket, String path, String filename) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        File f = new File(full);
        if (!f.exists()) {
            throw ResultException.notFound("filename not exists");
        }
        if (!f.isFile()) {
            throw ResultException.forbidden("not a regular file");
        }
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw ResultException.notFound("filename not exists");
        }
    }

    @Override
    public OutputStream save(@NonNull String bucket, String path, String filename) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        File f = new File(full);
        if (f.exists()) {
            throw ResultException.conflict("path already exists");
        }
        try {
            return new FileOutputStream(f);
        } catch (IOException e) {
            throw ResultException.forbidden("create file failed");
        }
    }

    @Override
    public FileInfo delete(@NonNull String bucket, String path, String filename, boolean recursive) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeFilePath(bucket, path, filename);
        File f = new File(full);
        if (!f.exists()) {
            throw ResultException.notFound("filename not exists");
        }
        FileInfo fileInfo = stat(bucket, path, filename, false, false);
        if (!deleteDirectory(f, recursive)) {
            throw ResultException.forbidden("delete failed");
        }
        return fileInfo;
    }

    @Override
    public File chunk(@NonNull String bucket, String path, String filename, int chunkNum) throws ResultException {
        Assert.notEmpty(filename, ResultException.badRequest());
        String full = makeChunkPath(chunkNum, bucket, path, filename);
        return new File(full);
    }
}
