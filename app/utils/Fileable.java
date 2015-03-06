package utils;

import models.File;
import java.util.List;

/**
 * Created by peter on 2/27/15.
 */
public interface Fileable {
        public void addFile(Long id);
        public void removeFile(Long id);
        public void removeAllFiles();
        public List<File> getAllFiles();
}
