package me.aap.fermata.addon.web.vidbox;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.aap.fermata.addon.AddonManager;
import me.aap.fermata.addon.web.FermataWebClient;
import me.aap.fermata.addon.web.WebBrowserAddon;
import me.aap.fermata.addon.web.WebBrowserFragment;
import me.aap.fermata.ui.activity.VoiceCommand;

/** Hosts vidbox.wtf in a full-screen WebView with a Home button. */
@Keep
@SuppressWarnings("unused")
public final class VidboxFragment extends WebBrowserFragment {

	@Override
	public int getFragmentId() {
		return me.aap.fermata.R.id.vidbox_fragment;
	}

	@NonNull
	@Override
	public CharSequence getTitle() {
		return "Vidbox";
	}

	@Nullable
	@Override
	protected WebBrowserAddon getAddon() {
		return AddonManager.get().getAddon(VidboxAddon.class);
	}

	@NonNull
	@Override
	protected FermataWebClient createWebClient() {
		return new VidboxWebClient();
	}

	@Override
	protected String getSearchUrl() {
		return VidboxAddon.SEARCH_URL;
	}

	@Override
	protected boolean hasHomeButton() {
		return true;
	}

	@Override
	protected void goHome() {
		loadUrl(VidboxAddon.HOME_URL);
	}

	void openSearch(String query) {
		voiceCommand(new VoiceCommand(query, VoiceCommand.ACTION_FIND));
	}
}
