package lecho.lib.filechooser.samples;

import java.util.ArrayList;

import lecho.lib.filechooser.FilechooserActivity;
import lecho.lib.filechooser.ItemType;
import lecho.lib.filechooser.SelectionMode;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private TextView textview;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			Button button1 = (Button) rootView.findViewById(R.id.button1);
			Button button2 = (Button) rootView.findViewById(R.id.button2);
			Button button3 = (Button) rootView.findViewById(R.id.button3);
			Button button4 = (Button) rootView.findViewById(R.id.button4);
			textview = (TextView) rootView.findViewById(R.id.textview);

			button1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getActivity(), FilechooserActivity.class);
					i.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);
					i.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);
					startActivityForResult(i, 1);

				}
			});
			
			button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getActivity(), FilechooserActivity.class);
					i.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);
					i.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.MULTIPLE_ITEM);
					startActivityForResult(i, 1);

				}
			});
			
			button3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getActivity(), FilechooserActivity.class);
					i.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.DIRECTORY);
					i.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);
					startActivityForResult(i, 1);

				}
			});
			
			button4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getActivity(), FilechooserActivity.class);
					i.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.ALL);
					i.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.MULTIPLE_ITEM);
					startActivityForResult(i, 1);

				}
			});
			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> paths = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);
				StringBuilder sb = new StringBuilder();
				for (String path : paths) {
					sb.append(path).append("<br/>");
				}

				textview.setText(Html.fromHtml(sb.toString()));
			}
		}
	}

}
