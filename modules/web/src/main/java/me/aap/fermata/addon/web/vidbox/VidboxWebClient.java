package me.aap.fermata.addon.web.vidbox;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import me.aap.fermata.addon.web.FermataWebClient;
import me.aap.utils.log.Log;

/**
 * Keeps the page on Vidbox. Embedded players (iframes) on third-party hosts still load,
 * but main-frame redirects to other sites (pop-under ads) and app-launch schemes
 * (intent://, market://) are dropped so the car screen never leaves Vidbox.
 */
final class VidboxWebClient extends FermataWebClient {
	VidboxWebClient() {
		super();
	}

	private VidboxWebClient(DiagnosticsObserver observer) {
		super(observer);
	}

	@Override
	protected FermataWebClient newReplacement() {
		return new VidboxWebClient(getDiagnosticsObserver());
	}

	@Override
	public boolean shouldOverrideUrlLoading(@NonNull WebView view,
			@NonNull WebResourceRequest request) {
		Uri uri = request.getUrl();
		if (uri == null) return false;
		String scheme = uri.getScheme();
		if ((scheme != null) && !scheme.equalsIgnoreCase("http") &&
				!scheme.equalsIgnoreCase("https")) {
			return true;
		}
		if (!request.isForMainFrame()) return false;
		if (VidboxAddon.isVidboxHost(uri.getHost())) return false;
		Log.d("Vidbox: blocked main-frame navigation to ", uri);
		return true;
	}
}
