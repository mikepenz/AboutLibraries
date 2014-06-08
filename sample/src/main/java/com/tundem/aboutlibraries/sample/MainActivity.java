package com.tundem.aboutlibraries.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.sample.cardsui.LibraryCard;
import com.tundem.aboutlibraries.ui.LibsActivity;

import java.util.List;


public class MainActivity extends Activity {

    private CardUI mCardView;
    private Libs libs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        libs = Libs.getInstance(this, Libs.toStringArray(R.string.class.getFields()));

        // init CardView
        mCardView = (CardUI) findViewById(R.id.cardsview);
        mCardView.setSwipeable(false);

        setDefined();
    }

    private void setAll() {
        mCardView.clearCards();

        mCardView.addStack(new CardStack(" "));

        List<Library> libraries = libs.getLibraries();
        for (Library lib : libraries) {
            mCardView.addCard(new LibraryCard(this, lib));
        }
        mCardView.addStack(new CardStack(" "));

        mCardView.refresh();
    }

    private void setExternal() {
        mCardView.clearCards();

        mCardView.addStack(new CardStack(" "));

        List<Library> libraries = libs.getExternLibraries();
        for (Library lib : libraries) {
            mCardView.addCard(new LibraryCard(this, lib));
        }
        mCardView.addStack(new CardStack(" "));

        mCardView.refresh();
    }

    private void setInternal() {
        mCardView.clearCards();

        mCardView.addStack(new CardStack(" "));

        List<Library> libraries = libs.getInternLibraries();
        for (Library lib : libraries) {
            mCardView.addCard(new LibraryCard(this, lib));
        }
        mCardView.addStack(new CardStack(" "));

        mCardView.refresh();
    }

    private void setDefined() {
        mCardView.clearCards();

        mCardView.addStack(new CardStack(" "));
        mCardView.addCard(new LibraryCard(this, "AndroidIconify"));
        mCardView.addCard(new LibraryCard(this, "ActiveAndroid"));
        mCardView.addCard(new LibraryCard(this, "CardsUI"));
        mCardView.addCard(new LibraryCard(this, "FButton"));
        mCardView.addCard(new LibraryCard(this, "Crouton"));
        mCardView.addCard(new LibraryCard(this, "HoloGraphLibrary"));
        mCardView.addCard(new LibraryCard(this, "ShowcaseView"));
        mCardView.addCard(new LibraryCard(this, "NineOldAndroids"));
        mCardView.addCard(new LibraryCard(this, "AndroidViewpagerIndicator"));
        mCardView.addStack(new CardStack(" "));

        mCardView.refresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_all) {
            setAll();
            return true;
        } else if (id == R.id.action_external) {
            setExternal();
            return true;
        } else if (id == R.id.action_internal) {
            setInternal();
            return true;
        } else if (id == R.id.action_defined) {
            setDefined();
            return true;
        } else if (id == R.id.action_opensource) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"));
            startActivity(browserIntent);
            return true;
        } else if (id == R.id.action_extendactivity) {
            Intent i = new Intent(getApplicationContext(), ExtendActivity.class);
            startActivity(i);
        } else if (id == R.id.action_fragmentactivity) {
            Intent i = new Intent(getApplicationContext(), FragmentActivity.class);
            startActivity(i);
        } else if (id == R.id.action_manifestactivity) {
            Intent i = new Intent(getApplicationContext(), LibsActivity.class);
            i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
            i.putExtra(Libs.BUNDLE_LIBS, new String[]{"crouton", "actionbarsherlock", "showcaseview"});

            i.putExtra(Libs.BUNDLE_VERSION, true);
            i.putExtra(Libs.BUNDLE_LICENSE, true);

            i.putExtra(Libs.BUNDLE_TITLE, "Open Source");
            i.putExtra(Libs.BUNDLE_THEME, android.R.style.Theme_Holo);
            i.putExtra(Libs.BUNDLE_ACCENTCOLOR, "#3396E5");
            i.putExtra(Libs.BUNDLE_TRANSLUCENTDECOR, true);

            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
