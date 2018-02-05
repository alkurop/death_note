package com.omar.deathnote;


import android.support.annotation.DrawableRes;

public class Constants {

    public enum Frags {
        TitleFragment, NoteFragment,  LinkFragment, AudioPlay, PicFragment, AudioRecord
    }

    public final static String ID = "noteId";
    public final static int[] bg_images_main = new int[]{R.drawable.gs_texture5};
    public final static int[] bg_images_main_2 = new int[]{R.drawable.gs_texture5_1};
    public final static int[] note_bg_images = new int[]{R.drawable.bg_blue,
            R.drawable.bg_green, R.drawable.bg_magenta, R.drawable.bg_metallic,
            R.drawable.bg_red, R.drawable.bg_yellow,};

    public final static int[] note_bg_images_1 = new int[]{
            R.drawable.bg_blue_1, R.drawable.bg_green_1,
            R.drawable.bg_magenta_1, R.drawable.bg_metallic_1,
            R.drawable.bg_red, R.drawable.bg_yellow_1,};

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
