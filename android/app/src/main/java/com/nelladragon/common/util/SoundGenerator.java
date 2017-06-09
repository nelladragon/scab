// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Code to generate a tone and then play it.
 */
public class SoundGenerator {
    private static final String LOG_TAG = SoundGenerator.class.getSimpleName();

    private static final double DURATION = 0.05; // seconds
    private static final int SAMPLE_RATE = 8000;
    private static final int NUM_SAMPLES = (int)(DURATION * SAMPLE_RATE);

    private boolean playing = false;

    private double freqOfTone = 440; // hz
    private AudioTrack audioTrack;



    public SoundGenerator() {
        // Create the track in streaming mode.
        this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, NUM_SAMPLES,
                AudioTrack.MODE_STREAM);
        // Call play so the track will start playing when data is written.
        this.audioTrack.play();
    }


    /**
     * Set the frequency to produce sound at. The parameter is a relative value ranging
     * from 0 to 1. The value is scaled to a frequency range 120Hz to 3700Hz.
     *
     * Note:
     * - If the upper frequency is to be increased, then the sample rate needs to be increased.
     *   Sample rate = 2 x max frequency.
     * - Frequnencies are measured in a linear scale. The relative frequency is in a linear scale.
     *   As such 0.5 input represents approximately 500 Hz.
     *
     * @param relativeFrequency Value ranging from 0 to 1 representing the minimum and maximum frequency.
     */
    public void setNewFrequency(double relativeFrequency) {
        final double LOG10_120 = 2.1;
        final double LOG10_3700 = 3.56;
        double scaledValue = relativeFrequency * (LOG10_3700 - LOG10_120) + LOG10_120;
        this.freqOfTone = Math.pow(10, scaledValue);

        Log.i(LOG_TAG, "Frequency set as: " + this.freqOfTone);

    }


    public synchronized void startPlaying() {
        if (this.playing) {
            return;
        }
        this.playing = true;

        Thread thread = new Thread(new AudioPlayer());
        thread.start();
    }

    public synchronized void stopPlaying() {
        this.playing = false;
    }



    class AudioPlayer implements Runnable {
        public void run() {
            double oldFrequency = freqOfTone;
            double oldPhase = 0;
            short generatedSnd[] = new short[NUM_SAMPLES];
            double angle = 0;

            while (playing) {
                // Get a snapshot of the frequency; just in
                double newFreq = freqOfTone;
                double realFreq = oldFrequency;
                double frequencyIncrement = (newFreq - oldFrequency) / NUM_SAMPLES;
                for (int i = 0; i < NUM_SAMPLES; ++i) {
                    realFreq += frequencyIncrement;
                    angle = 2 * Math.PI * i / (SAMPLE_RATE / realFreq) + oldPhase;
                    // Pure sine wave value.
                    double sample = Math.sin(angle);
                    // scale to maximum short amplitude
                    generatedSnd[i] = (short) (sample * 32767);
                }

                oldFrequency = newFreq;
                // Old phase for next time through is the phase at NUM_SAMPLES
                oldPhase = (2 * Math.PI * NUM_SAMPLES / (SAMPLE_RATE / realFreq) + oldPhase) % (2 * Math.PI);

                // Write sound into buffer. It should start playing automatically.
                int status = audioTrack.write(generatedSnd, 0, generatedSnd.length);
                if (status < 0) {
                    Log.e(LOG_TAG, "Audio write error: " + status);
                }
            }
        }
    }
}
