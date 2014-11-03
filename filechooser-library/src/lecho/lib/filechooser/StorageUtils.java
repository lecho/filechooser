package lecho.lib.filechooser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Based on http://stackoverflow.com/a/19982338/265597
 * 
 */
public class StorageUtils {

	private static final String TAG = "StorageUtils";

	public static class StorageInfo {

		public final String path;
		public final boolean readonly;
		public final boolean removable;
		public final int number;

		StorageInfo(String path, boolean readonly, boolean removable, int number) {
			this.path = path;
			this.readonly = readonly;
			this.removable = removable;
			this.number = number;
		}

		public String getDisplayName(Context context) {
			StringBuilder res = new StringBuilder();
			if (!removable) {
				res.append(context.getString(R.string.fc_internal_storage));
			} else if (number > 1) {
				res.append(context.getString(R.string.fc_external_storage) + number);
			} else {
				res.append(context.getString(R.string.fc_external_storage));
			}
			if (readonly) {
				res.append(context.getString(R.string.fc_read_only));
			}
			return res.toString();
		}
	}

	public static List<StorageInfo> getStorageList() {

		List<StorageInfo> list = new ArrayList<StorageInfo>();
		String path = Environment.getExternalStorageDirectory().getPath();
		boolean isPathRemovable = Environment.isExternalStorageRemovable();
		String pathState = Environment.getExternalStorageState();
		boolean ifPathAvailable = pathState.equals(Environment.MEDIA_MOUNTED)
				|| pathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		boolean isPathReadonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);

		HashSet<String> paths = new HashSet<String>();
		int removableNumber = 1;

		if (ifPathAvailable) {
			paths.add(path);
			list.add(new StorageInfo(path, isPathReadonly, isPathRemovable, isPathRemovable ? removableNumber++ : -1));
		}

		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader("/proc/mounts"));
			String line;
			while ((line = bufReader.readLine()) != null) {
				if (line.contains("vfat") || line.contains("/mnt")) {
					StringTokenizer tokens = new StringTokenizer(line, " ");

					tokens.nextToken(); // device

					String mountPoint = tokens.nextToken(); // mount point
					if (paths.contains(mountPoint)) {
						continue;
					}

					tokens.nextToken(); // file system

					List<String> flags = Arrays.asList(tokens.nextToken().split(",")); // flags

					boolean readonly = flags.contains("ro");

					if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
							paths.add(mountPoint);
							list.add(new StorageInfo(mountPoint, readonly, true, removableNumber++));
						}
					}
				}
			}

		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} catch (IOException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException ex) {
					Log.e(TAG, "Error listing storages", ex);
				}
			}
		}

		return list;
	}
}
