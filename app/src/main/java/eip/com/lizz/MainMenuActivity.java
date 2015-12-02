package eip.com.lizz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import eip.com.lizz.Fragments.CartFragment;
import eip.com.lizz.Fragments.HistoryListFragment;
import eip.com.lizz.Fragments.ParametersListFragment;
import eip.com.lizz.Utils.UDownload;


public class MainMenuActivity extends AppCompatActivity {

    private MainMenuPagerAdapter mMainMenuPagerAdapter;
    private ViewPager mViewPager;

    private String tabTitles[] = new String[] { "Paramètres", "Panier", "Historique" };
    private int[] blackIconResId = { R.drawable.ic_settings_black_24dp, R.drawable.ic_shopping_basket_black_24dp,
            R.drawable.ic_history_black_24dp };
    private int[] whiteIconResId = { R.drawable.ic_settings_white_24dp, R.drawable.ic_shopping_basket_white_24dp,
            R.drawable.ic_history_white_24dp };

    boolean isLogged, isLoginJustNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            isLoginJustNow = bundle.getBoolean("isLoginJustNow");

        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();

        if (isLogged)
        {

            if (isLoginJustNow)
            {
                final ProgressDialog progress = new ProgressDialog(MainMenuActivity.this);
                progress.setTitle(getResources().getString(R.string.dialog_download));
                progress.setMessage(getResources().getString(R.string.dialog_download_rsa_key));
                progress.setCancelable(false);
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        UDownload.downloadFile("http://test-ta-key.lizz.fr/lastKey.key", "keyrsa.pub", getBaseContext());
                        progress.dismiss();
                    }
                }).start();
            }

            /*if (getResources().getString(R.string.debugOrProd).equals("PROD"))
                checkSIMNumber();*/

            // Déclaration du PagerAdapter
            mMainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager(),
                    MainMenuActivity.this);

            // Déclaration du ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mViewPager.setAdapter(mMainMenuPagerAdapter);
            mViewPager.setCurrentItem(1);

            // Déclaration du TabLayout
            final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(mViewPager);

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_settings_black_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_shopping_basket_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_history_black_24dp);

            getSupportActionBar().setTitle(tabTitles[1]);

            getSupportActionBar().setElevation(0);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    getSupportActionBar().setTitle(tabTitles[tab.getPosition()]);
                    tab.setIcon(whiteIconResId[tab.getPosition()]);
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.setIcon(blackIconResId[tab.getPosition()]);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_settings_black_24dp);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_shopping_basket_black_24dp);
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_history_black_24dp);
                    tabLayout.getTabAt(position).setIcon(whiteIconResId[position]);
                    getSupportActionBar().setTitle(tabTitles[position]);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        else
        {
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
        }

    }

    /*
        CREATION DE L'ADAPTER
     */
    public class MainMenuPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private Context context;

        public MainMenuPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ParametersListFragment.newInstance(context);
                case 1:
                    return CartFragment.newInstance(context);
                case 2:
                    return HistoryListFragment.newInstance(context);
            }
            return ParametersListFragment.newInstance(context);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), MainMenuActivity.this);
    }

    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();
    }

}
