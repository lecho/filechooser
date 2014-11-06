package lecho.lib.filechooser;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import lecho.lib.filechooser.FileSizeUtils.FileSizeDivider;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class FileDetailsDialogFragment extends DialogFragment {
	private static final String BUNDLE_PATH = "lecho.lib.filechooser:bundle-path";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dialog_details, container, false);

		String path = getArguments().getString(BUNDLE_PATH);
		File file = new File(path);

		TextView typeView = (TextView) rootView.findViewById(R.id.fc_type);
		if (file.isFile()) {
			typeView.setText(R.string.fc_type_file);
		} else {
			typeView.setText(R.string.fc_type_directory);
		}

		TextView nameView = (TextView) rootView.findViewById(R.id.fc_name);
		nameView.setText(file.getName());

		TextView fullPathView = (TextView) rootView.findViewById(R.id.fc_full_path);
		fullPathView.setText(file.getAbsolutePath());

		TextView modifiedView = (TextView) rootView.findViewById(R.id.fc_modified);
		DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getActivity());
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
		Date lastModifiedDate = new Date(file.lastModified());
		StringBuilder modifiedText = new StringBuilder(dateFormat.format(lastModifiedDate)).append(" ").append(
				timeFormat.format(lastModifiedDate));
		modifiedView.setText(modifiedText.toString());

		if (file.isFile()) {
			TextView sizeView = (TextView) rootView.findViewById(R.id.fc_size);

			long length = file.length();

			FileSizeDivider divider = FileSizeUtils.getFileSizeDivider(length);
			BigDecimal dividedLength = new BigDecimal(length).divide(divider.div, 2, BigDecimal.ROUND_CEILING);
			NumberFormat numberFormat = NumberFormat.getInstance();
			StringBuilder sizeText = new StringBuilder(numberFormat.format(dividedLength.doubleValue()))
					.append(divider.unitText);

			sizeView.setText(sizeText.toString());
		}

		Button okButton = (Button) rootView.findViewById(R.id.fc_button_ok);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return rootView;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public static void showDialog(FragmentActivity activity, String path) {

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		DialogFragment fragment = new FileDetailsDialogFragment();

		Bundle args = new Bundle();
		args.putString(BUNDLE_PATH, path);

		fragment.setArguments(args);

		fragment.show(fragmentManager, "fc-file-details-dialog");

	}
}
