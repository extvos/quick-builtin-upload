package plus.extvos.builtin.upload.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import plus.extvos.builtin.upload.config.FileManagerConfig;
import plus.extvos.builtin.upload.dto.FileInfo;
import plus.extvos.builtin.upload.service.FileService;
import plus.extvos.common.exception.ResultException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@ConditionalOnProperty(prefix = "quick.builtin.filemanager", name = "default-fs", havingValue = "true")
public class DefaultFileServiceImpl implements FileService {

    @Autowired
    private FileManagerConfig fmConfig;

    @Override
    public FileInfo stat(String bucket, String path, String filename, boolean withMd5, boolean recursive) throws ResultException {
        return null;
    }

    @Override
    public FileInfo mkdir(String bucket, String path, String filename) throws ResultException {
        return null;
    }

    @Override
    public InputStream read(String bucket, String path, String filename) throws ResultException {
        return null;
    }

    @Override
    public OutputStream save(String bucket, String path, String filename) throws ResultException {
        return null;
    }

    @Override
    public FileInfo delete(String bucket, String path, String filename, boolean recursive) throws ResultException {
        return null;
    }

    @Override
    public File chunk(String bucket, String path, String filename, int chunkNum) throws ResultException {
        return null;
    }
}
