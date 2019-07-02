package FileManager;

import javafx.beans.property.SimpleStringProperty;

public class FileInfoBean {

    public FileInfoBean(String fileName,String filePath, String isFile,String permission,String fileSize,String lastTime) {
        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.permission = new SimpleStringProperty(permission);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.lastTime = new SimpleStringProperty(lastTime);
        this.isFile = new SimpleStringProperty(isFile);
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getFilePath() {
        return filePath.get();
    }

    public String getPermission() {
        return permission.get();
    }

    public String getFileSize() {
        if(fileSize.get().equals(" "))
            return fileSize.get();
        else
            return fileSize.get() + "KB";
    }
    public String getLastTime() {
        return lastTime.get();
    }

    public String getIsFile() {
        return isFile.get();
    }

    private SimpleStringProperty fileName;
    private SimpleStringProperty filePath;
    private SimpleStringProperty permission;
    private SimpleStringProperty fileSize;
    private SimpleStringProperty lastTime;
    private SimpleStringProperty isFile;
}
