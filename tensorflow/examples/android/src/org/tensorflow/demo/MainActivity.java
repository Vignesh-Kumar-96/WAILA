package org.tensorflow.demo;

/**
 * Created by vignesh on 5/5/18.
 */
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements FirebaseLoginFragment.FirebaseLoginInterface,
        FirebaseCreateAccountFragment.FirebaseCreateAccountInterface
{

    public static String TAG = "MainActivity";
    protected static String ANON_USER = "Anonymous User";
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected String userName;
    private Button main_button;
    private TextView login_text;
    PhotoManager photoManager;

    protected void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userName = user.getDisplayName();
                    if (userName == null) {
                        userName = ANON_USER; // Anonymous signin returns null
                    }
                    photoManager.updateCurrentUserName(userName);
                    login_text.setText("Logged in as " + userName);
                    main_button.setText("LOG OUT");
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userName = null;
                }

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        main_button = (Button) findViewById(R.id.main_button);
        login_text = (TextView) findViewById(R.id.login_text);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_login();
            }
        });

        photoManager = PhotoManager.getInstance();

        firebaseInit();
        main_button.performClick();
    }

    public void firebaseLoginFinish() {
        getFragmentManager().popBackStack();

        login_text.setText("Logged in as " + userName);
        main_button.setVisibility(View.VISIBLE);
        login_text.setVisibility(View.VISIBLE);

        photoManager.updateCurrentUserName(userName);

        Intent toDetector = new Intent(this, DetectorActivity.class);
        startActivity(toDetector);
    }

    public void firebaseFromLoginToCreateAccount() {
        getFragmentManager().popBackStack();

        FirebaseCreateAccountFragment fcaf = FirebaseCreateAccountFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_frame, fcaf);

        ft.addToBackStack(null);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //start_login();

    }

    public void start_login(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            main_button.setVisibility(View.VISIBLE);
            login_text.setVisibility(View.VISIBLE);
            //main_button.setText("LOGIN");
            //login_text.setText("Click button to log in");
            mAuth.signOut(); // Will call updateUserDisplay via callback
            main_button.performClick();

        } else {

            FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            // Replace any other Fragment with our new Details Fragment with the right data
            ft.add(R.id.main_frame, flf);
            // Let us come back
            ft.addToBackStack(null);
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            main_button.setVisibility(View.INVISIBLE);
            login_text.setVisibility(View.INVISIBLE);

        }
    }
}

