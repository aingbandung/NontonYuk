package me.aap.fermata.addon.web.ytmusic;

import androidx.annotation.NonNull;

import me.aap.fermata.addon.web.yt.YoutubeAdBlock;

/** Scripts injected into music.youtube.com. */
final class YtMusicScripts {
	static final String ORIGIN = "https://music.youtube.com";

	/**
	 * Keeps the page "visible" so music does not stop when another FermataX screen or Google
	 * Maps covers the WebView, the same way the YouTube addon keeps playing in the car.
	 */
	static final String KEEP_VISIBLE = """
			(function() {
			  if (window.__fermataYtmVisible) return;
			  window.__fermataYtmVisible = true;
			  try {
			    Object.defineProperty(document, 'hidden', {configurable: true, get: function() { return false; }});
			    Object.defineProperty(document, 'visibilityState', {configurable: true, get: function() { return 'visible'; }});
			    Object.defineProperty(document, 'webkitHidden', {configurable: true, get: function() { return false; }});
			  } catch (e) {}
			  var block = function(e) { e.stopImmediatePropagation(); };
			  document.addEventListener('visibilitychange', block, true);
			  document.addEventListener('webkitvisibilitychange', block, true);
			})();
			""";

	/** Hides ad slots and Premium upsell banners; ad video frames are made invisible. */
	static final String ADBLOCK_CSS = """
			(function() {
			  var cssId = 'fermata-ytm-adblock-css';
			  var css = '.video-ads, .ytp-ad-module, .ytp-ad-player-overlay, .ytp-ad-overlay-container, ' +
			      '.ytp-ad-image-overlay, .ytp-ad-text-overlay, #player-ads, ' +
			      'ytmusic-mealbar-promo-renderer, ytmusic-statement-banner-renderer, ' +
			      'ytmusic-display-ad-renderer, ytmusic-promoted-sparkles-web-renderer, ' +
			      'ad-slot-renderer { display: none !important; } ' +
			      '.html5-video-player.ad-showing video, video.ad-showing { opacity: 0 !important; } ' +
			      'ytmusic-button-renderer:has(a[href^="intent:"]) { display: none !important; }';
			  function inject() {
			    if (document.getElementById(cssId)) return;
			    var head = document.head || document.documentElement;
			    if (!head) return;
			    var s = document.createElement('style');
			    s.id = cssId;
			    s.textContent = css;
			    head.appendChild(s);
			  }
			  inject();
			  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', inject);
			})();
			""";

	/** Answers YT Music's "Are you still there?" prompt so long drives are not interrupted. */
	static final String STILL_THERE = """
			(function() {
			  if (window.__fermataYtmStillThere) return;
			  window.__fermataYtmStillThere = setInterval(function() {
			    var dlg = document.querySelector('ytmusic-you-there-renderer');
			    if (!dlg) return;
			    var btn = dlg.querySelector('button, tp-yt-paper-button, yt-button-renderer');
			    if (!btn) return;
			    btn.click();
			    var v = document.querySelector('video');
			    if (v && v.paused) try { v.play(); } catch (e) {}
			  }, 2000);
			})();
			""";

	private YtMusicScripts() {
	}

	/** Runs at document start (before YT Music's own scripts). */
	@NonNull
	static String documentStart(boolean adBlock) {
		return adBlock ? (KEEP_VISIBLE + YoutubeAdBlock.networkScript()) : KEEP_VISIBLE;
	}

	/** Runs once the page has loaded. Every part is idempotent. */
	@NonNull
	static String pageLoaded(boolean adBlock) {
		String common = KEEP_VISIBLE + STILL_THERE;
		if (!adBlock) {
			return common + "(function(){var s=document.getElementById('fermata-ytm-adblock-css');" +
					"if(s)s.remove();if(window.__fermataAdState)window.__fermataAdState.enabled=false;})();";
		}
		return common + YoutubeAdBlock.networkScript() + ADBLOCK_CSS + YoutubeAdBlock.adSkipScript();
	}

	static boolean isYtMusicUrl(String url) {
		if (url == null) return false;
		try {
			String host = android.net.Uri.parse(url).getHost();
			return isYtMusicHost(host);
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	static boolean isYtMusicHost(String host) {
		return (host != null) && host.toLowerCase(java.util.Locale.ROOT).equals("music.youtube.com");
	}
}
