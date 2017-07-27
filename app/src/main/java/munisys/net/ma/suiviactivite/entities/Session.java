package munisys.net.ma.suiviactivite.entities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mehdibouhafs on 12/07/2017.
 */

public class Session {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myapp",Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(boolean loggedIn,User user){
        editor.putBoolean("loggedMode",loggedIn);
        editor.putString("id",user.getId()+"");
        editor.putString("email",user.getEmail());
        editor.putString("name",user.getName());
        editor.commit();
    }
    public void setLoggedIn(boolean loggedIn){
        editor.putBoolean("loggedMode",loggedIn);
        editor.commit();
    }



    public boolean loggedIn(){

        return prefs.getBoolean("loggedMode",false);
    }

    public String getNameUser(){
        return prefs.getString("name","Nom utilisateur");
    }

    public String getEmail(){
        return  prefs.getString("email","emailIntervenant");
    }


}
