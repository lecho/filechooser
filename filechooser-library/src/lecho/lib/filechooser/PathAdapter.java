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
import android.widget.TextView;
import android.widget.Toast;

public class PathAdapter extends BaseAdapter {
	private static final long LENGTH_BYTES_CEIL = 1024;
	private static final long LENGTH_KBYTES_CEIL = 1024 * 1024;
	private static final long LENGTH_MBYTES_CEIL = 1024 * 1024 * 1024;
	private static final long LENGTH_GBYTES_CEIL = 1024 * 1024 * 1024 * 1024;
	private static final String LENGTH_UNIG_B = " B";
	private static final String LENGTH_UNIT_KB = " KB";
	private static final String LENGTH_UNIT_MB = " MB";
	private static final String LENGTH_UNIT_GB = " GB";
	private NumberFormat mNumberFormat;
	private DateFormat mDateFormat;
	private DateFormat mTimeFormat;
	private Context mContext;
	private List<File> mObjects = new ArrayList<File>();

	public PathAdapter(Context context) {
		mContext = context;
		mNumberFormat = NumberFormat.getInstance();
		mDateFormat = android.text.format.DateFormat.getDateFormat(mContext);
		mTimeFormat = android.text.format.DateFormat.getTimeFormat(mContext);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (null == convertView) {
			convertView = View.inflate(mContext, R.layout.fc_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.fc_item_name);
			viewHolder.details1 = (TextView) convertView.findViewById(R.id.fc_details1);
			viewHolder.details2 = (TextView) convertView.findViewById(R.id.fc_details2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		File file = mObjects.get(position);

		viewHolder.name.setText(file.getName());

		if (file.isFile()) {
			long length = file.length();
			StringBuilder details1 = new StringBuilder(mContext.getString(R.string.fc_file));

			BigDecimal div;
			String unit;

			if (length < LENGTH_BYTES_CEIL) {
				div = new BigDecimal(1);
				unit = LENGTH_UNIG_B;

			} else if (length < LENGTH_KBYTES_CEIL) {
				div = new BigDecimal(LENGTH_KBYTES_CEIL);
				unit = LENGTH_UNIT_KB;

			} else if (length < LENGTH_MBYTES_CEIL) {
				div = new BigDecimal(LENGTH_MBYTES_CEIL);
				unit = LENGTH_UNIT_MB;

			} else {
				div = new BigDecimal(LENGTH_GBYTES_CEIL);
				unit = LENGTH_UNIT_GB;

			}

			BigDecimal dividedLength = new BigDecimal(file.length()).divide(div, 0, BigDecimal.ROUND_CEILING);

			details1.append(mNumberFormat.format(dividedLength.longValue()));
			details1.append(unit);

			viewHolder.details1.setText(details1.toString());

		} else {
			viewHolder.details1.setText(R.string.fc_directory);
		}

		// last modification date
		Date modifiedDate = new Date(file.lastModified());
		StringBuilder details2 = new StringBuilder().append(mDateFormat.format(modifiedDate)).append(" ")
				.append(mTimeFormat.format(modifiedDate));

		viewHolder.details2.setText(details2.toString());

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(mContext, "fjdlksa", Toast.LENGTH_SHORT).show();

			}
		});

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

	private static class ViewHolder {
		public TextView name;
		public TextView details1;
		public TextView details2;
	}
}
