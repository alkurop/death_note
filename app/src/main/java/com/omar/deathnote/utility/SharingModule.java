package com.omar.deathnote.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.omar.deathnote.Constants;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.fragments.DefaultFragment;
import com.omar.deathnote.fragments.LinkFragment;
import com.omar.deathnote.fragments.NoteFragment;
import com.omar.deathnote.fragments.PicFragment;

public class SharingModule {

	private FragmentManager fm;
	private Fragment tempFragment;
	private TreeMap<String, String> fragList;
	Context context;

	private static final String PHOTO_URIS = "photouris";
	private static final String AUDIO_URIS = "audiouris";
	private static final String ALL_URIS = "alluris";

	private static final String SUBJECT = "subject";
	private static final String TEXT = "text";

	public SharingModule(FragmentManager fm, Context context,
			TreeMap<String, String> fragList) {
		this.fm = fm;
		this.context = context;
		this.fragList = fragList;
	}

	public void share() {
		initShareIntent(collectForShare());

	}

	private Bundle collectForShare() {

		String subject = "";
		String text = "";
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Uri> photoUris = new ArrayList<Uri>();
		ArrayList<Uri> audioUris = new ArrayList<Uri>();
		ArrayList<Uri> allUris = new ArrayList<Uri>();

		Bundle bundle = new Bundle();

		/*
		 * if(fragList != null) Log.d("fraglist length",
		 * String.valueOf(fragList.size()));
		 */

		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			TreeMap<String, String> temp = new TreeMap<String, String>();

			File fileIn;
			Uri u;

			String fragId = entry.getKey();
			String type = entry.getValue();

			String cont1 = "";

			Constants.Frags[] frags = Constants.Frags.values();
			Constants.Frags eType = null;

			for (Constants.Frags frag : frags) {

				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;

				}
			}

