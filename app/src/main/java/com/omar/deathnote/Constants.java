package com.omar.deathnote;


public class Constants {

    public enum AudioCommangs {
        Pause, Play, Rec, Repeat, Shuffle, Stop
    }

    public enum Flags {
        Cont1, Cont2, Dialog
    }

    public enum Frags {
        AudioFragment, DefaultFragment, LinkFragment, NoteFragment, NoticeDialogFragment, PicFragment
    }

    public final static String DATA = "data";
    public final static String ORDER_STATUS = "order_status";
    public final static String RECORD_TO_DELETE = "rec_to_del";
    public final static String ID = "id";
    public final static String PLAY = "play";
    public final static String ATTRIBUTE_NAME_STYLE = "style";
    public final static String ATTRIBUTE_NAME_TEXT = "text";
    public static final String AUDIO_NAME = "audio_name";
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
    public static final String BROADCAST_NOTIF = "com.omar.deathnote.audioplay.notif";
    public static final String BROADCAST_NOTIFIC = "com.omar.deathnote.audioplay.notif";
    public static final String BROADCAST_PAUSESONG = "com.omar.deathnote.fragments.audiofragment.pausesong";
    public static final String BROADCAST_REFRESHUI = "com.omar.deathnote.audioplay.refreshui";
    public static final String BROADCAST_SEEKBAR = "com.omar.deathnote.fragments.audiofragment.seekbar";
    public static final String COUNTER = "counter";
    public static final String DESTROY = "destroy";
    public final static String FLAG = "flag";
    public static final String FRAGMENT_ID = "frag_id";
    public static final String MEDIA_MAX = "media_max";

    public static final String MODE = "mode";

    public static final String MP3_LAME = "mp3lame";

    public static final String MUSIC_PAUSED = "music_paused";

    public static final String MUSIC_PLAYING = "music_playing";

    public static final String NEXT = "next";
    public final static int[] note_bg_images = new int[]{R.drawable.bg_blue,
            R.drawable.bg_green, R.drawable.bg_magenta, R.drawable.bg_metallic,
            R.drawable.bg_red, R.drawable.bg_yellow,};

    public final static int[] note_bg_images_1 = new int[]{
            R.drawable.bg_blue_1, R.drawable.bg_green_1,
            R.drawable.bg_magenta_1, R.drawable.bg_metallic_1,
            R.drawable.bg_red, R.drawable.bg_yellow_1,};
    public static final String NOTE_ID = "note_id";

    public static final String PAUSE = "pauseAudio";

    public static final String PAUSED = "paused";
    public static final String PLAY_PAUSE = "playpause";
    public static final String PREVIOUS = "prev";
    public static final String RECORD = "rec";
    public final static String RECORDER_MODE = "recorder_mode";
    public static final String RECORDING_COUNTER = "rec_counter";
    public final static String REPEAT_AUDIO = "repeat_audio";
    public final static String REPLAY = "replay";
    public static final String RESUME = "resume";
    public static final String SEEK_POSITION = "seek_pos";
    public final static int[] select_images = new int[]{
            R.drawable.ic_action_all, R.drawable.ic_action_d_blue,
            R.drawable.ic_action_green, R.drawable.ic_action_magenta,
            R.drawable.ic_action_metallic, R.drawable.ic_action_red,
            R.drawable.ic_action_yellow,};

    public final static int[] select_names = new int[]{R.string.all,
            R.string.d_blue, R.string.green, R.string.magenta,
            R.string.metallic, R.string.red, R.string.yellow};

    public final static String SHUFFLE_AUDIO = "shuffle_audio";

    public static final String SONG_ENDED = "song_ended";

    public static final String TODO = "todo";
    public static final String STYLE = "style";
    public static final String SOME = "some";
    public static final String DIALOG = "dialog";
    public static final String TITLE = "title";
    public static final String SPACE = " ";
    public static final String FRAGMENT_COUNTER = "frag_counter";
    public static final String FRAGMENT_LIST_KEYS = "frag_list_keys";
    public static final String FRAGMENT_LIST_VALUES = "frag_list_values";
    public static final String CAMERA = "camera";
    public static final String IMAGE_NAME = "im_name";
    public static final String LIST = "lis";
    public static final String PATH = "path";
    public static final String FRAGMENT_ARRAY_LIST = "frag_array_list";

    public enum LOADERS {SAVE_NOTE, LOAD_NOTE, EDIT_REC_TITLE, LOAD_STYLE, ADD_NEW_NOTE, LOAD_LIST, DELETE_SOME_NOTE}


}
