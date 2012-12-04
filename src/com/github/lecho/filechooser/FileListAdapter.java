package com.github.lecho.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author lecho
 * 
 */
public class FileListAdapter extends BaseAdapter {

	private Context mContext;
	private List<File> mObjects = new ArrayList<File>();

	public FileListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = View.inflate(mContext, R.layout.item_file_list, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		TextView name = (TextView) convertView.findViewById(R.id.name);

		File file = mObjects.get(position);
		if (file.isFile()) {
			icon.setImageResource(R.drawable.ic_menu_file);
		} else {
			icon.setImageResource(R.drawable.ic_menu_folder);
		}

		name.setText(file.getName());
		return convertView;
	}

	public void setObjects(List<File> objects) {
		mObjects = objects;
		notifyDataSetChanged();
	}

	public void clear() {
		mObjects.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public Object getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
