package toolbox;

import settings.FileSettings;

public class PNGFile extends MyFile {
    public PNGFile(String path) {
        super(FileSettings.RES_LOC + path + ".png");
    }
}
