package munisys.net.ma.suiviactivite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import munisys.net.ma.suiviactivite.dao.Db_gest;
import munisys.net.ma.suiviactivite.entities.ActiviterEmployer;
import munisys.net.ma.suiviactivite.entities.Session;
import munisys.net.ma.suiviactivite.mail.Mail;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private final static String TAG ="MainActivity";

    private DatePickerDialog dpd;
    private TimePickerDialog tpd1,tpd2;
    private EditText date,heureDebut,heureFin,descProjet;
    private SearchableSpinner client,ville;
    private SearchableSpinner natureIntervention,lieu;
    private Button valider;
    private TextInputLayout layoutDate,layoutHeureDebut,layoutHeureFin,layoutDescProjet;
    private TextView dure;
    private LinearLayout duree;
    private String duree2;

    String[] natures;
    String[] lieux;
    String[] clients;
    String[] villes;

    private ActiviterEmployer activiterEmployer;

    private int heureDebut1,heureFin1;
    private int minuteDebut1,minuteFin1;
    private LinearLayout layoutsHeureFin;
    private String natureSelected,lieuSelected,clientSelected,villeSelected;

    private Db_gest db;
    private File file;
    private Session session;
    private String dateFin;
    private Boolean supDay=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //definir notre toolbar en tant qu'actionBar
        setSupportActionBar(toolbar);

        //afficher le bouton retour
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Nouvelle intervention");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
        //SendMailTask sendMailTask = new SendMailTask();
        //sendMailTask.execute(message);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
                if(supDay) {
                    DateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                    //Date startDate = sdf.parse(date.getText().toString());
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(date.getText().toString()));
                        c.add(Calendar.DATE, 1);  // number of days to add
                        dateFin = sdf.format(c.getTime());
                        Log.e("DateFin", dateFin);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }else{
                    dateFin = date.getText().toString();
                }
                activiterEmployer = new ActiviterEmployer();
                String dateDebutDatetime = date.getText().toString() + " "+heureDebut.getText().toString()+":00";
                String dateFinDatetime = dateFin.toString() + " "+heureFin.getText().toString()+":00";
                Log.e("dateDebutDatetime",dateDebutDatetime);
                activiterEmployer.setDate(dateDebutDatetime);
                activiterEmployer.setDateSortie(dateFinDatetime);
                activiterEmployer.setHeureDebut(heureDebut.getText().toString());
                activiterEmployer.setHeureFin(heureFin.getText().toString());
                activiterEmployer.setDuree(duree2);
                activiterEmployer.setNature(natureSelected);
                activiterEmployer.setDescProjet(descProjet.getText().toString());
                activiterEmployer.setLieu(lieuSelected);
                activiterEmployer.setVille(villeSelected);
                activiterEmployer.setClient(clientSelected);

                Log.e("activiteEmployer",activiterEmployer.toString());

                if(db.insererActivityEmployer(session.getNameUser(),activiterEmployer.getDate(),activiterEmployer.getDateSortie(),activiterEmployer.getHeureDebut(),
                        activiterEmployer.getHeureFin(),activiterEmployer.getDuree(),activiterEmployer.getClient(),activiterEmployer.getNature(),
                        activiterEmployer.getDescProjet(),activiterEmployer.getLieu(),activiterEmployer.getVille())== true){
                    Toast.makeText(MainActivity.this,"Inserer avec succès",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this,ListeActiviterActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this,"Error insertion",Toast.LENGTH_LONG).show();
                }

            }
        });


    }




    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear+1;
        String dayOfmonth = dayOfMonth+"";
        String monthOfyear = month+"";
        if(dayOfMonth>=1 && dayOfMonth<10){
            dayOfmonth = "0"+dayOfMonth;
        }
        if(month>=1 && month<10){
            monthOfyear = "0"+month;
        }
        String date1 = dayOfmonth+"/"+monthOfyear+"/"+year;
        dateFin = date1;
        date.setText(date1);
        heureDebut.setText("");
        heureFin.setText("");

    }


    public void init(){

        date = (EditText) findViewById(R.id.date);
        heureDebut = (EditText) findViewById(R.id.heureDebut);
        heureFin = (EditText) findViewById(R.id.heureFin);
        descProjet = (EditText) findViewById(R.id.descProjet);
        client = (SearchableSpinner) findViewById(R.id.client);
        ville = (SearchableSpinner) findViewById(R.id.ville);
        natureIntervention = (SearchableSpinner) findViewById(R.id.nature);
        lieu = (SearchableSpinner) findViewById(R.id.lieu);
        valider = (Button) findViewById(R.id.valider);
        layoutDate = (TextInputLayout) findViewById(R.id.layoutDate);
        layoutDescProjet = (TextInputLayout) findViewById(R.id.layout_descProjet);
        layoutHeureDebut = (TextInputLayout) findViewById(R.id.layoutHeureDebut);
        layoutHeureFin = (TextInputLayout) findViewById(R.id.layoutHeureFin);
        layoutDescProjet = (TextInputLayout) findViewById(R.id.layout_descProjet);
        dure = (TextView) findViewById(R.id.dure);
        duree = (LinearLayout) findViewById(R.id.duree);
        session = new Session(MainActivity.this);

        natures = getResources().getStringArray(R.array.natures);
        lieux = getResources().getStringArray(R.array.lieux);
        clients = getResources().getStringArray(R.array.clients);
        villes  = getResources().getStringArray(R.array.villes);
        layoutsHeureFin  = (LinearLayout) findViewById(R.id.layoutsHeureFin);
        db = new Db_gest(this,5);


        final Calendar now = Calendar.getInstance();
         dpd = DatePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getFragmentManager(),"Datepickerdialog");
            }
        });

        date.setFocusableInTouchMode(false);
        date.setFocusable(false);

        heureDebut.setFocusableInTouchMode(false);
        heureDebut.setFocusable(false);
        heureFin.setFocusableInTouchMode(false);
        heureFin.setFocusable(false);
        descProjet.setSelected(false);
        //client.setFocusableInTouchMode(false);


        dpd.setTitle("Date de l'intervention");
        dpd.setYearRange(2017,2030);
        dpd.setMaxDate(Calendar.getInstance());


        tpd1 = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String hours = hourOfDay+"";
                String minutes = minute+"";
                if(hourOfDay>=0 && hourOfDay<10){
                    hours = "0"+hours;
                    Log.e("hours",hours);
                }
                if(minute>=0 && minute<10){
                    minutes="0"+minute;
                    Log.e("minutes",minutes);
                }

                String time = hours+":"+minutes;
                heureDebut1 = Integer.parseInt(hours);
                minuteDebut1 = minute;
                heureDebut.setText(time);

                if(duree.getVisibility()==View.VISIBLE){
                    AfficherDuree afficherDuree = new AfficherDuree();
                    afficherDuree.execute();
                }


            }
        },now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);

        tpd1.setTitle("Heure de début de l'intervention");
        heureDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd1.show(getFragmentManager(),"Datepickerdialog");
            }
        });


        tpd2 = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String hoursFin = hourOfDay+"";
                String minuteFin = minute+"";
                heureFin1 = hourOfDay;
                minuteFin1 = minute;
                if(hourOfDay>=0 && hourOfDay<10){
                    hoursFin = "0"+hoursFin;
                }
                if(minute>=0 && minute<10){
                    minuteFin="0"+minuteFin;
                }
                AfficherDuree afficherDuree = new AfficherDuree();
                afficherDuree.execute();
                String time5 = hoursFin+":"+minuteFin;
                heureFin.setText(time5);
            }
        }, heureDebut1, minuteDebut1, true);

        tpd2.setTitle("Heure de fin d'intérvention");


        heureFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd2.show(getFragmentManager(),"Datepickerdialog");
            }
        });



        ArrayAdapter<String> natureAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, natures);
        natureIntervention.setAdapter(natureAdapter);
        natureIntervention.setTitle("Sélectionner la nature de l'intervention");
        natureIntervention.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {
                    natureSelected = natures[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        final ArrayAdapter<String> lieuAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, lieux);
        lieu.setAdapter(lieuAdapter);
        lieu.setTitle("Sélectionner le lieu");

        lieu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1){
                    lieuSelected = lieux[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> clientAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, clients);
        client.setAdapter(clientAdapter);
        client.setTitle("Sélectionner le client");

        ArrayAdapter<String> villesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, villes);
        ville.setAdapter(villesAdapter);
        ville.setTitle("Sélectionner la ville");


        client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {
                    clientSelected = clients[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ville.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {
                    villeSelected = villes[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    @Override
    protected void onPause() {
        super.onPause();

        dpd.dismissOnPause(true);
        tpd1.dismissOnPause(true);
        if(tpd2!=null) {
            tpd2.dismissOnPause(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    dpd.show(getFragmentManager(),"Datepickerdialog");
                }
            }
        });
        heureDebut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    layoutsHeureFin.setVisibility(View.GONE);
                    duree.setVisibility(View.GONE);
                    tpd1.show(getFragmentManager(),"Datepickerdialog");
                }
            }
        });

        heureFin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    tpd2.show(getFragmentManager(),"Datepickerdialog");
                }
            }
        });

    }



    private void submitForm() {
        if (!validateDate()) {
            return;
        }

        if (!validateHeureDebut()) {
            return;
        }

        if (!validateHeureFin()) {
            return;
        }


        if (!validateClient()) {
            return;
        }
        if (!validateNature()) {
            return;
        }

        if (!validateDescProjet()) {
            return;
        }

        if (!validateLieu()) {
            return;
        }

        if (!validateVille()) {
            return;
        }

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateDate() {
        if (date.getText().toString().isEmpty()) {
            layoutDate.setError("La date d'intérvention est obligatoire ! ");
            requestFocus(date);
            return false;
        } else {
            layoutDate.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateHeureDebut() {
        String heureDebut1 = heureDebut.getText().toString().trim();

        if (heureDebut1.isEmpty()) {
            layoutHeureDebut.setError("L'heure de début est obligatoire ! ");
            requestFocus(heureDebut);
            return false;
        } else {
            layoutHeureDebut.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateHeureFin() {
        String heureFin1 = heureFin.getText().toString().trim();

        if (heureFin1.isEmpty()) {
            layoutHeureFin.setError("L'heure de la fin d'intérvention est obligatoire ! ");
            requestFocus(heureFin);
            return false;
        } else {
            layoutHeureFin.setErrorEnabled(false);
        }

        return true;
    }




    private boolean validateClient() {
        if (client.getSelectedItem()==null) {

            requestFocus(client);
            return false;
        } else {
            //layoutDate.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNature() {
       /* if (natureIntervention.ge ==null) {

            requestFocus(client);
            return false;
        } else {
            //layoutDate.setErrorEnabled(false);
        }
*/
        return true;
    }

    private boolean validateDescProjet(){
        if (descProjet.getText().toString().isEmpty()) {
            layoutDescProjet.setError("La description du projet où le comm. action est obligatoire ! ");
            requestFocus(descProjet);
            return false;
        } else {
            layoutDescProjet.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLieu() {
       /* if (natureIntervention.ge ==null) {

            requestFocus(client);
            return false;
        } else {
            //layoutDate.setErrorEnabled(false);
        }
*/
        return true;
    }

    private boolean validateVille() {
       /* if (natureIntervention.ge ==null) {

            requestFocus(client);
            return false;
        } else {
            //layoutDate.setErrorEnabled(false);
        }
*/
        return true;
    }

    public void reset(){
        date.setText("");
        heureDebut.setText("");
        heureFin.setText("");
        client.setSelection(0);
        natureIntervention.setSelection(0);
        descProjet.setText("");
        lieu.setSelection(0);
        ville.setSelection(0);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }




    public class AfficherDuree extends AsyncTask<Object, Object, String>
    {

        public String afficherDuree() {
            String time = heureFin1 + ":" + minuteFin1;
            String time1 = heureDebut1 + ":" + minuteDebut1 + ":00";
            String time2 = time + ":00";
            String time3 = "23" + ":" + "59" + ":" + "59";
            Date d1 = null;
            Date d2 = null;
            Date d3 = null;
            Date d4 = null;
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            try {
                d1 = format.parse(time1);
                d2 = format.parse(time2);
                d4 = format.parse(time3);
                DateTime dt1 = new DateTime(d1);
                DateTime dt2 = new DateTime(d2);
                DateTime dt3 = new DateTime(d4);
                dt3.plusSeconds(1);
                int hoursday = 24;
                int h1, min1;
                String hoursAfficher = null;
                if (((Hours.hoursBetween(dt1, dt2).getHours() % 24) <= 0) || ((Minutes.minutesBetween(dt1, dt2).getMinutes() % 60) <= 0)) {
                    if (((Hours.hoursBetween(dt1, dt2).getHours() % 24) == 0) && ((Minutes.minutesBetween(dt1, dt2).getMinutes() % 60) == 0)) {
                        hoursAfficher = "24 heures!";
                        duree2 = "24h";
                    } else {
                        h1 = (((Hours.hoursBetween(dt1, dt2).getHours() % 24)) * -1);
                        min1 = (((Minutes.minutesBetween(dt1, dt2).getMinutes() % 60)) * -1);
                        String newtime = h1 + ":" + min1 + ":00";
                        Log.e("newtime", newtime);
                        d3 = format.parse(newtime);
                        DateTime dt4 = new DateTime(d3);
                        Calendar calendar = Calendar.getInstance();
                        String nemTime2 = ((Hours.hoursBetween(dt3, dt4).getHours() % 24) * -1) + ":" + ((Minutes.minutesBetween(dt3, dt4).getMinutes() % 60) * -1) + ":" + "00";
                        calendar.setTime(format.parse(nemTime2));
                        Log.e("nemtime2", nemTime2);
                        calendar.add(Calendar.MINUTE, 1);
                        int h = calendar.get(Calendar.HOUR);
                        int m = calendar.get(Calendar.MINUTE);
                        if ((m == 0)) {
                            hoursAfficher = calendar.get(Calendar.HOUR_OF_DAY) + " Heure(s)";
                            duree2 = calendar.get(Calendar.HOUR_OF_DAY)+"h";
                        } else {
                            if (h == 0 && m > 0) {
                                hoursAfficher = calendar.get(Calendar.MINUTE) + " Minute(s)";
                                duree2 = calendar.get(Calendar.MINUTE)+"min";
                            } else {
                                hoursAfficher = calendar.get(Calendar.HOUR_OF_DAY) + " heure(s) et " + calendar.get(Calendar.MINUTE) + " minute(s)";
                                duree2 = calendar.get(Calendar.HOUR_OF_DAY) +"h:"+calendar.get(Calendar.MINUTE)+"min";
                            }
                        }

                    }
                    supDay = true;



                } else {
                    supDay = false;
                    int h = (Hours.hoursBetween(dt1, dt2).getHours() % 24);
                    int m = (Minutes.minutesBetween(dt1, dt2).getMinutes() % 60);

                    if ((m == 0)) {
                        hoursAfficher = Hours.hoursBetween(dt1, dt2).getHours() % 24 + " heure(s)";
                        duree2 = Hours.hoursBetween(dt1, dt2).getHours() % 24+"h";

                    } else {
                        if (h == 0 && m > 0) {
                            hoursAfficher = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60 + " minute(s)";
                            duree2 = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60+"min";
                        } else {
                            hoursAfficher = Hours.hoursBetween(dt1, dt2).getHours() % 24 + " heure(s) et " + Minutes.minutesBetween(dt1, dt2).getMinutes() % 60 + " minute(s)";
                            duree2 = Hours.hoursBetween(dt1, dt2).getHours() % 24+"h:"+Minutes.minutesBetween(dt1, dt2).getMinutes() % 60+"min";
                        }
                    }

                }

                return hoursAfficher;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected String doInBackground(Object... params) {
            String res  = afficherDuree();
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dure.setText(s);
            duree.setVisibility(View.VISIBLE);
        }
    }



    public String getDateTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);

    }
    public static Date getDate(String dateInventaire){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateInventaire);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this,MenuActivity.class));
    }




}
