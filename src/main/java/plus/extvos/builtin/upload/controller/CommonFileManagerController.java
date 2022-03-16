package plus.extvos.builtin.upload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plus.extvos.builtin.upload.service.FileService;

@RestController
@RequestMapping("/_builtin/filemanager")
@ConditionalOnProperty(prefix = "quick.builtin.filemanager", name = "enabled", havingValue = "true")
public class CommonFileManagerController extends AbstractFileManagerController {
    @Autowired
    private FileService fileService;

    @Override
    protected FileService fs() {
        return fileService;
    }
}
