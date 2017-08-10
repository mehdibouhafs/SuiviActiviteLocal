package munisys.net.ma.suiviactivite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import munisys.net.ma.suiviactivite.entities.ActiviterEmployer;
import munisys.net.ma.suiviactivite.entities.Client;
import munisys.net.ma.suiviactivite.entities.DbVersion;
import munisys.net.ma.suiviactivite.entities.Nature;
import munisys.net.ma.suiviactivite.entities.User;

/**
 * Created by user on 20/04/2017.
 */

public class Db_gest extends SQLiteOpenHelper {
    final static String Db_name = "Munisys.db";
    final static int Db_version=10;


    public Db_gest(Context context) {
        super(context, Db_name, null, Db_version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

            db.execSQL("Create Table User(id Integer Primary Key AUTOINCREMENT,name Text,email Text,password Text)");
            db.execSQL("Create Table ActiviterEmployer(id Integer Primary Key AUTOINCREMENT,emailEmployer Text,dateDebut DATETIME DEFAULT CURRENT_TIMESTAMP,dateFin DATETIME DEFAULT CURRENT_TIMESTAMP,duree Text," +
                    "heureDebut Text,HeureFin Text,client Text,nature Text,descProjet Text,lieu Text,ville Text,tag Integer)");
            db.execSQL("Create Table Client(id Integer Primary Key AUTOINCREMENT,client Text)");
            db.execSQL("Create Table Nature(id Integer Primary Key AUTOINCREMENT,nature Text)");
            db.execSQL("Create Table DbVersion(id Integer Primary Key,dateUpdateUser DATETIME DEFAULT CURRENT_TIMESTAMP,dateUpdateNature DATETIME DEFAULT CURRENT_TIMESTAMP,dateUpdateClient DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("Drop Table User");
        db.execSQL("Drop Table ActiviterEmployer");
        /*db.execSQL("Drop Table Client");
        db.execSQL("Drop Table Nature");
        db.execSQL("Drop Table DbVersion");*/
        onCreate(db);
    }


    public boolean insererUser(String name, String email, String password) {

        if(!getUserBooleanMailAndName(email,name)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues valeurs = new ContentValues();

            valeurs.put("name", name);
            valeurs.put("email", email);
            valeurs.put("password", password);


            db.insert("User", null, valeurs);
            db.close();
            return true;
        }
        return false;
    }

    public boolean insererClient(String client) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues valeurs = new ContentValues();
            valeurs.put("client", client);
            db.insert("Client", null, valeurs);
            db.close();
            return true;
    }

    public boolean insererNature(String nature) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("nature", nature);
        db.insert("Nature", null, valeurs);
        db.close();
        return true;
    }

    public boolean insererDbVersion(String dateUpdateUser,String dateUpdateNature,String dateUpdateClient ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("dateUpdateUser", dateUpdateUser);
        valeurs.put("dateUpdateNature", dateUpdateNature);
        valeurs.put("dateUpdateClient", dateUpdateClient);
        db.insert("DbVersion", null, valeurs);
        db.close();
        return true;
    }

    public boolean insererActivityEmployer(String emailEmployer,String dateDebut, String dateFin, String heureDebut,String heureFin,String duree,String client,
                                           String nature, String descProjet, String lieu, String ville,int tag) {

        if(!getActivityBoolean(emailEmployer,dateDebut,heureDebut,heureFin,client,nature,lieu)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues valeurs = new ContentValues();
            valeurs.put("emailEmployer",emailEmployer);
            valeurs.put("dateDebut", dateDebut);
            valeurs.put("dateFin", dateFin);
            valeurs.put("heureDebut", heureDebut);
            valeurs.put("duree", duree);
            valeurs.put("heureFin", heureFin);
            valeurs.put("client", client);
            valeurs.put("nature", nature);
            valeurs.put("descProjet", descProjet);
            valeurs.put("lieu", lieu);
            valeurs.put("ville",ville);
            valeurs.put("tag",tag);
            db.insert("ActiviterEmployer", null, valeurs);
            db.close();
            return true;
        }
        return false;
    }


