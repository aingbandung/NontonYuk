package me.aap.fermata.addon.web.ytmusic;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import me.aap.fermata.addon.web.FermataWebClient;

/**
 * Keeps YT Music (and the Google sign-in pages it uses) inside this WebView instead of
 * handing music.youtube.com over to the YouTube addon, and drops "open in app" links.
 */
final class YtMusicWebClient extends FermataWebClient {
	YtMusicWebClient() {
		super();
	}

	private YtMusicWebClient(DiagnosticsObserver observer) {
		super(observer);
	}

	@Override
	protected FermataWebClient newReplacement() {
		return new YtMusicWebClient(getDiagnosticsObserver());
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if (view instanceof YtMusicWebView ytm) ytm.onMainFrameStarted();
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public boolean shouldOverrideUrlLoading(@NonNull WebView view,
			@NonNull WebResourceRequest request) {
		Uri uri = request.getUrl();
		if (uri == null) return false;
		String scheme = uri.getScheme();
		if ((scheme != null) && !scheme.equalsIgnoreCase("http") &&
				!scheme.equalsIgnoreCase("https")) {
			// intent://, vnd.youtube.music:// etc. would launch the phone app, not the car view.
			return true;
		}
		if (YtMusicScripts.isYtMusicHost(uri.getHost())) return false;
		return super.shouldOverrideUrlLoading(view, request);
	}
}
