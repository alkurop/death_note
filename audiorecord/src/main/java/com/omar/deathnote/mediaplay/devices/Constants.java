package com.omar.deathnote.mediaplay.devices;

import android.media.AudioFormat;

/**
 * Created by omar on 19.08.15.
 */
public class Constants {



    /*------------------STRINGS--------------*/

    public static final String KBPS = "kbps";
    public static final String HZ = "Hz";
    public static final String KB = "kb";
    public static final String HZ_LABEL = "hz";
    public static final String MP3_EXTENTION = ".mp3";
    public static final String TYPE_ALL = "*/*";


    public static final String SAMPLE_RATE_LABEL = "Sample rate";
    public static final String VOICE_RECORD = "voice_record";
    public static final String DATA = "data";

    public static final String TWEETER = "twitter";
    public static final String ANDROID_EMAIL = "android.email";
    public static final String INSTAGRAM = "instagram";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String ANDROID_GM = "android.gm";
    public static final String MESSAGE_RFC822 = "message/rfc822";
    public static final String DROPBOX = "dropbox";
    public static final String SKYPE = "skype";
    public static final String VIBER = "viber";
    public static final String FACEBOOK_ORCA = "facebook.orca";
    public static final String MP3_LAME_LIB = "mp3lame";


    /*------------------NUMBERS--------------*/


    public static final int[] BIT_RATE_PRESETS = new int[] { 320, 192, 160, 128 };
    public static final int[] SAMPLE_RATE_PRESETS = new int[] { 44100, 22050, 11025, 8000 };


    public static final short[] AUDIO_FORMAT_PRESETS = new short[] {
            AudioFormat.ENCODING_PCM_8BIT, AudioFormat
            .ENCODING_PCM_16BIT
    };


    public static final int[] QUALITY_PRESETS = new int[] { 2, 5, 7 };  // the lower the better
    public static final short[] CHANNEL_PRESETS = new short[] {
            AudioFormat.CHANNEL_IN_MONO, AudioFormat
            .CHANNEL_IN_STEREO
    };


    public static final int MILLI_DELAY = 500;
}

