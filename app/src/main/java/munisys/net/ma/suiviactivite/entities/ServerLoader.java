package munisys.net.ma.suiviactivite.entities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import munisys.net.ma.suiviactivite.MainActivity;
import munisys.net.ma.suiviactivite.dao.Db_gest;

/**
 * Created by mehdibouhafs on 29/07/2017.
 */

public class ServerLoader {

    private Db_gest db;
    private Context context;
    private User user;
    private ArrayList<Nature> natures;
    private ArrayList<Client> clients;
    private DbVersion dbVersion;

    public ServerLoader() {

    }

    public ServerLoader(Db_gest db, Context context) {
        this.db = db;
        this.context = context;
    }

    public ArrayList<Client> loadClientFromServer(){
        final ArrayList<Client> clients =  new ArrayList<>();
        db.dropTableClients();
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
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        return clients;
    }


    public void loadNatureFromServer(){
        final  ArrayList<Nature> natures =  new ArrayList<Nature>();
        db.dropTableNature();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_NATURES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray obj = new JSONArray(response);
                            db.dropTableNature();
                            for(int i =0 ;i<obj.length();i++){
                                JSONObject object = obj.getJSONObject(i);
                                Nature nature = new Nature(object.getInt("id"),object.getString("nature"));
                                db.insererClient(nature.getNature());
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
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    public void getDbVersionServer() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_DBVERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            DbVersion dbVersion = new DbVersion();
                            Log.e("responDBSERVER",obj.toString());
                            dbVersion.setId(obj.getInt("id"));
                            dbVersion.setDateUpdateNature(obj.getString("dateUpdateNature"));
                            dbVersion.setDateUpdateClient(obj.getString("dateUpdateClient"));
                            dbVersion.setDateUpdateUser(obj.getString("dateUpdateUser"));
                            db.dropTableDbVersion();
                            db.insererDbVersion(dbVersion.getDateUpdateUser(),dbVersion.getDateUpdateNature(),dbVersion.getDateUpdateClient());

                        } catch (JSONException e) {
                            Log.e("resp4 ","catch");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("eroorresponse","erruer DBSERVER requete");
                    }
                });


        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    public void getUserServer(String email,final String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_GET_USER+"/"+email+"/"+password,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            db.dropTableUsers();
                            JSONObject obj = new JSONObject(response);
                            User user1 = new User();
                            Log.e("userServer",obj.toString());
                            user1.setEmail(obj.getString("email"));
                            user1.setName(obj.getString("nom"));
                            user1.setPassword(password);
                            user1.setActive(obj.getBoolean("active"));
                            db.insererUser(user1.getName(),user1.getEmail(),user1.getPassword());
                            Log.e("userPrepare",user1.toString());

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
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }

    public void clientServerToLocale(){
        Log.e("clientServerToLocale","clientServerToLocale");
        ArrayList<Client> clients = getClients();
        for (Client client : clients){
            db.insererClient(client.getClient());
        }
    }

    public void natureServerToLocale(){
        Log.e("natureServerToLocale","natureServerToLocale");
        ArrayList<Nature> natures = getNatures();
        for (Nature nature : natures){
            db.insererClient(nature.getNature());
        }
    }

    public void userServerToLocale(User user){
        Log.e("UserServerToLocale","UserServerToLocale");

    }

    public void dBVersionServerToLocale(){
        Log.e("dBVersionServerToLocale","dBVersionServerToLocale");

    }

    public void checkUser(String email,String password){
        Log.e("check","user");
        getUserServer(email, password);

    }

    public void fillDatabase(User user){
        Log.e("fillDatabase","fillDatabase");
        loadClientFromServer();
        loadNatureFromServer();
        userServerToLocale(user);
        dBVersionServerToLocale();
        clientServerToLocale();
        natureServerToLocale();

    }

    public ServerLoader(User user, ArrayList<Nature> natures, ArrayList<Client> clients, DbVersion dbVersion) {
        this.user = user;
        this.natures = natures;
        this.clients = clients;
        this.dbVersion = dbVersion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        Log.e("userSet",user.toString());
        this.user = user;
    }

    public ArrayList<Nature> getNatures() {
        return natures;
    }

    public void setNatures(ArrayList<Nature> natures) {
        this.natures = natures;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public DbVersion getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(DbVersion dbVersion) {
        this.dbVersion = dbVersion;
    }
}
