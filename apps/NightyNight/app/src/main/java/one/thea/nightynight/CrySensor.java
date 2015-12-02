package one.thea.nightynight;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JA on 2015-11-28.
 */
public class CrySensor
{
    private       int          SAMPLES_COUNT        = 5;
    private       long         SENSE_CRY_INTERVAL   = 10000L;
    private       int          SAMPLE_LENGTH_MILLIS = 3000;
    final private String       LOG_TAG              = "CRY_SENSOR";
    private       CryHandler   cryHandler;
    private       Timer        senseCryTaskTimer;
    private       AudioSampler audioSampler;
    private       CryDetector  cryDetector;


    public CrySensor(AudioSampler audioSampler) throws IOException {
        this.setAudioSampler(audioSampler);
        this.setCryDetector(new CryDetector());
    }

    public void registerCryHandler(CryHandler cryHandler)
    {
        this.cryHandler = cryHandler;
    }
    public void start() throws IOException {
        this.launchSenseCryTask();
    }
    public void stop() throws IOException {
        this.getSenseCryTaskTimer().cancel();
        this.getAudioSampler().stopRecording();
    }
    public void setSenseCryInterval(long milliseconds) {
        this.SENSE_CRY_INTERVAL = milliseconds;
    }
    public void setSampleLength(int milliseconds) {
        this.SAMPLE_LENGTH_MILLIS = milliseconds;
    }
    public void setSampleCount(int count) {
        this.SAMPLES_COUNT = count;
    }
    private void setAudioSampler(AudioSampler audioSampler) {
        this.audioSampler = audioSampler;
    }
    private AudioSampler getAudioSampler() {
        return this.audioSampler;
    }
    private void setCryDetector(CryDetector cryDetector) {
        this.cryDetector = cryDetector;
    }
    private CryDetector getCryDetector() {
        return cryDetector;
    }
    private void launchSenseCryTask() {
        this.setSenseCryTaskTimer(new Timer());
        this.getSenseCryTaskTimer().schedule(new SenseCryTask(), 0, this.SENSE_CRY_INTERVAL);
    }
    private void setSenseCryTaskTimer(Timer senseCryTaskTimer) {
        this.senseCryTaskTimer = senseCryTaskTimer;
    }
    private CryHandler getCryHandler() {
        return this.cryHandler;
    }
    private Timer getSenseCryTaskTimer() {
        return senseCryTaskTimer;
    }

    private class SenseCryTask extends TimerTask {
        @Override
        public synchronized void run()
        {
                this.recordNewAudioSamples();
                this.reactOnCry();
        }

        private void recordNewAudioSamples() {
            CrySensor.this.getAudioSampler().clearAudioSamples();
            for (int i = 0; i < CrySensor.this.SAMPLES_COUNT; i++)
            {
                CrySensor.this.getAudioSampler().recordAudioSample(CrySensor.this.SAMPLE_LENGTH_MILLIS);
            }
        }
        private void reactOnCry(){
            if(CrySensor.this.getCryDetector().isCrying(CrySensor.this.getAudioSampler().getAudioSamples()))
            {
                if(CrySensor.this.getCryHandler() !=null )
                {
                    CrySensor.this.getCryHandler().handle();
                }

            }
        }
    }
    private class CryDetector {
        private int                CRY_SAMPLES_THRESHOLD_PERCENT = 50;
        private int                CRY_MAX_AMPLITUDE_THRESHOLD   = 2500;
        public CryDetector(){}
        public boolean isCrying(List<AudioSample> audioSamples) {
            int crySamplesCount = this.countCrySamples(audioSamples);
            if(crySamplesCount == 0)
            {
                return false;
            }
            else
            {
                if(this.crySamplesCountExceedThreshold(crySamplesCount, audioSamples.size()))
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
        }

        private boolean crySamplesCountExceedThreshold(int crySamplesCount, int totalSamplesCount){
            return ((double)(crySamplesCount/totalSamplesCount)) * 100 > this.CRY_SAMPLES_THRESHOLD_PERCENT;
        }
        private boolean audioSampleIsACry(AudioSample audioSample) {
            return audioSample.getMaxAmplitude() >= this.CRY_MAX_AMPLITUDE_THRESHOLD;
        }
        private int countCrySamples(List<AudioSample> audioSamples){
            int crySamplesCount = 0;
            for(AudioSample audioSample: audioSamples)
            {
                if(this.audioSampleIsACry(audioSample))
                {
                    crySamplesCount++;
                }
            }
            return crySamplesCount;
        }

    }

}
