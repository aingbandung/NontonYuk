package me.aap.fermata.addon.web.ytmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static me.aap.fermata.util.Utils.dynCtx;

import me.aap.fermata.addon.AddonManager;
import me.aap.fermata.addon.web.FermataWebClient;
import me.aap.fermata.addon.web.R;
import me.aap.fermata.addon.web.WebBrowserAddon;
import me.aap.fermata.addon.web.WebBrowserFragment;
import me.aap.fermata.ui.activity.VoiceCommand;

/** Hosts music.youtube.com. Playback keeps going when another screen is shown. */
@Keep
@SuppressWarnings("unused")
public final class YtMusicFragment extends WebBrowserFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		dynCtx(requireContext());
		return inflater.inflate(R.layout.ytmusic_browser, container, false);
	}

	@Override
	public int getFragmentId() {
		return me.aap.fermata.R.id.ytmusic_fragment;
	}

	@NonNull
	@Override
	public CharSequence getTitle() {
		return "YT Music";
	}

	@Nullable
	@Override
	protected WebBrowserAddon getAddon() {
		return AddonManager.get().getAddon(YtMusicAddon.class);
	}

	@NonNull
	@Override
	protected FermataWebClient createWebClient() {
		return new YtMusicWebClient();
	}

	@Override
	protected String getSearchUrl() {
		return YtMusicAddon.HOME_URL + "search?q=";
	}

	@Override
	protected boolean hasHomeButton() {
		return true;
	}

	@Override
	protected void goHome() {
		loadUrl(YtMusicAddon.HOME_URL);
	}

	void openSearch(String query) {
		voiceCommand(new VoiceCommand(query, VoiceCommand.ACTION_FIND));
	}
}
