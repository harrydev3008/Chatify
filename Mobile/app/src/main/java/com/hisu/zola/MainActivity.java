package com.hisu.zola;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hisu.zola.database.Database;
import com.hisu.zola.databinding.ActivityMainBinding;
import com.hisu.zola.entity.ConversationHolder;
import com.hisu.zola.fragments.LoginFragment;
import com.hisu.zola.fragments.SplashScreenFragment;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.Random;

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
//        Database db = Database.getDatabase(this);
//        Database.dbExecutor.execute(() -> {
//            db.conversationDAO().insert(new ConversationHolder("3", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("4", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("5", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("6", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("7", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("8", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("9", false, R.drawable.app_logo, "Harry","new msg", 1));
//            db.conversationDAO().insert(new ConversationHolder("10", false, R.drawable.app_logo, "Harry","new msg", 1));
//        });
        initSocket();

        setFragment(new SplashScreenFragment());
    }

    private void initSocket() {
        SocketIOHandler.getInstance();
        SocketIOHandler.establishSocketConnection();
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

    private void clearFragmentListBeforeLogOut() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry topFragment = manager.getBackStackEntryAt(0);
            manager.popBackStack(topFragment.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void logOut() {
        if (LocalDataManager.getUserLoginState()) {
            LocalDataManager.setUserLoginState(false);
            clearFragmentListBeforeLogOut();
            setFragment(new LoginFragment());
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
        SocketIOHandler.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIOHandler.establishSocketConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketIOHandler.disconnect();
        SocketIOHandler.establishSocketConnection();
    }
}