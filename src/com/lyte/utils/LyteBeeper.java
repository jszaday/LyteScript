package com.lyte.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Created by jszaday on 7/7/2015.
 */
public class LyteBeeper {
  public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  public static final int SECONDS = 2;
  private static final AudioFormat AUDIO_FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);

  public static void beep(int f, int ms) throws LineUnavailableException {
    SourceDataLine dataLine = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
    dataLine.open(AUDIO_FORMAT, SAMPLE_RATE);
    dataLine.start();
    dataLine.write(generateSin(f), 0, SAMPLE_RATE * Math.min(ms, SECONDS * 1000) / 1000);
    dataLine.drain();
    dataLine.close();
  }

  private static byte[] generateSin(int f) {
    byte[] sin = new byte[SECONDS * SAMPLE_RATE];
    for (int i = 0; i < sin.length; i++) {
      double period = (double)SAMPLE_RATE / f;
      double angle = 2.0 * Math.PI * i / period;
      sin[i] = (byte)(Math.sin(angle) * 127f);
    }
    return sin;
  }
}
