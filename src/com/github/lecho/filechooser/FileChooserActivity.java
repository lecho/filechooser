/*
 * Copyright (C) 2012 Leszek Wach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lecho.filechooser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Usage:
 * 
 * Starting activity:
 * 
 * Intent intent = new Intent(context, FileChooserActivity.class);
 * startActivityForResult(intent, someRequestCode);
 * 
 * 
 * Retrieving path in onActivityResult:
 * 
 * Uri fileUri = result.getData;
 * 
 * @author lecho
 * 
 */
public class FileChooserActivity extends Activity {

	public static final String TAG = FileChooserActivity.class.getSimpleName();
	public static final String START_PATH = "com.github.lechofilechooser:start-path";

	private static final String HOME = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mPath;
	private FileListAdapter mAdapter;
	private LoaderListener mLoaderListener = new LoaderListener();
	private TextView mPathText;
	private ImageButton mBackBtn;
	private ImageButton mHomeBtn;
	private ImageButton mCloseBtn;
	private ViewSwitcher mViewSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filechooser_activity_file_chooser);
		mPath = HOME;
		if (null != getIntent().getExtras()) {
			String path = getIntent().getExtras().getString(START_PATH);

			if (!TextUtils.isEmpty(path) && path.length() >= HOME.length()) {
				mPath = path;
			}
		}
		if (null != savedInstanceState) {
			String path = savedInstanceState.getString(START_PATH);
			if (!TextUtils.isEmpty(path) && path.length() >= HOME.length()) {
				mPath = path;
			}
		}

		mCloseBtn = (ImageButton) findViewById(R.id.close_button);
		mCloseBtn.setOnClickListener(new CloseListener());
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
		mPathText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast toast = Toast.makeText(getApplicationContext(), mPath, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
		});

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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(START_PATH, mPath);
		super.onSaveInstanceState(outState);
	}

	/*
	 * Returns false if user returned to home directory, true otherwise.
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

	/*
	 * Loads files under current path. TODO if you want to add FileObserver,
	 * onEvent method should call this method.
	 */
	private void loadCurrentPath() {
		new PathLoader(mLoaderListener).execute(mPath);
	}

	/*
	 * 
	 */
	private interface OnPathLoadedListener {
		public void onLoading();

		public void onPathLoaded(List<File> files);
	}

	/**
	 * Called when files under current path are fully loaded, hides progress bar
	 * and shows list view.
	 * 
	 * @author lecho
	 * 
	 */
	private class LoaderListener implements OnPathLoadedListener {
		@Override
		public void onLoading() {
			mViewSwitcher.showNext();

		}

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
	 * Called when list item is selected.
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
				// if selected item is file then return path
				Intent data = new Intent();
				data.setData(Uri.parse(mPath));
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		}
	}

	/**
	 * Called when user click up button.
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
	 * Called when user click home button.
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
	 * Called when user click close button.
	 * 
	 * @author lecho
	 * 
	 */
	private class CloseListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();

		}

	}

	/**
	 * Loads files list under current path.
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
		protected void onPreExecute() {
			super.onPreExecute();
			mListener.onLoading();
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

	/**
	 * Filter for non system specific files.
	 */
	private static final FileFilter sSystemDirsFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {

			return pathname.isDirectory() && !pathname.getName().startsWith(".");
		}

	};

	/**
	 * Filter for non system specific directories.
	 */
	private static final FileFilter sSystemFilesFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {

			return pathname.isFile() && !pathname.getName().startsWith(".");
		}

	};

	/**
	 * Compare two files in terms of path alphabetical order.
	 */
	private static final Comparator<File> sPathnameComparator = new Comparator<File>() {

		@SuppressLint("DefaultLocale")
		@Override
		public int compare(File lhs, File rhs) {
			return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
		}

	};

}
