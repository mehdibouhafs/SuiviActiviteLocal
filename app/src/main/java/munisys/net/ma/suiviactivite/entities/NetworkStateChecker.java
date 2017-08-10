package munisys.net.ma.suiviactivite.entities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import munisys.net.ma.suiviactivite.ListeActiviterActivity;
import munisys.net.ma.suiviactivite.MainActivity;
import munisys.net.ma.suiviactivite.dao.Db_gest;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private Db_gest db;
    private Session session;

    private DbVersion dbVersionServer;
    private DbVersion dbVersionLocale;
    private User user;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new Db_gest(context);
        session = new Session(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Toast.makeText(context, "On receive", Toast.LENGTH_LONG).show();
        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                if(checkUpdate()){
                    if(compareTwoDates(dbVersionServer.getDateUpdateUser(),dbVersionLocale.getDateUpdateUser())){
                        session.setLoggedIn(false);
                    }

                    if(compareTwoDates(dbVersionServer.getDateUpdateClient(),dbVersionLocale.getDateUpdateClient())){
                        for(Client client : loadClientFromServer()){
                            db.insererClient(client.getClient());
                        }
                    }

                    if(compareTwoDates(dbVersionServer.getDateUpdateNature(),dbVersionLocale.getDateUpdateNature())){
                        for(Nature nature : loadNatureFromServer()){
                            db.insererClient(nature.getNature());
                        }
                    }
                }

                //getting all the unsynced names
                ArrayList<ActiviterEmployer> activiterEmployers = db.getALLActivityByTag(0);
                for (ActiviterEmployer activiterEmployer : activiterEmployers) {
                    saveActiviterEmployerServer(activiterEmployer.getId(), activiterEmployer);
                }

            }
        }
    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveActiviterEmployerServer(final int id, final ActiviterEmployer activiterEmployer) {
        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject jsonObject = null;


        try {
            params.put("dateDebut", activiterEmployer.getDateDebut());
            params.put("dateFin", activiterEmployer.getDateFin());
            params.put("heureDebut", activiterEmployer.getHeureDebut());
            params.put("heureFin", activiterEmployer.getHeureFin());
            params.put("duree", activiterEmployer.getDuree());
            params.put("client", activiterEmployer.getClient());
            params.put("nature", activiterEmployer.getNature());
            params.put("descProjet", activiterEmployer.getDescProjet());
            params.put("lieu", activiterEmployer.getLieu());
            params.put("ville", activiterEmployer.getVille());
            jsonObject = new JSONObject(params);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("email", "a@b.c");
            jsonObject.put("user", jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest req = new JsonObjectRequest(MainActivity.URL_SAVE_ACTIVITER_EMPLOYER, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (!response.getString("dateDebut").isEmpty()) {
                                db.majActivityEmployer(id, MainActivity.ACTIVITER_SYNCED_WITH_SERVER);
                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(ListeActiviterActivity.DATA_SAVED_BROADCAST));
                            }
                            Log.e("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());

            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(req);
    }


    private DbVersion getDbVersionServer() {
        final DbVersion dbVersion = new DbVersion();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_DBVERSION,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("resp1 ", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            dbVersion.setDateUpdateClient(obj.getString("dateUpdateClient"));
                            dbVersion.setDateUpdateUser(obj.getString("dateUpdateNature"));
                            dbVersion.setDateUpdateNature(obj.getString("dateUpdateUser"));
                        } catch (JSONException e) {
                            Log.e("resp4 ", "catch");
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        setDbVersionServer(dbVersion);
        return dbVersion;
    }


    private DbVersion getDbVersionLocale(){

        DbVersion dbVersion = db.getDbVersion(1);
        setDbVersionLocale(dbVersion);
        return dbVersion;
    }

    private boolean checkUpdate(){
        DbVersion dbVersionServer =  getDbVersionServer();
        DbVersion dbVersionLocale = getDbVersionLocale();

        if(dbVersionServer.getDateUpdateClient().equals(dbVersionLocale.getDateUpdateClient()) && dbVersionServer.getDateUpdateNature().equals(dbVersionLocale.getDateUpdateNature()) && dbVersionServer.getDateUpdateUser().equals(dbVersionLocale.getDateUpdateUser())){
            return false;
        }else{
            return true;
        }
    }

    public void setDbVersionServer(DbVersion dbVersionServer) {
        this.dbVersionServer = dbVersionServer;
    }

    public void setDbVersionLocale(DbVersion dbVersionLocale) {
        this.dbVersionLocale = dbVersionLocale;
    }

    public boolean compareTwoDates(String date1,String date2){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date dateDebut= sdf.parse(date1);
            Date dateFin= sdf.parse(date2);
            if(dateDebut.compareTo(dateFin)>0){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void refereshUserTable(){
        db.dropTableUsers();


    }


    private ArrayList<Client> loadClientFromServer(){
        db.dropTableClients();
        final ArrayList<Client> clients =  new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_CLIENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray obj = new JSONArray(response);
                            for(int i =0 ;i<obj.length();i++){
                                JSONObject object = obj.getJSONObject(i);
                                clients.add(new Client(object.getInt("id"),object.getString("client")));
                            }

                        } catch (JSONException e) {
                            Log.e("resp4 ","catch");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("eroorresponse","erruer Clients requete");
                    }
                });

        return clients;
    }


    private ArrayList<Nature> loadNatureFromServer(){
        db.dropTableNature();
        final ArrayList<Nature> natures =  new ArrayList<Nature>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_NATURES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray obj = new JSONArray(response);
                            for(int i =0 ;i<obj.length();i++){
                                JSONObject object = obj.getJSONObject(i);
                                natures.add(new Nature(object.getInt("id"),object.getString("nature")));
                            }

                        } catch (JSONException e) {
                            Log.e("resp4 ","catch");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("eroorresponse","erruer Natures requete");
                    }
                });

        return natures;
    }


    private User getUserServer(String email, final String password) {


        HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", email);
            params.put("password", password);

        final User user = new User();
        JsonObjectRequest req = new JsonObjectRequest(MainActivity.URL_SAVE_ACTIVITER_EMPLOYER, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            user.setEmail(response.getString("email"));
                            user.setName(response.getString("nom"));
                            user.setPassword(password);
                            user.setActive((response.getBoolean("active")));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());

            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(req);
        setUser(user);
        return user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /* StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_ACTIVITER_EMPLOYER,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.e("resp1 ",response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(MainActivity.this,"Good or bad response"+obj.toString() ,Toast.LENGTH_SHORT).show();
                        Log.e("resp2 ",obj.toString());
                        if (!obj.getBoolean("error")) {
                            //updating the status in sqlite

                            //saveActiviterToLocalStorage(activiterEmployer,MainActivity.ACTIVITER_SYNCED_WITH_SERVER);
                            Toast.makeText(MainActivity.this,"Inserer avec succès",Toast.LENGTH_LONG).show();
                        }else{
                            Log.e("resp3 ",obj.toString());
                            saveActiviterToLocalStorage(activiterEmployer,MainActivity.ACTIVITER_UNSYNCED_WITH_SERVER);

                        }
                        Intent intent = new Intent(MainActivity.this,ListeActiviterActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.e("resp4 ","catch");
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("resp5  ","error response");
                    Toast.makeText(MainActivity.this,"Error Response listner",Toast.LENGTH_SHORT).show();
                    saveActiviterToLocalStorage(activiterEmployer,MainActivity.ACTIVITER_UNSYNCED_WITH_SERVER);
                    Intent intent = new Intent(MainActivity.this,ListeActiviterActivity.class);
                    startActivity(intent);
                }
            });
*/
}
