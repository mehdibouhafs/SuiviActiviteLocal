package munisys.net.ma.suiviactivite;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import munisys.net.ma.suiviactivite.adaptor.AdaptorForActiviter;
import munisys.net.ma.suiviactivite.dao.Db_gest;
import munisys.net.ma.suiviactivite.entities.ActiviterEmployer;
import munisys.net.ma.suiviactivite.entities.NetworkStateChecker;
import munisys.net.ma.suiviactivite.entities.Session;
import munisys.net.ma.suiviactivite.mail.Mail;

public class ListeActiviterActivity extends AppCompatActivity {

    private final static String TAG ="ListeActiviterActivity";
    private RecyclerView recyclerView;
    private AdaptorForActiviter adaptorForActiviter;
    private Db_gest db;
    private ArrayList<ActiviterEmployer> activiterEmployers;
    private String fileName;
    private Session session;
    private boolean generate;

    //1 means data is synced and 0 means data is not synced
    public static final int ACTIVITER_SYNCED_WITH_SERVER = 1;
    public static final int ACTIVITER_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "munisys.net.ma.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private NetworkStateChecker networkStateChecker;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_activiter);

        networkStateChecker  = new NetworkStateChecker();
        registerReceiver(networkStateChecker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Mes interventions");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        db = new Db_gest(this);



        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                Toast.makeText(context,"On receive load",Toast.LENGTH_LONG).show();
                loadActivitesEmployer();
            }

        };
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        activiterEmployers = db.getALLActivity();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        session = new Session(ListeActiviterActivity.this);
        //calling the method to load all the stored names
        loadActivitesEmployer();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               creer();
            }
        });
    }

    public void loadActivitesEmployer(){
        activiterEmployers.clear();
        activiterEmployers = db.getALLActivity();
        adaptorForActiviter = new AdaptorForActiviter(activiterEmployers,ListeActiviterActivity.this);
        adaptorForActiviter.setList_don_filtred(activiterEmployers);
        recyclerView.setAdapter(adaptorForActiviter);
        adaptorForActiviter.notifyDataSetChanged();
    }

    private void refreshList() {
        adaptorForActiviter.notifyDataSetChanged();
    }

    public void creer(){
        Intent intent = new Intent(ListeActiviterActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ListeActiviterActivity.this,MenuActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lister, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.send:
                Toast.makeText(ListeActiviterActivity.this,"Send Mail",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("textSearch",newText);
                adaptorForActiviter.getFilter().filter(newText);
                return true;
            }
        });
    }



    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    public class SendMailTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ListeActiviterActivity.this, "Veuillez patienter", "Email en cours d'envoi..", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        protected Void doInBackground(Void... avoid)
        {
            try
            {
                Message message = Mail.sendMail2("mon Intervention",fileName);
                Transport.send(message);
            } catch (MessagingException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(networkStateChecker);
    }
}
