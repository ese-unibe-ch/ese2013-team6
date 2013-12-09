package com.ese2013.mub;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.ViewUtil;
import com.ese2013.mub.util.parseDatabase.MensaDBHandler;
/**
 * Is the View for a single Menu
 * it contains a colored header, a description and a RatingBar, 
 *
 */
public class MenuView extends LinearLayout {
	private Menu menu;
	private Day day;

	public MenuView(Context context, Menu menu, Day day) {
		super(context);
		this.menu = menu;
		this.day = day;
		setOrientation(VERTICAL);
		setPadding(0, 0, 0, dimToPixels(R.dimen.menu_view_bottom_margin));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.menu_view, this);

		MenuManager menuManager = Model.getInstance().getMenuManager();
		String menuTitle, menuDesc;
		if (menuManager.isTranslationEnabled() && menuManager.translationsAvailable()) {
			menuTitle = menu.getTranslatedTitle();
			menuDesc = menu.getTranslatedDescription();
		} else {
			menuTitle = menu.getTitle();
			menuDesc = menu.getDescription();
		}

		menuTitle = menuTitle.toUpperCase(Locale.getDefault());
		setTitle(menuTitle, getTitleColor(menu.getTitle()));
		setDescription(menuDesc);

		setCountDisplay();

		initRatingBar();
	}

	private void initRatingBar() {
		RatingBar ratingBar = (RatingBar) this.findViewById(R.id.menu_rating_bar);
		setCountDisplay();
		ratingBar.setRating(menu.getAverageRating());
		ratingBar.setIsIndicator(true);
		RatingBarListener listener = new RatingBarListener();
		ratingBar.setOnRatingBarChangeListener(listener);
		ratingBar.setOnTouchListener(listener);
		ratingBar.setId(ViewUtil.generateViewId());
	}

	private boolean menusIsFromFutureDate() {
		return day.compareTo(Day.today()) > 0;
	}

	private void setCountDisplay() {
		((TextView) findViewById(R.id.menu_rating_count)).setText("" + menu.getRatingCount());
	}

	public MenuView(Context context) {
		super(context);
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void setTitle(String menuTitle, int color) {
		TextView menuTitleText = (TextView) getChildAt(0);
		menuTitleText.setText(menuTitle);
		menuTitleText.setBackgroundColor(color);
	}

	private void setDescription(String menuDesc) {
		TextView menuDescView = (TextView) this.findViewById(R.id.menu_description_text);
		menuDescView.setText(menuDesc);
	}

	private int getTitleColor(String title) {
		title = title.toUpperCase(Locale.GERMAN);
		if (title.contains("VEGI") || title.contains("VEGETARISCH"))
			return getResources().getColor(R.color.green);

		if (title.contains("EINFACH GUT") || title.contains("TAGESGERICHT") || title.contains("WARMES SCH�SSELGERICHT"))
			return getResources().getColor(R.color.yellow);

		return getResources().getColor(R.color.blue);
	}

	private int dimToPixels(int dim) {
		return (int) getResources().getDimension(dim);
	}

	private class RatingBarListener implements RatingBar.OnRatingBarChangeListener, OnTouchListener {
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
			if (fromUser) {
				Menu menu = MenuView.this.menu;
				CurrentUser user = LoginService.getLoggedInUser();
				int userRating = (int) rating;
				menu.setUserRating(userRating);
				user.addToRated(menu);
				new MensaDBHandler().saveMenuRating(user, menu, userRating);
				ratingBar.setIsIndicator(true);
				setCountDisplay();
				ratingBar.setRating(menu.getAverageRating());
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Context context = MenuView.this.getContext();
			if (LoginService.isLoggedIn()) {
				RatingBar ratingBar = (RatingBar) v;
				if (LoginService.getLoggedInUser().hasBeenRated(MenuView.this.menu)) {
					Toast.makeText(context, R.string.rating_msg_already_rated, Toast.LENGTH_SHORT).show();
				} else if (menusIsFromFutureDate()) {
					Toast.makeText(context, R.string.rating_msg_from_future, Toast.LENGTH_SHORT).show();
				} else {
					ratingBar.setIsIndicator(false);
				}
			} else {
				Toast.makeText(context, R.string.rating_msg_not_logged_in, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	}

}
