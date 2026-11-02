package me.aap.fermata.addon.web.yt;

import androidx.annotation.NonNull;

import me.aap.fermata.BuildConfig;
import me.aap.fermata.addon.AddonManager;

/**
 * Public entry point to the YouTube ad-blocking scripts, so other YouTube-family addons
 * (YT Music) apply exactly the same blocking under exactly the same condition as YouTube.
 */
public final class YoutubeAdBlock {
	private YoutubeAdBlock() {
	}

	/**
	 * Same condition as the YouTube addon: Android Auto build and the YouTube "skip ads"
	 * setting enabled (it defaults to on). Falls back to the build default when the YouTube
	 * addon itself is not loaded.
	 */
	public static boolean isEnabled() {
		YoutubeAddon yt = AddonManager.get().getAddon(YoutubeAddon.class);
		return (yt != null) ? yt.skipAd() : BuildConfig.AUTO;
	}

	/**
	 * Must run before the page scripts: prefers H.264 and strips ad placements from the
	 * player / next API responses so most ads are never scheduled.
	 */
	@NonNull
	public static String networkScript() {
		return YoutubeScripts.PREFER_H264 + YoutubeScripts.NETWORK_ADBLOCK;
	}

	/** Mutes and skips any ad that still reaches the player (no native event reporting). */
	@NonNull
	public static String adSkipScript() {
		return YoutubeScripts.PLAYBACK_SIGNAL + YoutubeScripts.AD_SKIP +
				"window.__fermataAdState.configure(true, 0);";
	}
}
