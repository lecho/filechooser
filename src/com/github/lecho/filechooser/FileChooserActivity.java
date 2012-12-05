package com.github.lecho.filechooser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 
 * @author lecho
 * 
 */
public class FileChooserActivity extends Activity {

	public static final String TAG = FileChooserActivity.class.getSimpleName();
	public static final String START_PATH = "com.github.lecho:start-path";
	private static final String HOME = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mPath;
	private FileListAdapter mAdapter;
	private LoaderListener mLoaderListener = new LoaderListener();
	private TextView mPathText;
	private ImageButton mBackBtn;
	private ImageButton mHomeBtn;
	private ViewSwitcher mViewSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file_chooser);

		mPath = HOME;

		if (null != getIntent().getExtras()) {
			String path = getIntent().getExtras().getString(START_PATH);

			if (!TextUtils.isEmpty(path)) {
				mPath = path;
			}
		}

		mBackBtn = (ImageButton) findViewById(R.id.back_button);
		mBackBtn.setOnClickListener(new BackListener());
		mHomeBtn = (ImageButton) findViewById(R.id.home_button);
		mHomeBtn.setOnClickListener(new HomeListener());
		if (mPath.equals(HOME)) {
			mBackBtn.setEnabled(false);
			mHomeBtn.setEnabled(false);
		}
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);

		mPathText = (TextView) findViewById(R.id.path);
		ListView list = (ListView) findViewById(R.id.list);
		mAdapter = new FileListAdapter(getApplicationContext());
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new SelectionListener());
		loadCurrentPath();

	}

	@Override
	public void onBackPressed() {
		if (!back()) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	/**
	 * 
	 * @return false if user returned to home directory, true otherwise.
	 */
	private boolean back() {

		if (mPath.length() > HOME.length()) {
			int index = mPath.lastIndexOf("/");
			mPath = mPath.substring(0, index);
			loadCurrentPath();
			return true;
		} else {
			return false;
		}
	}

	private void home() {
		mPath = HOME;
		loadCurrentPath();
	}

	private void loadCurrentPath() {
		mViewSwitcher.showNext();
		new PathLoader(mLoaderListener).execute(mPath);
	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private interface OnPathLoadedListener {
		public void onPathLoaded(List<File> files);
	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private class LoaderListener implements OnPathLoadedListener {

		@Override
		public void onPathLoaded(List<File> files) {
			mAdapter.setObjects(files);
			mPathText.setText(mPath);
			if (mPath.equals(HOME)) {
				mBackBtn.setEnabled(false);
				mHomeBtn.setEnabled(false);
			} else {
				mBackBtn.setEnabled(true);
				mHomeBtn.setEnabled(true);
			}
			mViewSwitcher.showPrevious();

		}

	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private class SelectionListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			File file = (File) mAdapter.getItem(position);
			mPath = file.getPath();
			if (file.isDirectory()) {
				loadCurrentPath();
			} else {
				Intent data = new Intent();
				data.setData(Uri.parse(mPath));
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		}
	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private class BackListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			back();

		}

	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private class HomeListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			home();

		}

	}

	/**
	 * 
	 * @author lecho
	 * 
	 */
	private static class PathLoader extends AsyncTask<String, Void, List<File>> {

		private OnPathLoadedListener mListener;

		public PathLoader(OnPathLoadedListener listener) {
			mListener = listener;
		}

		@Override
		protected List<File> doInBackground(String... params) {
			List<File> result = new ArrayList<File>();
			if (params.length != 1) {
				Log.e(TAG, "Could not load directory with empty path");
				return result;
			}

			File dir = new File(params[0]);
			File[] dirs = dir.listFiles(sSystemDirsFilter);
			if (null != dirs) {
				Arrays.sort(dirs, sPathnameComparator);
				for (File file : dirs) {
					result.add(file);
				}
			}

			File[] files = dir.listFiles(sSystemFilesFilter);
			if (null != files) {
				Arrays.sort(files, sPathnameComparator);
				for (File file : files) {
					result.add(file);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<File> result) {
			super.onPostExecute(result);
			mListener.onPathLoaded(result);

		}

	}

	private static final FileFilter sSystemDirsFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {

			return pathname.isDirectory() && !pathname.getName().startsWith(".");
		}

	};

	private static final FileFilter sSystemFilesFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {

			return pathname.isFile() && !pathname.getName().startsWith(".");
		}

	};

	private static final Comparator<File> sPathnameComparator = new Comparator<File>() {

		@Override
		public int compare(File lhs, File rhs) {
			return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
		}

	};

}
