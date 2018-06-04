package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;
import settings.AudioSettings;

public class Source {

    private int sourceId;
    private int volume;
    private int pitch;
    private Vector3f position;
    private Vector3f velocity;

    public Source() {
        this.sourceId = AL10.alGenSources();
        this.volume = 1;
        this.pitch = 1;
        this.velocity = new Vector3f(0, 0, 0);
        this.position = new Vector3f(0, 0, 0);
    }

    public Source(int volume){
        this.sourceId = AL10.alGenSources();
        this.volume = volume;
        this.pitch = 1;
        this.velocity = new Vector3f(0, 0, 0);
        this.position = new Vector3f(0, 0, 0);
    }

    public Source(int volume, int pitch, Vector3f position, Vector3f velocity) {
        this.volume = volume;
        this.pitch = pitch;
        this.position = position;
        this.velocity = velocity;
        setAttributes();
    }

    private void setAttributes(){
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
        AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
        AL10.alSource3f(sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setLinearClampedVariables(int referenceDistance, int maxDistance){
        AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, AudioSettings.LINEAR_CLAMPED_ROLLOFF);
        AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
        AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
    }

    public void play(int buffer){
        stop();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
        continuePlaying();
    }

    public void pause(){
        AL10.alSourcePause(sourceId);
    }

    public void continuePlaying(){
        AL10.alSourcePlay(sourceId);
    }

    public void stop(){
        AL10.alSourceStop(sourceId);
    }

    public void setVelocity(Vector3f velocity){
        this.velocity = velocity;
        AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setLooping(boolean looping){
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public boolean isPlaying(){
        return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
        AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        AL10.alSource3f(sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
    }

    public void delete() {
        stop();
        AL10.alDeleteSources(sourceId);
    }
}