			switch (eType) {

			case DefaultFragment:

				tempFragment = (DefaultFragment) fm.findFragmentByTag(fragId);

				temp = ((DefaultFragment) tempFragment).saveContent();

				if (temp.get(Constants.Flags.Cont1.name()) != null) {
					cont1 = temp.get(Constants.Flags.Cont1.name());
				} else {
					cont1 = "No Title";
				}

				subject = cont1;

				Bundle titleBundle = new Bundle();
				titleBundle.putString(Constants.Flags.Cont1.name(), cont1);

				break;
			case PicFragment:

				tempFragment = (PicFragment) fm.findFragmentByTag(fragId);
				temp = ((PicFragment) tempFragment).saveContent();

				cont1 = temp.get(Constants.Flags.Cont1.name());
				if (cont1 != null)
					;
				fileIn = new File(cont1);
				fileIn.setReadable(true, false);
				u = Uri.fromFile(fileIn);
				photoUris.add(u);

				break;
			case NoteFragment:

				tempFragment = (NoteFragment) fm.findFragmentByTag(fragId);
				temp = ((NoteFragment) tempFragment).saveContent();

				if (!temp.get(Constants.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Constants.Flags.Cont1.name());
				} else {
					cont1 = "No Content";
				}

				stringBuilder.append("\n" + "\n" + cont1);

				break;

			case LinkFragment:

				tempFragment = (LinkFragment) fm.findFragmentByTag(fragId);
				temp = ((LinkFragment) tempFragment).saveContent();

				if (!temp.get(Constants.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Constants.Flags.Cont1.name());
				} else {
					cont1 = "No Link";
				}

				stringBuilder.append("http://" + cont1 + "\n" + "\n");

				break;
			case AudioFragment:

				tempFragment = (AudioFragment) fm.findFragmentByTag(fragId);
				temp = ((AudioFragment) tempFragment).saveContent();

				if (!temp.get(Constants.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Constants.Flags.Cont1.name());
				} else {
					cont1 = "No Audio";
				}

				fileIn = new File(cont1);
				fileIn.setReadable(true, false);
				u = Uri.fromFile(fileIn);
				audioUris.add(u);

				break;

			default:
				throw new IllegalArgumentException("illigal fragment type");

			}

		}

		text = stringBuilder.toString();

		bundle.putString(SUBJECT, subject);
		bundle.putString(TEXT, text);
		bundle.putParcelableArrayList(PHOTO_URIS, photoUris);
		bundle.putParcelableArrayList(AUDIO_URIS, audioUris);

		return bundle;

	}

	private List<LabeledIntent> formShareChoise(Intent emailIntent,
			PackageManager pm, ArrayList<Uri> photoUris,
			ArrayList<Uri> audioUris, List<ResolveInfo> resInfo,
			String subject, String text) {

		ArrayList<Uri> allUris = photoUris;
		allUris.addAll(audioUris);

		List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
		String packageName = null;
		ResolveInfo ri;

		for (int i = 0; i < resInfo.size(); i++) {

			ri = resInfo.get(i);
			Log.d("share type ---->>>", ri.activityInfo.packageName);
			packageName = ri.activityInfo.packageName;
			if (packageName.contains("android.email")) {
				emailIntent.setPackage(packageName);
			} else if (packageName.contains("twitter")
					|| packageName.contains("facebook.orca")
					|| packageName.contains("skype")
					|| packageName.contains("instagram")
					|| packageName.contains("viber")
					|| packageName.contains("dropbox")
					|| packageName.contains("android.gm")) {

				Intent intent = new Intent();
				intent.setComponent(new ComponentName(packageName,
						ri.activityInfo.name));
				intent.setAction(Intent.ACTION_SEND_MULTIPLE);
				intent.setType("text/plain");
				if (packageName.contains("android.gm")) {
					intent.putExtra(Intent.EXTRA_TEXT, text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
 

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, allUris);
					intent.setType("message/rfc822");

				}
				if (packageName.contains("instagram")) {
					intent.setAction(Intent.ACTION_SEND);
					if (photoUris.size() > 0) {
						intent.putExtra(Intent.EXTRA_STREAM, photoUris.get(0));
					}
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.setType("image/*");

				}
				if (packageName.contains("dropbox")) {
					intent.putExtra(Intent.EXTRA_TEXT, text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, allUris);
					intent.setType("message/rfc822");

				}

				if (packageName.contains("skype")) {
					intent.setAction(Intent.ACTION_SEND_MULTIPLE);
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, allUris);

					intent.setType("message/rfc822");

				}

				if (packageName.contains("viber")) {
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, allUris);
					intent.setType("message/rfc822");

				}
				if (packageName.contains("facebook.orca")) {
					intent.putExtra(Intent.EXTRA_TEXT, subject + text + "\n");

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, allUris);
					intent.setType("text/pain");

				}

				if (packageName.contains("twitter")) {
					intent.setAction(Intent.ACTION_SEND);
					if (photoUris.size() > 0) {
						intent.putExtra(Intent.EXTRA_STREAM, photoUris.get(0));
					}
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);

					intent.setType("text/pain");

				}
				intentList.add(new LabeledIntent(intent, packageName, ri
						.loadLabel(pm), ri.icon));

			}
		}
		return intentList;

	}

	private void initShareIntent(Bundle bundle) {
		/* Log.d("sharing", "sharing"); */
		String subject = bundle.getString(SUBJECT);
		String text = bundle.getString(SUBJECT);

		ArrayList<Uri> photoUris = bundle.getParcelableArrayList(PHOTO_URIS);
		ArrayList<Uri> audUris = bundle.getParcelableArrayList(AUDIO_URIS);

		/* Log.d("urisSpecial", String.valueOf(urisSpecial.size())); */

		Intent emailIntent = new Intent();
		emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putParcelableArrayListExtra(
				android.content.Intent.EXTRA_STREAM, photoUris);
		emailIntent.setType("message/rfc822");

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("*/*");
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
		Intent openInChooser = Intent.createChooser(emailIntent, "Select");

		List<LabeledIntent> intentList = formShareChoise(emailIntent, pm,
				photoUris, audUris, resInfo, subject, text);

		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		context.startActivity(openInChooser);
	}

}
