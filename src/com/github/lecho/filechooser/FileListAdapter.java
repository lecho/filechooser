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
			convertView = View.inflate(mContext, R.layout.filechooser_item_file_list, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		TextView name = (TextView) convertView.findViewById(R.id.name);

		File file = mObjects.get(position);
		if (file.isFile()) {
			icon.setImageResource(R.drawable.filechooser_ic_file);
		} else {
			icon.setImageResource(R.drawable.filechooser_ic_folder);
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
