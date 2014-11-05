package lecho.lib.filechooser;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

public class FilechooserActivity extends FragmentActivity {
	public static final String SELECTED_PATHS = "lecho.lib.filechooser:selected-paths";
	public static final String ITEM_TYPE = "lecho.lib.filechoser:item-type";
	public static final String SELECTION_MODE = "lecho.lib.filechooser:selection-mode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filechooser);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fc_container, new FilechooserFragment()).commit();
		}
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		// Propagate backpressed event to fragment.
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
				FilechooserFragment.getBackPressedBroadcastIntent());
	}
}
