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

package lecho.lib.filechooser;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
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
	private static final String LENGTH_UNIT_KB = " KB";
	private static final String DIR = "<DIR>";
	private static final BigDecimal LENGTH_DIV = new BigDecimal(1024);
	private NumberFormat mNumberFormat;
	private DateFormat mDateFormat;
	private DateFormat mTimeFormat;
	private Context mContext;
	private List<File> mObjects = new ArrayList<File>();

	public FileListAdapter(Context context) {
		mContext = context;
		mNumberFormat = NumberFormat.getInstance();
		mDateFormat = android.text.format.DateFormat.getDateFormat(mContext);
		mTimeFormat = android.text.format.DateFormat.getTimeFormat(mContext);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = View.inflate(mContext, R.layout.fc_item_file_list, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.fc_item_icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.fc_item_name);
			viewHolder.details1 = (TextView) convertView.findViewById(R.id.fc_details1);
			viewHolder.details2 = (TextView) convertView.findViewById(R.id.fc_details2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		File file = mObjects.get(position);
		if (file.isFile()) {
			viewHolder.icon.setImageResource(R.drawable.filechooser_ic_file);
			BigDecimal length = new BigDecimal(file.length());
			length = length.divide(LENGTH_DIV, 0, BigDecimal.ROUND_CEILING);
			String formLength = mNumberFormat.format(length.longValue());
			viewHolder.details1.setText(formLength + LENGTH_UNIT_KB);

		} else {
			viewHolder.icon.setImageResource(R.drawable.filechooser_ic_folder);
			viewHolder.details1.setText(DIR);
		}

		// last modification date
		StringBuilder lastModified = new StringBuilder().append(mDateFormat.format(new Date(file.lastModified())))
				.append(" ").append(mTimeFormat.format(new Date(file.lastModified())));
		viewHolder.details2.setText(lastModified);

		viewHolder.name.setText(file.getName());
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

	// maybe with api 14+ can be replaced set setTag(id, View) which uses sparse
	// array instead
	// of global hashmap
	private static class ViewHolder {
		public ImageView icon;// R.id.icon
		public TextView name;// R.id.name
		public TextView details1;// R.id.details1
		public TextView details2;// R.id.details2

	}
}
