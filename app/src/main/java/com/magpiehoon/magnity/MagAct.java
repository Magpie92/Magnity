package com.magpiehoon.magnity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.magpiehoon.magnity.auth.AuthMagFrag;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagAct extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drw_root_mag)
    DrawerLayout mDrawerbleRoot;

    @BindView(R.id.nav_toolbar_mag)
    Toolbar mNavToolbar;

    @BindView(R.id.nav_main_view)
    NavigationView mNavMainView;

    @BindView(R.id.frm_content_mag)
    FrameLayout mFrmContentMag;


    @Override
    protected void onResume() {
        super.onResume();
        authProfileCheck();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag);
        ButterKnife.bind(this);

        setSupportActionBar(mNavToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerbleRoot, mNavToolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerbleRoot.addDrawerListener(toggle);
        toggle.syncState();

        mNavMainView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerbleRoot.isDrawerOpen(GravityCompat.START)) {
            mDrawerbleRoot.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            // Handle the camera action
//            startActivity(AuthMagUi.createIntent(this));
            fragment = AuthMagFrag.newInstance();
            fm.beginTransaction().add(R.id.frm_content_mag, fragment).commit();
        } else if (id == R.id.nav_timeline) {
            Toast.makeText(this, "timeline", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "not yet", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_img_ic) {
            Toast.makeText(this, "click ic", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_header) {
            Toast.makeText(this, "click header", Toast.LENGTH_SHORT).show();
        }

        mDrawerbleRoot.closeDrawer(GravityCompat.START);
        return true;
    }

    private void authProfileCheck() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            View headerView = mNavMainView.getHeaderView(0);
            TextView textView = (TextView) headerView.findViewById(R.id.nav_profile_email);
            textView.setText(auth.getCurrentUser().getEmail());
        }
    }
}
