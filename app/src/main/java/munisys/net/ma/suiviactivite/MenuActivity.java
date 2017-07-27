package munisys.net.ma.suiviactivite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import munisys.net.ma.suiviactivite.entities.Session;

public class MenuActivity extends AppCompatActivity {

    Button creer,lister;
    CardView cardCreer,cardLister;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    protected Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);




        init();
        creer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creer();
            }
        });

        lister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lister();

            }
        });

        cardCreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creer();
            }
        });

        cardLister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lister();
            }
        });


    }



    public void creer(){
        Intent  intent = new Intent(MenuActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void lister(){
        Intent  intent = new Intent(MenuActivity.this,ListeActiviterActivity.class);
        startActivity(intent);
        finish();
    }


    public void init(){
        creer = (Button) findViewById(R.id.creer);
        lister = (Button) findViewById(R.id.lister);
        cardCreer = (CardView) findViewById(R.id.cardCreer);
        cardLister = (CardView) findViewById(R.id.cardLister);
        session = new Session(this);
        if(!session.loggedIn()){
            logout();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
    }


    private void logout() {
        session.setLoggedIn(false);
        finish();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Intent intent;
                switch (id){
                    case R.id.home:

                        //Toast.makeText(getApplicationContext(),"We are already in home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        intent = new Intent(getApplicationContext(),MenuActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.newActiviter:
                        drawerLayout.closeDrawers();
                        intent = new Intent(MenuActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.lister:
                        Toast.makeText(getApplicationContext(),"new inventaire",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        intent = new Intent(MenuActivity.this,ListeActiviterActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        finish();
                }
                return true;
            }
        });


        View header = navigationView.getHeaderView(0);

        TextView name_user = (TextView)header.findViewById(R.id.name_user);
        name_user.setText(session.getNameUser());
        ImageView logout = (ImageView) header.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

}
