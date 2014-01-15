package ch.hearc.devmobile.travelnotebook.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import ch.hearc.devmobile.travelnotebook.Utilities;

public class ImagePagerAdapter extends PagerAdapter {

	private Context context;
	private List<String> imagePaths;

	public ImagePagerAdapter(Context context, List<String> imagePaths) {
		super();
		this.context = context;
		this.imagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		final ImageView image = new ImageView(this.context);
		final String path = imagePaths.get(position);

		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, collection.getResources().getDisplayMetrics());
		Bitmap bm = Utilities.loadImage(path, px);
		image.setImageBitmap(bm);

		collection.addView(image, 0);

		return image;
	}

	@Override
	public void destroyItem(ViewGroup collection, int position, Object view) {
		collection.removeView((ImageView) view);
	}

}
