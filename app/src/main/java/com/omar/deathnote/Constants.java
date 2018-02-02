package com.omar.deathnote;


import android.support.annotation.DrawableRes;

public class Constants {

    public enum Frags {
        TitleFragment, NoteFragment,  LinkFragment, AudioFragment, PicFragment, AudioRecord
    }

    public final static String ID = "noteId";
    public static final String AUDIO_NUMBER = "audio_number";
    public static final String AUDIO_PATH = "audio_path";
    public static final String AUDIO_REPEAT = "audio_repeat";
    public static final String AUDIO_SHUFFLE = "audio_shuffle";
    public final static int[] bg_images_main = new int[]{R.drawable.gs_texture5};
    public final static int[] bg_images_main_2 = new int[]{R.drawable.gs_texture5_1};
    public static final String BLANK = "";
    public static final String BROADCAST_ACTION = "com.omar.deathnote.audioplay.progressbar";
    public static final String BROADCAST_AUTONOME = "com.omar.deathnote.fragments.audiofragment.autonome";
    public static final String BROADCAST_ENDOFSONG = "com.omar.deathnote.audioplay.endofsong";
    public static final String BROADCAST_NOTIFIC = "com.omar.deathnote.audioplay.notif";
    public static final String BROADCAST_PAUSESONG = "com.omar.deathnote.fragments.audiofragment.pausesong";
    public static final String BROADCAST_REFRESHUI = "com.omar.deathnote.audioplay.refreshui";
    public static final String BROADCAST_SEEKBAR = "com.omar.deathnote.fragments.audiofragment.seekbar";
    public static final String COUNTER = "counter";
    public static final String DESTROY = "destroy";
    public final static String FLAG = "flag";
    public static final String MEDIA_MAX = "media_max";

    public static final String MODE = "mode";

    public static final String MP3_LAME = "mp3lame";


    public static final String NEXT = "next";
    public final static int[] note_bg_images = new int[]{R.drawable.bg_blue,
            R.drawable.bg_green, R.drawable.bg_magenta, R.drawable.bg_metallic,
            R.drawable.bg_red, R.drawable.bg_yellow,};

    public final static int[] note_bg_images_1 = new int[]{
            R.drawable.bg_blue_1, R.drawable.bg_green_1,
            R.drawable.bg_magenta_1, R.drawable.bg_metallic_1,
            R.drawable.bg_red, R.drawable.bg_yellow_1,};

    public static final String PAUSE = "pauseAudio";

    public static final String PAUSED = "paused";
    public static final String PLAY_PAUSE = "playpause";
    public static final String PREVIOUS = "prev";
    public static final String RECORD = "rec";
    public static final String RECORDING_COUNTER = "rec_counter";
    public static final String RESUME = "resume";
    public static final String SEEK_POSITION = "seek_pos";
    public final static int[] select_images = new int[]{
            R.drawable.ic_action_all, R.drawable.ic_action_d_blue,
            R.drawable.ic_action_green, R.drawable.ic_action_magenta,
            R.drawable.ic_action_metallic, R.drawable.ic_action_red,
            R.drawable.ic_action_yellow,};

    public final static @DrawableRes
    int[] select_names = new int[]{R.string.all,
            R.string.d_blue, R.string.green, R.string.magenta,
            R.string.metallic, R.string.red, R.string.yellow};

    public static final String SONG_ENDED = "song_ended";

    public static final String TODO = "todo";
    public static final String LIST = "lis";
    public static final String PATH = "path";

    @DrawableRes
    public static int getStyleImage(int position) {
        return select_images[position];
    }


}
