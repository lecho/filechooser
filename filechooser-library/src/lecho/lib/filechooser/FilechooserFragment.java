package lecho.lib.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.filechooser.PathAdapter.FcCheckboxValidator;
import lecho.lib.filechooser.PathAdapter.OnFcItemCheckListener;
import lecho.lib.filechooser.PathAdapter.OnFcListItemClickListener;
import lecho.lib.filechooser.PathAdapter.OnFcListItemLongClickListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * A placeholder fragment containing a simple view.
 */
public class FilechooserFragment extends Fragment implements LoaderCallbacks<List<File>> {
	private static final String BACK_PRESSED_BROADCAST_ACTION = "lecho.lib.filechooser:back-pressed-broadcast-action";
	private static final String BUNDLE_CURRENT_DIR = "lecho.lib.filechooser:bundle-current-dir";
	private static final String BUNDLE_CURRENT_SELECTIONS = "lecho.lib.filechooser:bundle-current-selection";

	private static final int LOADER_ID = 1;

	private File rootDir;
	private File currentDir;

	private PathAdapter adapter;
	private ListView listView;
	private ViewSwitcher viewSwitcher;
	private TextView currentDirView;
	private Button buttonCancel;
	private Button buttonConfirm;

	private ItemType itemType = ItemType.FILE;
	private SelectionMode selectionMode = SelectionMode.SINGLE_ITEM;

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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BUNDLE_CURRENT_DIR, currentDir.getAbsolutePath());

		int size = adapter.getCheckedPositions().size();
		int[] checkedPositions = new int[size];
		for (int i = 0; i < size; ++i) {
			checkedPositions[i] = adapter.getCheckedPositions().keyAt(i);
		}
		outState.putIntArray(BUNDLE_CURRENT_SELECTIONS, checkedPositions);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_filechooser, container, false);

		currentDirView = (TextView) rootView.findViewById(R.id.fc_path);

		viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.fc_view_switcher);

		listView = (ListView) rootView.findViewById(R.id.fc_list);

		listView.setEmptyView(rootView.findViewById(R.id.fc_empty_view));

		buttonCancel = (Button) rootView.findViewById(R.id.fc_button_cancel);
		buttonCancel.setOnClickListener(new CancelButtonClickListener());

		buttonConfirm = (Button) rootView.findViewById(R.id.fc_button_confirm);
		buttonConfirm.setOnClickListener(new ConfirmButtonClickListener());
		buttonConfirm.setEnabled(false);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		rootDir = new File(Environment.getExternalStorageDirectory().getParent());

		if (null == savedInstanceState) {
			currentDir = new File(rootDir.getAbsolutePath());
		} else {
			String currentDirPath = savedInstanceState.getString(BUNDLE_CURRENT_DIR);
			if (null == currentDirPath) {
				currentDir = new File(rootDir.getAbsolutePath());
			} else {
				currentDir = new File(currentDirPath);
			}
		}

		currentDirView.setText(currentDir.getName());

		OnFcListItemClickListener itemClickListener = new DefaultListItemClickListener();

		OnFcListItemLongClickListener itemLongClickListener = new ItemLongClickListener();

		// Check selection mode
		SelectionMode tempSelectionMode = (SelectionMode) getActivity().getIntent().getSerializableExtra(
				FilechooserActivity.SELECTION_MODE);
		if (null != tempSelectionMode) {
			this.selectionMode = tempSelectionMode;
		}

		OnFcItemCheckListener checkListener;
		if (SelectionMode.MULTIPLE_ITEM.equals(selectionMode)) {
			checkListener = new MultipleItemCheckListener();
		} else {
			checkListener = new SingleItemCheckListener();
		}

		// Check item type
		ItemType tempItemType = (ItemType) getActivity().getIntent()
				.getSerializableExtra(FilechooserActivity.ITEM_TYPE);
		if (null != tempItemType) {
			this.itemType = tempItemType;
		}

		FcCheckboxValidator checkboxValidator;
		if (ItemType.FILE.equals(itemType)) {
			checkboxValidator = new FileOnlyCheckboxValidator();
		} else if (ItemType.DIRECTORY.equals(itemType)) {
			checkboxValidator = new DirectoryOnlyCheckboxValidator();
		} else {
			checkboxValidator = new AllItemsVisibleCheckboxValidator();
		}

		// Set up adapter
		adapter = new PathAdapter(getActivity(), itemClickListener, itemLongClickListener, checkListener,
				checkboxValidator);

		// Restore saved checked positions
		if (null != savedInstanceState) {
			int[] checkedPositions = savedInstanceState.getIntArray(BUNDLE_CURRENT_SELECTIONS);
			if (null != checkedPositions) {
				for (int position : checkedPositions) {
					adapter.checkPosition(position, true);
				}
			}
		}

		// Set up list
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);

		getLoaderManager().initLoader(LOADER_ID, null, this);

	}

	private void loadDirectory(File dir) {
		currentDir = dir;
		currentDirView.setText(currentDir.getName());
		clearAllSelectedItems();
		getLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	private void clearAllSelectedItems() {
		int size = adapter.getCheckedPositions().size();

		for (int i = 0; i < size; ++i) {
			int position = adapter.getCheckedPositions().keyAt(i);
			adapter.checkPosition(position, false);
		}
	}

	private void clearOtherSelectedItems(int positionToSkip) {
		int size = adapter.getCheckedPositions().size();

		for (int i = 0; i < size; ++i) {
			int position = adapter.getCheckedPositions().keyAt(i);

			if (position != positionToSkip) {
				adapter.checkPosition(position, false);
			}
		}
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
				loadDirectory(new File(currentDir.getParent()));
			}
		}
	}

	private class CancelButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			ArrayList<String> paths = new ArrayList<String>(0);
			Intent data = new Intent();
			data.putStringArrayListExtra(FilechooserActivity.SELECTED_PATHS, paths);
			getActivity().setResult(Activity.RESULT_CANCELED, data);
			getActivity().finish();
		}

	}

	private class ConfirmButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int pathsSize = adapter.getCheckedPositions().size();
			ArrayList<String> paths = new ArrayList<String>(pathsSize);

			for (int i = 0; i < pathsSize; ++i) {
				int position = adapter.getCheckedPositions().keyAt(i);
				paths.add(((File) adapter.getItem(position)).getAbsolutePath());
			}

			Intent data = new Intent();
			data.putStringArrayListExtra(FilechooserActivity.SELECTED_PATHS, paths);
			getActivity().setResult(Activity.RESULT_OK, data);
			getActivity().finish();
		}
	}

	private class DefaultListItemClickListener implements OnFcListItemClickListener {

		@Override
		public void onItemClick(int position, File file) {

			if (file.isDirectory()) {

				onDirectoryClick(position, file);

			} else if (file.isFile()) {

				onFileClick(position, file);

			}

		}

		protected void onDirectoryClick(int position, File file) {
			loadDirectory(file);
		}

		protected void onFileClick(int position, File file) {
			if (ItemType.FILE.equals(itemType) || ItemType.ALL.equals(itemType)) {

				boolean isChecked = !adapter.getCheckedPositions().get(position);
				adapter.checkPosition(position, isChecked);

				if (isChecked && SelectionMode.SINGLE_ITEM.equals(selectionMode)) {
					clearOtherSelectedItems(position);
				}

				adapter.notifyDataSetChanged();
			}
		}

	}

	// *** Item selection listeners ***//

	private class MultipleItemCheckListener implements OnFcItemCheckListener {

		@Override
		public void onItemCheck(int position, boolean isChecked) {
			if (adapter.getCheckedPositions().size() > 0) {
				buttonConfirm.setEnabled(true);
			} else {
				buttonConfirm.setEnabled(false);
			}

		}
	}

	private class SingleItemCheckListener extends MultipleItemCheckListener {

		@Override
		public void onItemCheck(int position, boolean isChecked) {
			if (isChecked && SelectionMode.SINGLE_ITEM.equals(selectionMode)) {
				clearOtherSelectedItems(position);
				adapter.notifyDataSetChanged();
			}

			super.onItemCheck(position, isChecked);

		}
	}

	// *** Checkbox visibility validators ***//

	private class FileOnlyCheckboxValidator implements PathAdapter.FcCheckboxValidator {

		@Override
		public boolean isCheckboxVisible(int positon, File file) {
			// TODO Auto-generated method stub
			if (file.isFile()) {
				return true;
			}

			return false;
		}
	}

	private class DirectoryOnlyCheckboxValidator implements PathAdapter.FcCheckboxValidator {

		@Override
		public boolean isCheckboxVisible(int positon, File file) {
			if (file.isDirectory()) {
				return true;
			}

			return false;
		}

	}

	private class AllItemsVisibleCheckboxValidator implements PathAdapter.FcCheckboxValidator {

		@Override
		public boolean isCheckboxVisible(int positon, File file) {
			return true;
		}

	}

	// *** Long item click listeners ***//
	private class ItemLongClickListener implements OnFcListItemLongClickListener {

		@Override
		public boolean onItemLongClick(int position, File file) {
			// TODO start directory/file details dialog fragment
			return false;
		}

	}
}