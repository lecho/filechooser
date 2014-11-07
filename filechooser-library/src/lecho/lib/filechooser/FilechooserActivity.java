package lecho.lib.filechooser;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Example usage:<br/><br/>
 * 
 * Intent i = new Intent(getActivity(), FilechooserActivity.class);<br/>
 * i.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);<br/>
 * i.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);<br/>
 * startActivityForResult(i, 1);<br/><br/>
 * 
 * 
 * public void onActivityResult(int requestCode, int resultCode, Intent data) {<br/>
 * if (resultCode == Activity.RESULT_OK) {<br/>
 * ArrayList<String> paths = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);<br/>
 * }<br/>
 * }<br/>
 */
public class FilechooserActivity extends FragmentActivity {
	public static final String BUNDLE_SELECTED_PATHS = "lecho.lib.filechooser:selected-paths";
	public static final String BUNDLE_ITEM_TYPE = "lecho.lib.filechoser:item-type";
	public static final String BUNDLE_SELECTION_MODE = "lecho.lib.filechooser:selection-mode";

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
