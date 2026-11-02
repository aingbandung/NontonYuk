package me.aap.fermata.addon.web.ytmusic;

import android.net.Uri;

import androidx.annotation.IdRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.aap.fermata.addon.AddonInfo;
import me.aap.fermata.addon.FermataAddon;
import me.aap.fermata.addon.web.WebBrowserAddon;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.utils.ui.fragment.ActivityFragment;

/** YT Music (music.youtube.com) as a FermataX addon, with the YouTube ad blocking applied. */
@Keep
public class YtMusicAddon extends WebBrowserAddon {
	static final String HOME_URL = "https://music.youtube.com/";

	public YtMusicAddon() {
		super("ytmusic", HOME_URL);
	}

	@IdRes
	@Override
	public int getAddonId() {
		return me.aap.fermata.R.id.ytmusic_fragment;
	}

	@NonNull
	@Override
	public AddonInfo getInfo() {
		return FermataAddon.findAddonInfo(YtMusicAddon.class.getName());
	}

	@NonNull
	@Override
	public String getVoiceTarget() {
		return "ytmusic";
	}

	@NonNull
	@Override
	public ActivityFragment createFragment() {
		return new YtMusicFragment();
	}

	@Override
	public boolean handleVoiceSearch(MainActivityDelegate activity, String query, boolean play) {
		if (!(activity.showFragment(getAddonId()) instanceof YtMusicFragment f)) return false;
		f.openSearch(query);
		return true;
	}

	@Override
	protected String getLastUrl() {
		String url = super.getLastUrl();
		return YtMusicScripts.isYtMusicUrl(url) ? url : HOME_URL;
	}

	/** True for music.youtube.com links, which belong to this addon rather than YouTube. */
	public static boolean isYtMusicUri(@Nullable Uri uri) {
		if (uri == null) return false;
		String scheme = uri.getScheme();
		return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) &&
				YtMusicScripts.isYtMusicHost(uri.getHost());
	}
}
