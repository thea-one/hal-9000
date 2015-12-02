package one.thea.nightynight;

import java.io.IOException;
import java.util.List;

/**
 * Created by JA on 2015-11-29.
 */
public interface AudioSampler {
    public void recordAudioSample(int duration);
    public List<AudioSample> getAudioSamples();
    public void clearAudioSamples();
    public void stopRecording() throws IOException;
}
