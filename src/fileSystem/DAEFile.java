package fileSystem;

import settings.FileSettings;

public class DAEFile extends MyFile{
    public DAEFile(String path) {
        super(FileSettings.RES_LOC + path + ".dae");
    }
}
