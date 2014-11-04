package lecho.lib.filechooser;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
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
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new FilechooserFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class FilechooserFragment extends Fragment implements LoaderCallbacks<List<File>> {

		private static final int LOADER_ID = 1;

		private File rootDir;
		private File currentDir;

		private PathAdapter adapter;
		private ListView listView;
		private ViewSwitcher viewSwitcher;
		private TextView currentDirView;

		public FilechooserFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_filechooser, container, false);

			currentDirView = (TextView) rootView.findViewById(R.id.fc_path);

			viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.fc_view_switcher);

			listView = (ListView) rootView.findViewById(R.id.fc_list);

			return rootView;
		}

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			rootDir = new File(Environment.getExternalStorageDirectory().getParent());

			currentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

			adapter = new PathAdapter(getActivity(), new PathAdapter.OnFcListItemClickListener() {

				@Override
				public void onItemClick(int position) {
					File file = (File)adapter.getItem(position);
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

		@Override
		public Loader<List<File>> onCreateLoader(int id, Bundle data) {
			if (LOADER_ID == id) {
				viewSwitcher.setDisplayedChild(1);
				//TODO check if should parse mounts
				return new PathLoader(getActivity(), currentDir, false);
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
	}
}
