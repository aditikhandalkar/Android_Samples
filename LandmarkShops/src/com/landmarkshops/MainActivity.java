package com.landmarkshops;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	static String[] ImageURLs = new String[12];
	String[] ProductName = new String[12];
	String[] ProductPrice = new String[12];
	String[] ProductPriceNew = new String[12];
	String[] ProductRating = new String[12];
	ListView list;

	ViewPager viewPager;
	int[] flag;
	LinearLayout llDots;
	ImagePagerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseHTML();
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		adapter = new ImagePagerAdapter(MainActivity.this);
		viewPager.setAdapter(adapter);

		llDots = (LinearLayout) findViewById(R.id.llDots);

		for (int i = 0; i < adapter.getCount(); i++) {
			ImageButton imgDot = new ImageButton(this);
			imgDot.setTag(i);
			imgDot.setImageResource(R.drawable.dot_selector);
			imgDot.setBackgroundResource(0);
			imgDot.setPadding(5, 5, 5, 5);
			LayoutParams params = new LayoutParams(20, 20);
			imgDot.setLayoutParams(params);
			if (i == 0)
				imgDot.setSelected(true);

			llDots.addView(imgDot);
		}

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				Log.e("", "Page Selected is ===> " + pos);
				for (int i = 0; i < adapter.getCount(); i++) {
					if (i != pos) {
						((ImageView) llDots.findViewWithTag(i))
								.setSelected(false);
					}
				}
				((ImageView) llDots.findViewWithTag(pos)).setSelected(true);
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	public void ParseHTML() {
		// initialize image view
		InputStream is = null;
		Document doc = null;
		try {
			is = getAssets().open("asset.html");
			doc = Jsoup.parse(is, "UTF-8", "http://www.landmarkshops.com/");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		String href = null, absUrl = null;
		// doc = Jsoup.parse(is, "UTF-8", "http://www.landmarkshops.com/");
		Element div = doc.select("div#HomePageStaffPicksProductCarousel")
				.first();
		Elements classes = div.select("div.image");
		// Log.e("Doc----------------",
		// "----------------------------------"+classes.toString());
		int k = 0;
		for (Element link : classes) {
			href = link.select("a").first().attr("href");
			absUrl = link.select("img").first().absUrl("src");
			// Log.e("Doc----------------", "----------" + href + "----------"+
			// absUrl);
			ImageURLs[k] = absUrl;
			k++;
		}

		Elements details = div.select("div.item-desc-extra");
		// Log.e("Details",
		// "----------------------------------"+details.html());
		k = 0;
		for (Element link : details) {
			href = link.select("a").first().select("p").first().text();
			absUrl = link.select("div.left").select("span[class=old-price]")
					.text();
			String newprice = link.select("div.left").select("span[class=new-price]")
					.text();
			String rating = link.select("span[class=rating-stars]").select("span[class=rating-overlay]").text();
			// Log.e("Doc----------------", "----------" + href + "----------"+
			// absUrl);
			ProductName[k] = href;
			ProductPrice[k] = absUrl;
			ProductPriceNew[k] = newprice;
			ProductRating[k] = rating;
			Log.e("Doc----------------", ProductName[k] + "-----------------"
					+ ProductPrice[k]);
			k++;
		}
	}

	class ImagePagerAdapter extends PagerAdapter {
		// Declare Variables
		Context context;

		LayoutInflater inflater;
		ImageLoader imageLoader;

		public ImagePagerAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return ImageURLs.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		public float getPageWidth(int position) {
			return 0.5f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageLoader imageLoader=new ImageLoader(MainActivity.this);
			ImageView imgflag;
			TextView name;
			TextView OldPrice;
			TextView newPrice;
			TextView savings;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.pager_object, container,
					false);

			// Locate the ImageView in viewpager_item.xml
			imgflag = (ImageView) itemView.findViewById(R.id.flag);
			name = (TextView) itemView.findViewById(R.id.Name);
			OldPrice = (TextView) itemView.findViewById(R.id.OldPrice);
			newPrice = (TextView) itemView.findViewById(R.id.NewPrice);
			savings = (TextView) itemView.findViewById(R.id.savings);
			
			name.setText(ProductName[position]);
			OldPrice.setText(ProductPrice[position]);
			OldPrice.setPaintFlags(OldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			newPrice.setText(ProductPriceNew[position]);
			
			RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.Stars);
			ratingBar.setRating(Float.parseFloat(ProductRating[position]+".0f")); 
			
			float oldValue, newValue;
			try {
				oldValue = Float.parseFloat(ProductPrice[position].replaceAll("[\\D]", ""));
			}
			catch(NumberFormatException ex) {
				oldValue = 0.0f; // default ??
			}
			
			try {
				newValue = Float.parseFloat(ProductPriceNew[position].replaceAll("[\\D]", ""));
			}
			catch(NumberFormatException ex) {
				newValue = 0.0f; // default ??
			}
			
			float compare = 0.0f;
			float value = oldValue - newValue;
			
			if (Float.compare(compare, value) > 0){
				savings.setVisibility(View.GONE);
			}else{
				savings.setText("SAVE AED "+value);
			}
			
			imageLoader.DisplayImage(ImageURLs[position], imgflag);
			((ViewPager) container).addView(itemView);

			return itemView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);
		}
	}
}