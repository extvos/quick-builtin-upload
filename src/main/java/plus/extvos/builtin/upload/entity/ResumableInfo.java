package plus.extvos.builtin.upload.entity;

import java.io.File;
import java.util.HashSet;

/**
 * @author Mingcai SHEN
 */
public class ResumableInfo {
    public long chunkSize;
    public long totalSize;
    public int totalChunks;
    public int chunkNum;
    public String identifier;
    public String filename;
    public String relativePath;
    public String url;

    public String fullFilename;
    public String chunkFilename;

    public static class ChunkNumber {
        public ChunkNumber(int number) {
            this.number = number;
        }

        public int number;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ChunkNumber && ((ChunkNumber) obj).number == this.number;
        }

        @Override
        public int hashCode() {
            return number;
        }
    }

    /**
     * Chunks uploaded
     */
    public HashSet<ChunkNumber> uploadedChunks = new HashSet<ChunkNumber>();

    public String filePath;

    public boolean valid() {
        return chunkSize >= 0 && totalSize >= 0
                && !identifier.isEmpty()
                && !filename.isEmpty()
                && !relativePath.isEmpty();
    }

    public boolean checkIfUploadFinished() {
        //check if upload finished
        int count = (int) Math.ceil(((double) totalSize) / ((double) chunkSize));
        for (int i = 1; i < count; i++) {
            if (!uploadedChunks.contains(new ChunkNumber(i))) {
                return false;
            }
        }

        //Upload finished, change filename.
        File file = new File(filePath);
        String newPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - ".temp".length());
        return file.renameTo(new File(newPath));
    }
}
