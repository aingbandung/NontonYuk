package me.aap.fermata.addon.web.vidbox;

import android.net.Uri;

import androidx.annotation.IdRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import me.aap.fermata.addon.AddonInfo;
import me.aap.fermata.addon.FermataAddon;
import me.aap.fermata.addon.web.WebBrowserAddon;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.utils.ui.fragment.ActivityFragment;

/** Vidbox (vidbox.wtf) movies and series as a FermataX web addon. */
@Keep
public class VidboxAddon extends WebBrowserAddon {
	static final String HOME_URL = "https://vidbox.wtf/home";
	static final String SEARCH_URL = "https://vidbox.wtf/search?q=";

	public VidboxAddon() {
		super("vidbox", HOME_URL);
	}

	@IdRes
	@Override
	public int getAddonId() {
		return me.aap.fermata.R.id.vidbox_fragment;
	}

	@NonNull
	@Override
	public AddonInfo getInfo() {
		return FermataAddon.findAddonInfo(VidboxAddon.class.getName());
	}

	@NonNull
	@Override
	public String getVoiceTarget() {
		return "vidbox";
	}

	@NonNull
	@Override
	public ActivityFragment createFragment() {
		return new VidboxFragment();
	}

	@Override
	public boolean handleVoiceSearch(MainActivityDelegate activity, String query, boolean play) {
		if (!(activity.showFragment(getAddonId()) instanceof VidboxFragment f)) return false;
		f.openSearch(query);
		return true;
	}

	@Override
	protected String getLastUrl() {
		String url = super.getLastUrl();
		return isVidboxUrl(url) ? url : HOME_URL;
	}

	/** vidbox.wtf and its mirror domains (vidbox.cc, vidbox.to, ...). */
	static boolean isVidboxHost(@Nullable String host) {
		if (host == null) return false;
		host = host.toLowerCase(Locale.ROOT);
		if (host.startsWith("www.")) host = host.substring(4);
		return host.equals("vidbox") || host.startsWith("vidbox.") || host.endsWith(".vidbox.wtf");
	}

	static boolean isVidboxUrl(@Nullable String url) {
		if (url == null) return false;
		try {
			Uri uri = Uri.parse(url);
			String scheme = uri.getScheme();
			return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) &&
					isVidboxHost(uri.getHost());
		} catch (RuntimeException ignored) {
			return false;
		}
	}
}
