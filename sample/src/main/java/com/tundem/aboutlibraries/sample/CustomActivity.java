package com.tundem.aboutlibraries.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.sample.cardsui.LibraryCard;

import java.util.List;


public class CustomActivity extends ActionBarActivity {

    private CardUI mCardView;
    private Libs libs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        libs = new Libs(this, Libs.toStringArray(R.string.class.getFields()));

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
        mCardView.addCard(new LibraryCard(this, libs, "AndroidIconify"));
        mCardView.addCard(new LibraryCard(this, libs, "ActiveAndroid"));
        mCardView.addCard(new LibraryCard(this, libs, "CardsUI"));
        mCardView.addCard(new LibraryCard(this, libs, "FButton"));
        mCardView.addCard(new LibraryCard(this, libs, "Crouton"));
        mCardView.addCard(new LibraryCard(this, libs, "HoloGraphLibrary"));
        mCardView.addCard(new LibraryCard(this, libs, "ShowcaseView"));
        mCardView.addCard(new LibraryCard(this, libs, "NineOldAndroids"));
        mCardView.addCard(new LibraryCard(this, libs, "AndroidViewpagerIndicator"));
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
        }
        return super.onOptionsItemSelected(item);
    }
}
