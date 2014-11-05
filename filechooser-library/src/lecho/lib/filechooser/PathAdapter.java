package lecho.lib.filechooser;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class PathAdapter extends BaseAdapter {
	private static final long LENGTH_BYTES_CEIL = 1024;
	private static final long LENGTH_KBYTES_CEIL = 1024 * 1024;
	private static final long LENGTH_MBYTES_CEIL = 1024 * 1024 * 1024;
	// private static final long LENGTH_GBYTES_CEIL = 1024 * 1024 * 1024 * 1024;
	private static final String LENGTH_UNIG_B = "B";
	private static final String LENGTH_UNIT_KB = "KB";
	private static final String LENGTH_UNIT_MB = "MB";
	private static final String LENGTH_UNIT_GB = "GB";
	private NumberFormat numberFormat;
	private DateFormat dateFormat;
	private Context context;
	private List<File> mObjects = new ArrayList<File>();
	private OnFcListItemClickListener itemClickListener = new DummyOnFcListItemClickListener();
	private SparseBooleanArray checkedPositions = new SparseBooleanArray();

	public PathAdapter(Context context, OnFcListItemClickListener itemClickListener) {
		this.context = context;
		numberFormat = NumberFormat.getInstance();
		dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);

		if (null != itemClickListener) {
			this.itemClickListener = itemClickListener;
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (null == convertView) {
			convertView = View.inflate(context, R.layout.fc_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.fc_item_name);
			viewHolder.details1 = (TextView) convertView.findViewById(R.id.fc_details1);
			viewHolder.details2 = (TextView) convertView.findViewById(R.id.fc_details2);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.fc_checkbox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		File file = mObjects.get(position);

		viewHolder.name.setText(file.getName());

		if (file.isFile()) {
			long length = file.length();

			StringBuilder details1Text = new StringBuilder(context.getString(R.string.fc_file)).append(" ");

			FileSizeDivider divider = getFileSizeDivider(length);

			BigDecimal dividedLength = new BigDecimal(length).divide(divider.div, 2, BigDecimal.ROUND_CEILING);

			details1Text.append(numberFormat.format(dividedLength.doubleValue()));
			details1Text.append(divider.unitText);

			viewHolder.details1.setText(details1Text.toString());

		} else {
			viewHolder.details1.setText(R.string.fc_directory);
		}

		// last modification date
		Date modifiedDate = new Date(file.lastModified());
		StringBuilder details2Text = new StringBuilder().append(dateFormat.format(modifiedDate));
		viewHolder.details2.setText(details2Text.toString());

		viewHolder.checkBox.setOnCheckedChangeListener(new OnFcListItemCheckedChangeListener(position));

		convertView.setOnClickListener(new OnFcListItemViewClickListener(position));

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

	public SparseBooleanArray getCheckedPositions() {
		return checkedPositions;
	}

	private FileSizeDivider getFileSizeDivider(long fileLength) {

		FileSizeDivider divider = new FileSizeDivider();

		if (fileLength < LENGTH_BYTES_CEIL) {
			divider.div = new BigDecimal(1);
			divider.unitText = LENGTH_UNIG_B;

		} else if (fileLength < LENGTH_KBYTES_CEIL) {
			divider.div = new BigDecimal(LENGTH_BYTES_CEIL);
			divider.unitText = LENGTH_UNIT_KB;

		} else if (fileLength < LENGTH_MBYTES_CEIL) {
			divider.div = new BigDecimal(LENGTH_KBYTES_CEIL);
			divider.unitText = LENGTH_UNIT_MB;

		} else {
			divider.div = new BigDecimal(LENGTH_MBYTES_CEIL);
			divider.unitText = LENGTH_UNIT_GB;
		}

		return divider;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView details1;
		public TextView details2;
		public CheckBox checkBox;
	}

	private static class FileSizeDivider {
		public BigDecimal div;
		public String unitText;
	}

	private class OnFcListItemViewClickListener implements View.OnClickListener {

		private int position;

		public OnFcListItemViewClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			PathAdapter.this.itemClickListener.onItemClick(position);
		}

	}

	private class OnFcListItemCheckedChangeListener implements OnCheckedChangeListener {

		private int position;

		public OnFcListItemCheckedChangeListener(int position) {
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				checkedPositions.put(position, isChecked);
			} else {
				checkedPositions.delete(position);
			}

		}

	}

	public interface OnFcListItemClickListener {
		public void onItemClick(int position);
	}

	private static class DummyOnFcListItemClickListener implements OnFcListItemClickListener {

		@Override
		public void onItemClick(int position) {

		}
	}
}
