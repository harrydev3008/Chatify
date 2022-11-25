package com.hisu.zola;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.badge.BadgeDrawable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.database.Database;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ContactUserRepository;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.ActivityMainBinding;
import com.hisu.zola.fragments.SplashScreenFragment;
import com.hisu.zola.fragments.StartScreenFragment;
import com.hisu.zola.fragments.contact.ContactsFragment;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.fragments.profile.ProfileFragment;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.socket.ConversationSocketHandler;
import com.hisu.zola.util.socket.MessageSocketHandler;
import com.hisu.zola.util.socket.SocketIOHandler;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    private long backPressTime;
    private static final int PRESS_TIME_INTERVAL = 2 * 1000; //2 secs
    private Toast mExitToast;
    private UserRepository userRepository;
    private ContactUserRepository contactUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        if (LocalDataManager.getUserLoginState()) {
            Socket mSocketIO = SocketIOHandler.getInstance().getSocketConnection();
            mSocketIO.on(Socket.EVENT_DISCONNECT, args -> SocketIOHandler.getInstance().establishSocketConnection());
            mSocketIO.on(Constraints.EVT_ADD_FRIEND_RECEIVE, onFriendRequestReceive);
            mSocketIO.on(Constraints.EVT_ACCEPT_FRIEND_REQUEST_RECEIVE, onAcceptFriendRequestReceive);
            mSocketIO.on(Constraints.EVT_UNSENT_FRIEND_REQUEST_RECEIVE, onRequestReceive);
            mSocketIO.on(Constraints.EVT_DELETE_FRIEND_RECEIVE, onRequestReceive);

            mSocketIO.on(Constraints.EVT_ADD_MEMBER_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onAddMemberToGroupReceive);
            mSocketIO.on(Constraints.EVT_REMOVE_MEMBER_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onReceiveRemoveMember);
            mSocketIO.on(Constraints.EVT_CHANGE_GROUP_NAME_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onReceiveChangeGroupName);
            mSocketIO.on(Constraints.EVT_OUT_GROUP_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onReceiveOutGroup);
            mSocketIO.on(Constraints.EVT_CREATE_GROUP_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onReceiveNewGroup);
            mSocketIO.on(Constraints.EVT_CHANGE_GROUP_ADMIN_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onReceiveNewAdmin);
            mSocketIO.on(Constraints.EVT_DELETE_GROUP_RECEIVE, ConversationSocketHandler.getINSTANCE(this.getApplication()).onDisbandGroup);

            mSocketIO.on(Constraints.EVT_MESSAGE_RECEIVE, MessageSocketHandler.getINSTANCE(this.getApplication()).onMessageReceive);
            mSocketIO.on(Constraints.EVT_DELETE_MESSAGE_RECEIVE, MessageSocketHandler.getINSTANCE(this.getApplication()).onMessageDeleteReceive);
        }

        init();
        initProgressBar();

        addSelectedActionForNavItem();
        messageBadge();

        setFragment(new SplashScreenFragment());
    }

    private void init() {
        userRepository = new UserRepository(this.getApplication());
        contactUserRepository = new ContactUserRepository(this.getApplication());
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public int getViewContainerID() {
        return mainBinding.viewContainer.getId();
    }

    private void clearFragmentList() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void clearDB() {
        Database database = Database.getDatabase(this);
        Database.dbExecutor.execute(() -> {
            database.conversationDAO().dropConversationTable();
            database.messageDAO().dropMessageTable();
            database.userDAO().dropUserTable();
            database.contactUserDAO().dropTableContactUser();
        });
    }

    public void logOut() {
        if (LocalDataManager.getUserLoginState()) {
            LocalDataManager.setUserLoginState(false);

            clearDB();

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
        SocketIOHandler.disconnect();
        SocketIOHandler.close();
    }

    private void updateUserInfo() {
        JsonObject object = new JsonObject();
        object.addProperty("phoneNumber", LocalDataManager.getCurrentUserInfo().getPhoneNumber());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.findFriendByPhoneNumber(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User user = response.body();
                    LocalDataManager.setCurrentUserInfo(user);
                    userRepository.update(user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(MainActivity.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private final Emitter.Listener onFriendRequestReceive = args -> {
        JSONObject data = (JSONObject) args[0];
        if (data != null) {
            Gson gson = new Gson();
            User sender = gson.fromJson(data.toString(), User.class);
            String placeHolder = sender.getUsername() + " " + getString(R.string.friend_request_noty_message);
            NotificationUtil.sendNotification(MainActivity.this, placeHolder);
            updateUserInfo();
        }
    };

    private final Emitter.Listener onAcceptFriendRequestReceive = args -> {
        JSONObject data = (JSONObject) args[0];
        if (data != null) {
            try {
                Gson gson = new Gson();
                User sender = gson.fromJson(data.get("sender").toString(), User.class);
                String placeHolder = sender.getUsername() + " " + getString(R.string.accept_friend_request_noty_message);
                NotificationUtil.sendNotification(MainActivity.this, placeHolder);
                contactUserRepository.updateFriend(sender.getId(), true);
                updateUserInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Emitter.Listener onRequestReceive = args -> {
        JSONObject data = (JSONObject) args[0];
        if (data != null) {
            updateUserInfo();
        }
    };
}