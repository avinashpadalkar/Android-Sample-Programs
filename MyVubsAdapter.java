package com.genora.vubit.adapters;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.genora.swipelistview.SwipeListView;
import com.genora.vubit.R;
import com.genora.vubit.VideoPlayerActivity;
import com.genora.vubit.Utils.MyUtils;
import com.genora.vubit.database.MYDBHelper;
import com.genora.vubit.models.MyVubsModel;
import com.squareup.picasso.Picasso;

public class MyVubsAdapter extends BaseAdapter implements OnClickListener {

	Context context;
	List<MyVubsModel> myVubsList;

	private LayoutInflater inflater;
	Dialog myDialog;
	MYDBHelper db;
	int myPos;

	SwipeListView swipelistview;

	public MyVubsAdapter(Activity activity, List<MyVubsModel> myVubsList,
			SwipeListView swipelistview) {

		this.context = activity;
		db = new MYDBHelper(context);
		this.myVubsList = myVubsList;
		this.swipelistview = swipelistview;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myVubsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return myVubsList.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder {
		TextView tvName, tvDuration;
		ImageView ivIcon;
		Button btnRemove, btnShare;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		myPos = position;
		View row = convertView;
		Holder holder = new Holder();
		if (row == null) {
			row = inflater.inflate(R.layout.custom_row_myvubs, parent, false);
		} else {
			row = convertView;
		}

		holder.tvName = (TextView) row.findViewById(R.id.title);
		holder.tvDuration = (TextView) row.findViewById(R.id.duration);

		holder.ivIcon = (ImageView) row.findViewById(R.id.childImageView);
		holder.btnRemove = (Button) row.findViewById(R.id.btn_remove_vubs);
		holder.btnShare = (Button) row.findViewById(R.id.btn_share);

		holder.tvName.setText(myVubsList.get(position).getV_name());
		holder.tvDuration.setText("Dubbed On "
				+ myVubsList.get(position).getDate());
		// holder.ivIcon.setBackgroundResource(f_icons[position]);

		Picasso.with(context)
				.load(MyUtils.video_thumb_link
						+ myVubsList.get(position).getV_thumbs())
				.into(holder.ivIcon);

		holder.ivIcon.setOnClickListener(this);
		holder.btnShare.setOnClickListener(this);

		holder.tvName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startDubbedActivity(position);
			}
		});

		holder.tvDuration.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startDubbedActivity(position);
			}
		});

		// holder.tvName.setOnClickListener(this);
		// holder.tvDuration.setOnClickListener(this);
		holder.btnRemove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				int pos = myVubsList.get(position).getId();
				myVubsList.remove(position);

				db.removeMyVubs(pos);

				swipelistview.dismiss(position);
				swipelistview.closeOpenedItems();
			}
		});

		return row;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.childImageView:

			shareVubs();
			break;

		case R.id.btn_share:

			shareVubs();
			break;
		case R.id.btn_remove_vubs:

			break;

		case R.id.share_fb:

			Intent fbsharingIntent = new Intent(Intent.ACTION_SEND);
			Uri fbUri = Uri.parse("android.resource://"
					+ context.getPackageName() + "/" + R.raw.nature);

			fbsharingIntent.setType("video/*");
			fbsharingIntent.putExtra(Intent.EXTRA_STREAM, fbUri);
			context.startActivity(Intent.createChooser(fbsharingIntent,
					"Share image using"));

			break;

		case R.id.share_whatsapp:

			Intent whatsappsharingIntent = new Intent(Intent.ACTION_SEND);

			String video_path = myVubsList.get(myPos).getFile_path();
			// Uri whatsappVideoUri = Uri.parse("android.resource://"
			// + context.getPackageName() + "/" + R.raw.nature);

			Uri whatsappVideoUri = Uri.parse(video_path);

			whatsappsharingIntent.setType("video/*");
			whatsappsharingIntent.putExtra(Intent.EXTRA_STREAM,
					whatsappVideoUri);
			context.startActivity(Intent.createChooser(whatsappsharingIntent,
					"Share image using"));

			break;

		default:
			break;
		}
	}

	private void shareVubs() {
		myDialog = new Dialog(context);
		myDialog.setCancelable(false);
		myDialog.setContentView(R.layout.sahre_video_dialogue);
		myDialog.setTitle("Share Video");
		Button btnCancel = (Button) myDialog.findViewById(R.id.btnCancel);
		TextView share_fb = (TextView) myDialog.findViewById(R.id.share_fb);
		TextView share_whatsapp = (TextView) myDialog
				.findViewById(R.id.share_whatsapp);

		share_fb.setOnClickListener(this);
		share_whatsapp.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

	private void startDubbedActivity(int position) {

		myPos = position;
		int v_id = Integer.parseInt(myVubsList.get(myPos).getV_id());
		String c_name = myVubsList.get(myPos).getC_name();
		String v_title = myVubsList.get(myPos).getV_name();
		String file_path = myVubsList.get(myPos).getFile_path();
		String desc = myVubsList.get(myPos).getDesc();
		String v_thumbs = myVubsList.get(myPos).getV_thumbs();

		Intent intent = new Intent(context, VideoPlayerActivity.class);
		Bundle bundle = new Bundle();

		bundle.putInt("v_id", v_id);
		bundle.putString("c_name", c_name);
		bundle.putString("v_title", v_title);
		bundle.putString("file_path", file_path);
		bundle.putString("desc", desc);
		bundle.putString("v_thumbs", v_thumbs);
		bundle.putInt("flag", 2);

		intent.putExtras(bundle);

		context.startActivity(intent);
	}

}
