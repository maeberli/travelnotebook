package ch.hearc.devmobile.travelnotebook;

import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import ch.hearc.devmobile.travelnotebook.database.Image;

public class ImageAdapter extends BaseAdapter {

	/********************
	 * Private Static constants
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = ImageAdapter.class.getSimpleName();

	private Context context;
	private Collection<Image> images;

	private LayoutInflater inflater;

	public ImageAdapter(Context context, Collection<Image> images) {
		this.context = context;
		this.images = images;

		this.inflater = LayoutInflater.from(this.context);

	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View view, ViewGroup viewGroup) {
		return getImageView(position, view, viewGroup);
	}

	private View getImageView(int position, View view, ViewGroup viewGroup) {
		View newVew = view;
		ImageView picture;
		ImageButton deleteBtn;

		if (newVew == null) {
			newVew = inflater.inflate(R.layout.grid_imageitem, viewGroup, false);
			newVew.setTag(R.id.image_view, newVew.findViewById(R.id.image_view));
			newVew.setTag(R.id.image_delete_btn, newVew.findViewById(R.id.image_delete_btn));
		}

		picture = (ImageView) newVew.getTag(R.id.image_view);
		deleteBtn = (ImageButton) newVew.getTag(R.id.image_delete_btn);

		final Image image = getItem(position);
		Bitmap bitmap = loadImage(image.getImageURI(), newVew);

		picture.setImageBitmap(bitmap);
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				images.remove(image);
				ImageAdapter.this.notifyDataSetChanged();
			}
		});

		return newVew;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Image getItem(int position) {
		return (Image) images.toArray()[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return IGNORE_ITEM_VIEW_TYPE;
		}
		return super.getItemViewType(position);
	}

	private Bitmap loadImage(String path, View view) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, view.getResources().getDisplayMetrics());

		int targetW = px;
		int targetH = px;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		return bitmap;
	}
}
