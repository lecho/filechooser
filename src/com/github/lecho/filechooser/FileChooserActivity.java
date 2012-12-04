package com.github.lecho.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author lecho
 * 
 */
public class FileChooserActivity extends Activity {

	public static final String TAG = FileChooserActivity.class.getSimpleName();
	public static final String START_PATH = "com.github.lecho:start-path";
	private String mPath;
	private FileListAdapter mAdapter;
	private PathLoader mLoader = new PathLoader(new LoaderListener());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file_chooser);

		mPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		if (null != getIntent().getExtras()) {
			String path = getIntent().getExtras().getString(START_PATH);

			if (!TextUtils.isEmpty(path)) {
				mPath = path;
			}
		}

		ListView list = (ListView) findViewById(R.id.list);
		mAdapter = new FileListAdapter(getApplicationContext());
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new SelectionListener());
		loadCurrentPath();

	}

	private void loadCurrentPath() {
		mLoader.execute(mPath);
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
			File[] files = dir.listFiles();
			if (null != files) {
				for (File file : dir.listFiles()) {
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

}
