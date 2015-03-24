package com.omar.deathnote;

import com.omar.deathnote.R;

public class Select {

	public static enum Frags {
		DefaultFragment, AudioFragment, NoteFragment, LinkFragment, PicFragment, NoticeDialogFragment
	};

	public static enum AudioCommangs {
		Stop, Rec, Pause, Shuffle, Repeat, Play
	};

	public static enum Flags {
		Cont1, Cont2, Dialog
	};

	public final static int[] select_images = new int[] {
			R.drawable.ic_action_all, R.drawable.ic_action_d_blue,
			R.drawable.ic_action_green, R.drawable.ic_action_magenta,
			R.drawable.ic_action_metallic, R.drawable.ic_action_red,
			R.drawable.ic_action_yellow, };

	public final static int[] select_names = new int[] { R.string.all,
			R.string.d_blue, R.string.green, R.string.magenta,
			R.string.metallic, R.string.red, R.string.yellow };

	public final static String ATTRIBUTE_NAME_TEXT = "text";
	public final static String ATTRIBUTE_NAME_STYLE = "style";

	public final static int[] bg_images_main = new int[] { R.drawable.gs_texture5 };
	public final static int[] bg_images_main_2 = new int[] { R.drawable.gs_texture5_1 };

	public final static int[] note_bg_images = new int[] { R.drawable.bg_blue,
			R.drawable.bg_green, R.drawable.bg_magenta, R.drawable.bg_metallic,
			R.drawable.bg_red, R.drawable.bg_yellow, };

	public final static int[] note_bg_images_1 = new int[] {
			R.drawable.bg_blue_1, R.drawable.bg_green_1,
			R.drawable.bg_magenta_1, R.drawable.bg_metallic_1,
			R.drawable.bg_red, R.drawable.bg_yellow_1, };

}
