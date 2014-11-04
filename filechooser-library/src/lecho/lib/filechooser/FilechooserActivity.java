package lecho.lib.filechooser;

import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class FilechooserActivity extends FragmentActivity {

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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class FilechooserFragment extends Fragment implements LoaderCallbacks<List<File>> {
		private static final String BACK_PRESSED_BROADCAST_ACTION = "lecho.lib.filechooser:back-pressed-broadcast-action";
		private static final int LOADER_ID = 1;

		private File rootDir;
		private File currentDir;

		private PathAdapter adapter;
		private ListView listView;
		private ViewSwitcher viewSwitcher;
		private TextView currentDirView;

		private BroadcastReceiver backPressedBroadcastReceiver = new BackPressedBroadcastReceiver();

		public FilechooserFragment() {
		}

		@Override
		public void onResume() {
			super.onResume();
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(backPressedBroadcastReceiver,
					getBackPressedBroadcastIntentFilter());
		}

		@Override
		public void onPause() {
			super.onPause();
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(backPressedBroadcastReceiver);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_filechooser, container, false);

			currentDirView = (TextView) rootView.findViewById(R.id.fc_path);

			viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.fc_view_switcher);

			listView = (ListView) rootView.findViewById(R.id.fc_list);

			listView.setEmptyView(rootView.findViewById(R.id.fc_empty_view));

			return rootView;
		}

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			rootDir = new File(Environment.getExternalStorageDirectory().getParent());

			currentDir = new File(rootDir.getAbsolutePath());

			adapter = new PathAdapter(getActivity(), new PathAdapter.OnFcListItemClickListener() {

				@Override
				public void onItemClick(int position) {
					File file = (File) adapter.getItem(position);
					if (file.isDirectory() && file.canRead()) {
						currentDir = file.getAbsoluteFile();
						getLoaderManager().restartLoader(LOADER_ID, null, FilechooserFragment.this);
					}

				}
			});

			listView.setAdapter(adapter);
			listView.setItemsCanFocus(true);

			getLoaderManager().initLoader(LOADER_ID, null, this);

		}

		public static Intent getBackPressedBroadcastIntent() {
			Intent intent = new Intent(BACK_PRESSED_BROADCAST_ACTION);
			return intent;
		}

		public static IntentFilter getBackPressedBroadcastIntentFilter() {
			IntentFilter intentFilter = new IntentFilter(BACK_PRESSED_BROADCAST_ACTION);
			return intentFilter;
		}

		@Override
		public Loader<List<File>> onCreateLoader(int id, Bundle data) {
			if (LOADER_ID == id) {
				viewSwitcher.setDisplayedChild(1);
				boolean shouldParseMounts = rootDir.equals(currentDir);
				return new PathLoader(getActivity(), currentDir, shouldParseMounts);
			}
			return null;
		}

		@Override
		public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
			if (LOADER_ID == loader.getId()) {
				adapter.setObjects(data);
				viewSwitcher.setDisplayedChild(0);
			}

		}

		@Override
		public void onLoaderReset(Loader<List<File>> arg0) {
			adapter.clear();
			viewSwitcher.setDisplayedChild(0);
		}

		private class BackPressedBroadcastReceiver extends BroadcastReceiver {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (currentDir.equals(rootDir)) {
					getActivity().finish();
				} else {
					currentDir = new File(currentDir.getParent());
					getLoaderManager().restartLoader(LOADER_ID, null, FilechooserFragment.this);
				}
			}
		}
	}
}
