package me.aap.fermata.addon.web.ytmusic;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.webkit.ScriptHandler;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import java.util.Set;

import me.aap.fermata.addon.web.FermataChromeClient;
import me.aap.fermata.addon.web.FermataWebClient;
import me.aap.fermata.addon.web.FermataWebView;
import me.aap.fermata.addon.web.WebBrowserAddon;
import me.aap.fermata.addon.web.yt.YoutubeAdBlock;
import me.aap.utils.log.Log;

/** WebView hosting music.youtube.com with the YouTube ad blocking applied. */
public final class YtMusicWebView extends FermataWebView {
	@Nullable
	private ScriptHandler documentStartScript;
	private boolean documentStartAdBlock;

	public YtMusicWebView(Context context) {
		super(context);
	}

	public YtMusicWebView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public YtMusicWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(WebBrowserAddon addon, FermataWebClient webClient,
			FermataChromeClient chromeClient) {
		super.init(addon, webClient, chromeClient);
		setBackgroundColor(0xFF000000);
		installDocumentStartScript(YoutubeAdBlock.isEnabled());
	}

	/** True when the scripts are registered to run before YT Music's own code. */
	boolean hasDocumentStartScript() {
		return documentStartScript != null;
	}

	/** Called before every main-frame page start; re-registers if the ad setting changed. */
	void onMainFrameStarted() {
		boolean adBlock = YoutubeAdBlock.isEnabled();
		if ((documentStartScript != null) && (adBlock != documentStartAdBlock)) {
			installDocumentStartScript(adBlock);
		}
		if (documentStartScript == null) {
			// Older WebView without document-start support: inject as early as we can.
			evaluateJavascript(YtMusicScripts.documentStart(adBlock), null);
		}
	}

	private void installDocumentStartScript(boolean adBlock) {
		if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) return;
		removeDocumentStartScript();
		try {
			documentStartScript = WebViewCompat.addDocumentStartJavaScript(this,
					YtMusicScripts.documentStart(adBlock), Set.of(YtMusicScripts.ORIGIN));
			documentStartAdBlock = adBlock;
		} catch (RuntimeException error) {
			Log.w(error, "YT Music document start script registration failed");
		}
	}

	private void removeDocumentStartScript() {
		ScriptHandler h = documentStartScript;
		documentStartScript = null;
		if (h != null) {
			try {
				h.remove();
			} catch (RuntimeException ignored) {
			}
		}
	}

	@Override
	protected void pageLoaded(String url) {
		super.pageLoaded(url);
		if (YtMusicScripts.isYtMusicUrl(url)) {
			evaluateJavascript(YtMusicScripts.pageLoaded(YoutubeAdBlock.isEnabled()), null);
		}
	}

	@Override
	public void destroy() {
		removeDocumentStartScript();
		super.destroy();
	}
}
