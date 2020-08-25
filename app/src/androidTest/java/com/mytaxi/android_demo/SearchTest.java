package com.mytaxi.android_demo;

import android.os.SystemClock;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import com.mytaxi.android_demo.activities.MainActivity;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchTest {

    private String username = "crazydog335";
    private String password = "venture";
    String search_text = "sa";
    private String search_Driver = "Sarah Scott";

    @Rule
    public ActivityTestRule<MainActivity> mainRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        //Before Test case execution - Login to App if not logged in else ignore
        try
        {
            onView(withId(R.id.edt_username)).perform(typeText(username), closeSoftKeyboard());
            onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void searchDriver() {
        //Wait added to address search not auto populated issue
        SystemClock.sleep(5000);

        //Search for "sa", select the 2nd result (via the name, not the index) from the list
        //onView(isRoot()).perform(waitId(R.id.textSearch, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.textSearch)).check(matches((isDisplayed())));
        onView(withId(R.id.textSearch)).perform(clearText());
        onView(withId(R.id.textSearch)).perform(typeText("sa"), closeSoftKeyboard());
        SystemClock.sleep(5000);

        // Check that suggestions are displayed.
        onView(withText(search_Driver))
                .inRoot(withDecorView(not(is(mainRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Tap on a suggestion via the name, not the index
        onView(withText(search_Driver))
                .inRoot(withDecorView(not(is(mainRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        //Check whether correct driver profile is displayed
        onView(isRoot()).perform(waitId(R.id.textViewDriverName, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.textViewDriverName)).check(matches(withText(search_Driver)));

        //Tap the call button
        onView(withId(R.id.fab)).perform(click());
    }

    //wait Util function
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw (new PerformException.Builder())
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    @After
    public void tearDown() {
        //After Test case Execution
    }
}