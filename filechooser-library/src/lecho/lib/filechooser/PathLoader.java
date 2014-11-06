package lecho.lib.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lecho.lib.filechooser.StorageUtils.StorageInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;

public class PathLoader extends AsyncTaskLoader<List<File>> {

	private File dir;

	private List<File> data;

	private PathComparator pathComparator = new PathComparator();

	private PathObserver pathObserver;

	private boolean shouldParseMounts;

	public PathLoader(Context context, File dir, boolean shouldParseMounts) {
		super(context);
		this.dir = dir;
		this.shouldParseMounts = shouldParseMounts;
	}

	@Override
	public List<File> loadInBackground() {

		List<File> files = new ArrayList<File>();

		if (shouldParseMounts) {

			List<StorageInfo> storages = StorageUtils.getStorageList();

			for (StorageInfo storageInfo : storages) {
				files.add(new File(storageInfo.path));
			}

		} else {

			File[] filesTab = dir.listFiles();
			// TODO filters

			if (null != filesTab) {

				Arrays.sort(filesTab, pathComparator);

				for (File file : filesTab) {
					files.add(file);
				}
			}
		}

		return files;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class will take care of delivering it; the
	 * implementation here just adds a little more logic.
	 */
	@Override
	public void deliverResult(List<File> data) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (data != null) {
				onReleaseResources(data);
			}
		}
		List<File> oldData = this.data;
		this.data = data;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}

		// At this point we can release the resources associated with
		// 'oldData' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldData != null && oldData != data) {
			onReleaseResources(oldData);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (this.data != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(this.data);
		}

		// Start watching for changes in the app data.
		if (pathObserver == null) {
			pathObserver = new PathObserver(this, dir.getAbsolutePath());
			pathObserver.startWatching();
		}
		if (null == this.data || takeContentChanged()) {
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<File> data) {
		super.onCanceled(data);
		onReleaseResources(data);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (this.data != null) {
			onReleaseResources(this.data);
			this.data = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		if (pathObserver != null) {
			pathObserver.stopWatching();
			pathObserver = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an actively loaded data set.
	 */
	protected void onReleaseResources(List<File> data) {
	}

	/**
	 * Compare two files in terms of path alphabetical order.
	 * 
	 * TODO natural sorting
	 */
	private static class PathComparator implements Comparator<File> {

		@SuppressLint("DefaultLocale")
		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.isDirectory() && rhs.isFile()) {
				return -1;
			}

			if (lhs.isFile() && rhs.isDirectory()) {
				return 1;
			}

			//TODO natural sort
			return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
		}

	}

	private static class PathObserver extends FileObserver {

		private static final int MASK = FileObserver.CREATE | FileObserver.DELETE | FileObserver.DELETE_SELF
				| FileObserver.MOVED_FROM | FileObserver.MOVED_TO | FileObserver.MODIFY | FileObserver.MOVE_SELF;

		private PathLoader pathLoader;

		public PathObserver(PathLoader pathLoader, String path) {
			super(path, MASK);
			this.pathLoader = pathLoader;
		}

		@Override
		public void onEvent(int event, String path) {
			pathLoader.onContentChanged();

		}

	}

}
