package munisys.net.ma.suiviactivite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_activiter);
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


        db = new Db_gest(this,5);
        activiterEmployers = db.getALLActivity();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adaptorForActiviter = new AdaptorForActiviter(activiterEmployers,ListeActiviterActivity.this);
        recyclerView.setAdapter(adaptorForActiviter);
        session = new Session(ListeActiviterActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               creer();
            }
        });
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
                GenerateExcel generateExcel = new GenerateExcel();
                generateExcel.execute();
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

    private boolean saveExcelFile() {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("interventions");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Date Debut");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Date Fin");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Heure Debut");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Heure Fin");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Durée");
        c.setCellStyle(cs);


        c = row.createCell(5);
        c.setCellValue("Employer");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Client");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("Nature de l'intervention");
        c.setCellStyle(cs);

        c = row.createCell(8);
        c.setCellValue("Description projet / Comm.Action");
        c.setCellStyle(cs);

        c = row.createCell(9);
        c.setCellValue("Lieu");
        c.setCellStyle(cs);

        c = row.createCell(10);
        c.setCellValue("Ville");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));
        sheet1.setColumnWidth(6, (15 * 500));
        sheet1.setColumnWidth(7, (15 * 500));
        sheet1.setColumnWidth(8, (15 * 500));
        sheet1.setColumnWidth(9, (15 * 500));
        sheet1.setColumnWidth(10, (15 * 500));

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName1 = session.getNameUser()+"_"+timeStamp+".xls";
        fileName = fileName1;
        ArrayList<ActiviterEmployer> activiterEmployers = db.getALLActivityByTag(0);
        int i=1;
        for(ActiviterEmployer activiterEmployer:activiterEmployers){
            Row row1 = sheet1.createRow(i);
            db.majActivityEmployer(activiterEmployer.getId(),1);
            c = row1.createCell(0);
            c.setCellValue(activiterEmployer.getDate());
            c = row1.createCell(1);
            c.setCellValue(activiterEmployer.getDateSortie());
            c = row1.createCell(2);
            c.setCellValue(activiterEmployer.getHeureDebut());
            c = row1.createCell(3);
            c.setCellValue(activiterEmployer.getHeureFin());
            c = row1.createCell(4);
            c.setCellValue(activiterEmployer.getDuree());
            c = row1.createCell(5);
            c.setCellValue(activiterEmployer.getEmployer());
            c = row1.createCell(6);
            c.setCellValue(activiterEmployer.getClient());
            c = row1.createCell(7);
            c.setCellValue(activiterEmployer.getNature());
            c = row1.createCell(8);
            c.setCellValue(activiterEmployer.getDescProjet());
            c = row1.createCell(9);
            c.setCellValue(activiterEmployer.getLieu());
            c = row1.createCell(10);
            c.setCellValue(activiterEmployer.getVille());
            i++;
        }


        // Create a path where we will place our List of objects on external storage
        File file = new File(this.getExternalFilesDir(null), fileName1);
        //ContextWrapper cw = new ContextWrapper(context);
        //File directory = cw.getDir("files", Context.MODE_PRIVATE);
        //file=new File(directory,fileName);
        //Log.e("filepath",file.getPath());
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
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

    public class GenerateExcel extends AsyncTask<Void, Void, Boolean>
    {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ListeActiviterActivity.this, "Veuillez patienter", "Fichier en cours de génération...", true, false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return saveExcelFile();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean){
                adaptorForActiviter.setList_don_filtred(db.getALLActivity());
                adaptorForActiviter.notifyDataSetChanged();
                SendMailTask sendMailTask = new SendMailTask();
                sendMailTask.execute();
            }else{
                generate = false;
            }
        }
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


}
