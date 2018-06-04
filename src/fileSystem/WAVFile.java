package fileSystem;

import settings.FileSettings;

public class WAVFile extends MyFile {
    public WAVFile(String path) {
        super(FileSettings.RES_LOC + path + ".wav");
    }
}
