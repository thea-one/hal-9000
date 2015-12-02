package one.thea.nightynight;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* Created by JA on 2015-11-29.
*/
public class MediaRecorderAudioSampler implements AudioSampler {
    private MediaRecorder     mediaRecorder;
    private List<AudioSample> audioSamples      = new ArrayList<AudioSample>();
    final private String      LOG_TAG           = "AUDIO_SAMPLER";
    private AudioSamplerState audioSamplerState = AudioSamplerState.IDLE;
    public MediaRecorderAudioSampler(MediaRecorder mediaRecorder)
    {
        this.setMediaRecorder(mediaRecorder);
    }

    @Override
    public void recordAudioSample(int duration)    {
        try
        {
            this.configureRecorder();
            this.startRecordingAudioSample(duration);
        }
        catch (Exception e)
        {
            Log.e(this.LOG_TAG, e.getMessage(), e);
        }
    }
    @Override
    public void stopRecording() throws IOException {
        if(this.getMediaRecorder() != null && this.isRecording())
        {
            this.getMediaRecorder().stop();
            this.getMediaRecorder().release();
            this.audioSamplerState = AudioSamplerState.IDLE;
        }
    }
    @Override
    public List<AudioSample> getAudioSamples() {
        return this.audioSamples;
    }
    @Override
    public void clearAudioSamples() {
        audioSamples.clear();
    }

    private void addAudioSample(AudioSample audioSample) {
        this.getAudioSamples().add(audioSample);
    }

    private void configureRecorder() throws IOException {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile("/dev/null");
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                try
                {
                    if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                    {
                        MediaRecorderAudioSampler.this.saveAudioSample();
                        MediaRecorderAudioSampler.this.stopRecording();
                        MediaRecorderAudioSampler.this.audioSamplerState = AudioSamplerState.IDLE;
                    }
                }
                catch (Exception e)
                {
                    Log.e(MediaRecorderAudioSampler.this.LOG_TAG, e.getMessage(),e);
                }
            }
        });

        this.setMediaRecorder(mediaRecorder);
    }
    private MediaRecorder getMediaRecorder() {
        return this.mediaRecorder;
    }
    private void setMediaRecorder(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
    }
    private void startRecordingAudioSample(int duration) {
        try
        {
            if(this.getMediaRecorder() != null && this.isIdle())
            {
                this.getMediaRecorder().setMaxDuration(duration);
                this.getMediaRecorder().prepare();
                this.getMediaRecorder().start();
                this.audioSamplerState = AudioSamplerState.RECORDING;
                this.getMediaRecorder().getMaxAmplitude();//initial call to get bearing;
                while(this.isRecording()){}
            }
            else
            {
                throw new Exception("No Media Recorder Available or Media Recorder is not idle!");
            }
        }
        catch (Exception e)
        {
            Log.e(MediaRecorderAudioSampler.this.LOG_TAG, e.getMessage(), e);
            this.audioSamplerState = AudioSamplerState.IDLE;
        }

    }
    private boolean isRecording() {
        return this.audioSamplerState.equals(AudioSamplerState.RECORDING);
    }
    private boolean isIdle()    {
        return this.audioSamplerState.equals(AudioSamplerState.IDLE);
    }
    private void saveAudioSample() throws Exception {

            if(this.getMediaRecorder() != null)
            {
                AudioSample audioSample = new AudioSample();
                audioSample.setMaxAmplitude(this.getMediaRecorder().getMaxAmplitude());
                this.addAudioSample(audioSample);
            }
        else
            {
                throw new Exception("No Media Recorder Available!");
            }
        }

    private enum AudioSamplerState
    {
        IDLE, RECORDING;
    }

}
