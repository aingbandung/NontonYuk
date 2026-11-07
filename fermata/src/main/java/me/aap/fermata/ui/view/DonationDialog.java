package me.aap.fermata.ui.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import me.aap.fermata.R;
import me.aap.fermata.ui.activity.MainActivityDelegate;

/** "Buy me a coffee" via Indonesian e-wallets (GoPay, ShopeePay, OVO). No external links. */
public final class DonationDialog {
	public static final String WALLET_NUMBER = "081227780676";

	private DonationDialog() {
	}

	public static void show(@NonNull MainActivityDelegate activity) {
		Context ctx = activity.getContext();
		activity.createDialogBuilder()
				.setTitle(R.drawable.coffee, R.string.support_coffee_title)
				.setMessage(ctx.getString(R.string.support_coffee_message, WALLET_NUMBER))
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(R.string.support_copy_number, (d, i) -> copyNumber(ctx))
				.show();
	}

	static void copyNumber(@NonNull Context ctx) {
		try {
			ClipboardManager cm = ctx.getSystemService(ClipboardManager.class);
			if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("GoPay / ShopeePay / OVO",
					WALLET_NUMBER));
			Toast.makeText(ctx, R.string.support_number_copied, Toast.LENGTH_SHORT).show();
		} catch (RuntimeException ignored) {
		}
	}
}
