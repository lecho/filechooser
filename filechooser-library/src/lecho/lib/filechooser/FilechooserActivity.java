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

		private PathAdapter mAdapter;
		private ListView mListView;
		private ViewSwitcher mViewSwitcher;
		private TextView mCurrentPath;

		public FilechooserFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_filechooser, container, false);

			mCurrentPath = (TextView) rootView.findViewById(R.id.fc_path);

			mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.fc_view_switcher);

			mListView = (ListView) rootView.findViewById(R.id.fc_list);

			return rootView;
		}

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mAdapter = new PathAdapter(getActivity());
			mListView.setAdapter(mAdapter);

			getLoaderManager().initLoader(LOADER_ID, null, this);

		}

		@Override
		public Loader<List<File>> onCreateLoader(int id, Bundle data) {
			if (LOADER_ID == id) {
				mViewSwitcher.setDisplayedChild(1);
				return new PathLoader(getActivity(), Environment.getExternalStorageDirectory());
			}
			return null;
		}

		@Override
		public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
			if (LOADER_ID == loader.getId()) {
				mAdapter.setObjects(data);
				mAdapter.notifyDataSetChanged();
				mViewSwitcher.setDisplayedChild(0);
			}

		}

		@Override
		public void onLoaderReset(Loader<List<File>> arg0) {
			mViewSwitcher.setDisplayedChild(0);

		}
	}
}
