package audio;

import fileSystem.WAVFile;

import static settings.AudioSettings.DEFAULT_VOLUME;

public class BackgroundMusic {

    private static Source source;

    public static void init(WAVFile music, boolean playMusic) {
        int buffer = AudioMaster.loadSound(music);
        source = new Source(DEFAULT_VOLUME);
        source.setLooping(true);
        source.play(buffer);
        if (!playMusic)
            pause();
    }

    public static void pause(){
        source.pause();
    }

    public static void continuePlaying(){
        source.continuePlaying();
    }

    public static void stop(){
        source.stop();
    }

    public static void cleanUp(){
        source.delete();
    }
}
