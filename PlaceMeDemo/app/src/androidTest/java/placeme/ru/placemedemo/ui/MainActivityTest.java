package placeme.ru.placemedemo.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import placeme.ru.placemedemo.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * UI test main activity
 * Created by Андрей on 07.02.2018.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void testMenu() throws Exception {
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        final ViewInteraction onDrawer = onView(withId(R.id.drawer_layout));

        onDrawer.check(matches(isClosed()));
        onDrawer.perform(DrawerActions.open());
        onDrawer.check(matches(isOpen()));
        onDrawer.perform(DrawerActions.close());

        onView(withId(R.id.search_friends)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        onView(withId(R.id.button_search_parameters)).check(matches(isDisplayed()));
        onView(withId(R.id.button4)).check(matches(isDisplayed()));

        onView(withId(R.id.search_friends)).perform(click());
        onView(withText("Search friends!")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.route_description)).perform(replaceText("ho"));
        onView(withText("Search!")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        Thread.sleep(4000);

        onView(withText("Add to friends!")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Cancel")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.fab)).perform(click());
        onView(withText("Save Route")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.route_description)).perform(replaceText("test route savage"));
        onView(withText("Back")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        Thread.sleep(4000);

        onView(withId(R.id.button_search_parameters)).perform(click());
        onView(withText("Choose parameters")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dist)).perform(click());
        onView(withId(R.id.switch_dist)).perform(click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        Thread.sleep(4000);

        onView(withId(R.id.search)).check(matches(isDisplayed())).perform(replaceText("еда")).perform(pressImeActionButton());
        Thread.sleep(4000);

        onView(withText("Cancel")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        onDrawer.perform(DrawerActions.open());

        onView(withText("Profile")).check(matches(isDisplayed())).perform(click());

        onView(withText("Edit")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.editProfile)).check(matches(isDisplayed()));

        onView(withId(R.id.teEditNickname)).check(matches(isDisplayed()));
        onView(withId(R.id.teEditName)).check(matches(isDisplayed()));
        onView(withId(R.id.teEditSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.teEditLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.teEditPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.tvEditNickname)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEditName)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEditSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEditLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEditPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.saveEdit)).check(matches(isDisplayed()));

        onView(isRoot()).perform(ViewActions.pressBack());
        onView(isRoot()).perform(ViewActions.pressBack());

        onDrawer.perform(DrawerActions.open());
        onView(withText("My Favourite Places")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(isRoot()).perform(ViewActions.pressBack());

        onDrawer.perform(DrawerActions.open());
        onView(withText("My Routes")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(isRoot()).perform(ViewActions.pressBack());

        onDrawer.perform(DrawerActions.open());
        onView(withText("Plans")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(isRoot()).perform(ViewActions.pressBack());

        onView(withId(R.id.search_friends)).perform(longClick());
        Thread.sleep(200);
        onView(withText("Find friends\nhere!")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.google_places_button)).perform(longClick());
        Thread.sleep(200);
        onView(withText("Import places\nfrom big base")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
    }
}