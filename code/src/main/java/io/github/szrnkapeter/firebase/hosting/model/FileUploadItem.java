package io.github.szrnkapeter.firebase.hosting.model;

/**
 * @author Peter Szrnka
 * @since 0.9
 */
public class FileUploadItem {

    private final byte[] fileContent;
    private final String checkSum;

    public FileUploadItem(byte[] fileContent, String checkSum) {
        this.fileContent = fileContent;
        this.checkSum = checkSum;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getCheckSum() {
        return checkSum;
    }
}
