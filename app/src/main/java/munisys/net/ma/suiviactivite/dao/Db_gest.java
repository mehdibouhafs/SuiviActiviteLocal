package munisys.net.ma.suiviactivite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import munisys.net.ma.suiviactivite.entities.ActiviterEmployer;
import munisys.net.ma.suiviactivite.entities.User;

/**
 * Created by user on 20/04/2017.
 */

public class Db_gest extends SQLiteOpenHelper {
    final static String Db_name = "Munisys.db";


    public Db_gest(Context context, int version) {
        super(context, Db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            db.execSQL("Create Table User(id Integer Primary Key AUTOINCREMENT,name Text,email Text,password Text)");
            db.execSQL("Create Table ActiviterEmployer(id Integer Primary Key AUTOINCREMENT,employer Text,dateDebut DATETIME DEFAULT CURRENT_TIMESTAMP,dateFin DATETIME DEFAULT CURRENT_TIMESTAMP,duree Text," +
                    "heureDebut Text,HeureFin Text,client Text,nature Text,descProjet Text,lieu Text,ville Text,tag Integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("Drop Table User");
        db.execSQL("Drop Table ActiviterEmployer");
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

    public boolean insererActivityEmployer(String employer,String dateDebut, String dateFin, String heureDebut,String heureFin,String duree,String client,
                                           String nature, String descProjet, String lieu, String ville) {

        if(!getActivityBoolean(employer,dateDebut,dateFin,heureDebut,duree,heureFin,client,nature,descProjet,lieu,ville)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues valeurs = new ContentValues();
            valeurs.put("employer",employer);
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
            valeurs.put("tag",0);
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

    public Boolean getActivityBoolean(String employer,String date, String dateSortie, String heureDebut,String duree,String heureFin, String client,
                                      String nature, String descProjet, String lieu, String ville) {
        SQLiteDatabase db=getReadableDatabase();
        User e=new User();
        String selectQuery = "select * from ActiviterEmployer where employer ='"+employer+"' and dateDebut = '"+date +"' and heureDebut ='"+heureDebut+"' and heureFin = '"+heureFin+"' and client = '"+client+"' and nature = '"+nature+"' and lieu = '"+lieu+"'";
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

    public ArrayList<ActiviterEmployer> getALLActivity() {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<ActiviterEmployer> arl=new ArrayList<ActiviterEmployer>();
        Cursor cur=db.rawQuery("select * from ActiviterEmployer ORDER BY id DESC",null);

        if(cur.moveToFirst())
            while(cur.isAfterLast()==false)
            {

                arl.add(new ActiviterEmployer(cur.getInt(cur.getColumnIndex("id")),cur.getString(cur.getColumnIndex("employer")),
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
                arl.add(new ActiviterEmployer(cur.getInt(cur.getColumnIndex("id")),cur.getString(cur.getColumnIndex("employer")),
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


}

















