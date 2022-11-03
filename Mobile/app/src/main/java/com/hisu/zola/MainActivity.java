package com.hisu.zola;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.badge.BadgeDrawable;
import com.hisu.zola.databinding.ActivityMainBinding;
import com.hisu.zola.fragments.SplashScreenFragment;
import com.hisu.zola.fragments.StartScreenFragment;
import com.hisu.zola.fragments.contact.ContactsFragment;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.fragments.profile.ProfileFragment;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    private long backPressTime;
    private static final int PRESS_TIME_INTERVAL = 2 * 1000; //2 secs
    private Toast mExitToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        initProgressBar();

        addSelectedActionForNavItem();
        messageBadge();

        setFragment(new SplashScreenFragment());
    }

    private void initProgressBar() {
        Sprite cubes = new WanderingCubes();
        cubes.setColor(ContextCompat.getColor(this, R.color.primary_color));
        mainBinding.progressBarLoading.setIndeterminateDrawable(cubes);
    }

    public void setProgressbarVisibility(int visibility) {
        mainBinding.progressBarLoading.setVisibility(visibility);
    }

    public void setBottomNavVisibility(int hide) {
        mainBinding.navigationMenu.setVisibility(hide);
    }

    private void messageBadge() {
        BadgeDrawable badge = mainBinding.navigationMenu.getOrCreateBadge(R.id.action_message);
        badge.setNumber(5);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.chat_badge_bg));
        badge.setVerticalOffset(10);
        badge.setHorizontalOffset(5);
    }

    private void addSelectedActionForNavItem() {
        mainBinding.navigationMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            setProgressbarVisibility(View.VISIBLE);

            if (itemId == R.id.action_message)
                addFragmentToBackStack(new ConversationListFragment());
            else if (itemId == R.id.action_contact)
                addFragmentToBackStack(new ContactsFragment());
            else if (itemId == R.id.action_profile)
                addFragmentToBackStack(new ProfileFragment());

            if (mainBinding.navigationMenu.getVisibility() != View.VISIBLE)
                setBottomNavVisibility(View.VISIBLE);

            return true;
        });
    }

    public void addFragmentToBackStack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getViewContainerID(), fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getViewContainerID(), fragment)
                .commit();
    }

    public int getViewContainerID() {
        return mainBinding.viewContainer.getId();
    }

    private void clearFragmentList() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void logOut() {
        if (LocalDataManager.getUserLoginState()) {
            LocalDataManager.setUserLoginState(false);
            mainBinding.navigationMenu.setSelectedItemId(R.id.action_message);
            setBottomNavVisibility(View.GONE);
            clearFragmentList();
            setFragment(new StartScreenFragment());
        }
    }

    @Override
    public void onBackPressed() {
        /*
          Press back button twice within 2s to exit program
          if not => stay :D
         */
        if (backPressTime + PRESS_TIME_INTERVAL > System.currentTimeMillis()) {
            mExitToast.cancel();
            moveTaskToBack(true); //Only move to back not close the whole app
            return;
        } else {
            mExitToast = Toast.makeText(MainActivity.this,
                    getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT
            );
            mExitToast.show();
        }

        backPressTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearFragmentList();
        SocketIOHandler.disconnect();
        SocketIOHandler.close();
    }
}