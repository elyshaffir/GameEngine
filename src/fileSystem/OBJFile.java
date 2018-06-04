package fileSystem;

import settings.FileSettings;

public class OBJFile extends MyFile{
    public OBJFile(String path) {
        super(FileSettings.RES_LOC + path + ".obj");
    }
}