    public void deleteUser(int id) {
        SQLiteDatabase db=getWritableDatabase();
        db.delete("User","id=?",new String[]{String.valueOf(id)});
        db.close();

    }

    public void majUser(int id, String name, String email, String password) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues valeurs=new ContentValues();
        valeurs.put("name",name);
        valeurs.put("email",email);
        valeurs.put("password",password);
        //valeurs.put("idClient",idClient);

        db.update("User",valeurs,"id=?",new String[]{String.valueOf(id)});
        db.close();
    }


    public void majActivityEmployer(int id,int tag) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues valeurs=new ContentValues();

        valeurs.put("tag",tag);
        //valeurs.put("idClient",idClient);

        db.update("ActiviterEmployer",valeurs,"id=?",new String[]{String.valueOf(id)});
        db.close();
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from User where email = '"+email +"' and password = '"+password+"'";
        Cursor cur=db.rawQuery(selectQuery,null);

        if(cur.moveToFirst())
        {   e.setId(cur.getInt(cur.getColumnIndex("id")));
            e.setName(cur.getString(cur.getColumnIndex("name")));
            e.setEmail(cur.getString(cur.getColumnIndex("email")));
            e.setPassword(cur.getString(cur.getColumnIndex("password")));
        }
        cur.close();
        db.close();
        return e;
    }


    public Boolean getUserBoolean(String email, String password) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from User where email = '"+email +"' and password = '"+password+"'";
        Cursor cur=db.rawQuery(selectQuery,null);

        cur.moveToFirst();
        if(cur.getCount() > 0){
            return true;
        }
        cur.close();
        db.close();

        return false;
    }


    public Boolean getUserBooleanMailAndName(String email,String name) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from User where email = '"+email +"' or name ='"+name+"'";
        Cursor cur=db.rawQuery(selectQuery,null);

        cur.moveToFirst();
        if(cur.getCount() > 0){
            return true;
        }
        cur.close();
        db.close();

        return false;
    }

    public Boolean getActivityBoolean(String emailEmployer,String date, String heureDebut,String heureFin, String client,
                                      String nature,String lieu) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from ActiviterEmployer where emailEmployer ='"+emailEmployer+"' and dateDebut = '"+date +"' and heureDebut ='"+heureDebut+"' and heureFin = '"+heureFin+"' and client = '"+client+"' and nature = '"+nature+"' and lieu = '"+lieu+"'";
        Cursor cur=db.rawQuery(selectQuery,null);

        cur.moveToFirst();
        if(cur.getCount() > 0){
            return true;
        }
        cur.close();
        db.close();

        return false;
    }


    public Boolean getUserBooleanEmail(String email) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from User where email = '"+email +"'";
        Cursor cur=db.rawQuery(selectQuery,null);

        cur.moveToFirst();
        if(cur.getCount() > 0){
            return true;
        }
        cur.close();
        db.close();

        return false;
    }


    public ArrayList<User> getALLUser() {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<User> arl=new ArrayList<User>();
        Cursor cur=db.rawQuery("select * from User",null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {
                arl.add(new User(cur.getInt(cur.getColumnIndex("id")),
                        cur.getString(cur.getColumnIndex("name")),cur.getString(cur.getColumnIndex("email")),cur.getString(cur.getColumnIndex("password"))));
                cur.moveToNext();
            }
        cur.close();
        db.close();

        return arl;
    }

    public ArrayList<Client> getALLClients() {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<Client> arl=new ArrayList<Client>();
        Cursor cur=db.rawQuery("select * from Client",null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {
                arl.add(new Client(cur.getInt(cur.getColumnIndex("id")),
                        cur.getString(cur.getColumnIndex("client"))));
                cur.moveToNext();
            }
        cur.close();
        db.close();

        return arl;
    }

    public ArrayList<Nature> getALLNatures() {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<Nature> arl=new ArrayList<Nature>();
        Cursor cur=db.rawQuery("select * from Nature",null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {
                arl.add(new Nature(cur.getInt(cur.getColumnIndex("id")),
                        cur.getString(cur.getColumnIndex("nature"))));
                cur.moveToNext();
            }
        cur.close();
        db.close();

        return arl;
    }

    public DbVersion getDbVersion(int id) {
        SQLiteDatabase db=getReadableDatabase();
        DbVersion e=new DbVersion();
        String selectQuery = "select * from DbVersion where id = "+id;
        Cursor cur=db.rawQuery(selectQuery,null);

        if(cur.moveToFirst())
        {   e.setId(cur.getInt(cur.getColumnIndex("id")));
            e.setDateUpdateUser(cur.getString(cur.getColumnIndex("dateUpdateUser")));
            e.setDateUpdateClient(cur.getString(cur.getColumnIndex("dateUpdateClient")));
            e.setDateUpdateNature(cur.getString(cur.getColumnIndex("dateUpdateNature")));

        }
        cur.close();
        db.close();
        return e;
    }

    public ArrayList<ActiviterEmployer> getALLActivity() {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<ActiviterEmployer> arl=new ArrayList<ActiviterEmployer>();
        Cursor cur=db.rawQuery("select * from ActiviterEmployer ORDER BY id DESC",null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {

                arl.add(new ActiviterEmployer(cur.getInt(cur.getColumnIndex("id")),cur.getString(cur.getColumnIndex("emailEmployer")),
                        cur.getString(cur.getColumnIndex("dateDebut")),cur.getString(cur.getColumnIndex("dateFin")),
                        cur.getString(cur.getColumnIndex("heureDebut")),cur.getString(cur.getColumnIndex("HeureFin")),
                        cur.getString(cur.getColumnIndex("duree")),cur.getString(cur.getColumnIndex("client")),
                        cur.getString(cur.getColumnIndex("nature")),cur.getString(cur.getColumnIndex("descProjet")),
                        cur.getString(cur.getColumnIndex("lieu")),cur.getString(cur.getColumnIndex("ville")),cur.getInt(cur.getColumnIndex("tag"))));
                cur.moveToNext();
            }
        cur.close();
        db.close();

        return arl;
    }

    public ArrayList<ActiviterEmployer> getALLActivityByTag(int tag) {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<ActiviterEmployer> arl=new ArrayList<ActiviterEmployer>();
        String selectQuery = "select * from ActiviterEmployer where tag ="+tag+" ORDER BY id ASC" ;
        Cursor cur=db.rawQuery(selectQuery,null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {
                arl.add(new ActiviterEmployer(cur.getInt(cur.getColumnIndex("id")),cur.getString(cur.getColumnIndex("emailEmployer")),
                        cur.getString(cur.getColumnIndex("dateDebut")),cur.getString(cur.getColumnIndex("dateFin")),
                        cur.getString(cur.getColumnIndex("heureDebut")),cur.getString(cur.getColumnIndex("HeureFin")),
                        cur.getString(cur.getColumnIndex("duree")),cur.getString(cur.getColumnIndex("client")),
                        cur.getString(cur.getColumnIndex("nature")),cur.getString(cur.getColumnIndex("descProjet")),
                        cur.getString(cur.getColumnIndex("lieu")),cur.getString(cur.getColumnIndex("ville")),cur.getInt(cur.getColumnIndex("tag"))));
                cur.moveToNext();
            }
        cur.close();
        db.close();

        return arl;
    }


    public void dropTableUsers() {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from User");
        db.close();
    }

    public void dropTableClients() {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from Client");
        db.close();
    }

    public void dropTableNature() {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from Nature");
        db.close();
    }

    public void dropTableDbVersion() {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from DbVersion");
        db.close();
    }


}

















