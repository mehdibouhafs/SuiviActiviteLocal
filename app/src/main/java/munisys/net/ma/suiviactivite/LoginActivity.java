package munisys.net.ma.suiviactivite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import munisys.net.ma.suiviactivite.dao.Db_gest;
import munisys.net.ma.suiviactivite.entities.ServerLoader;
import munisys.net.ma.suiviactivite.entities.Session;
import munisys.net.ma.suiviactivite.entities.User;
import munisys.net.ma.suiviactivite.entities.VolleySingleton;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ProgressDialog progressDialog;
    private EditText _emailText, _passwordText;
    private TextView _signupLink;
    private Button _loginButton;
    private Session session;
    private Db_gest db;
    private User user;
    private ServerLoader serverLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ButterKnife.bind(this);

        session = new Session(this);
        if(session.loggedIn()){
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
            finish();
        }


        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        db = new Db_gest(this);
        serverLoader = new ServerLoader(db,this);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, 11);
                overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess(email,password);
                        //progressDialog.dismiss();


                    }
                }, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 11 && resultCode == Activity.RESULT_OK) {

            session.setLoggedIn(true, (User) data.getSerializableExtra("user"));
            Intent intent = new Intent(this,MenuActivity.class);
            Toast.makeText(getApplicationContext(),"Bienvenue "+ ((User) data.getSerializableExtra("user")).getName(),Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String email,String password) {
        _loginButton.setEnabled(true);

        //serverLoader1.checkUser(email, password);

        getUserServer(email,password);
        //Log.e("user u",u.toString());


    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Echec d'authentification", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("saisissez une addresse email valide");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("entre 4 et 10 alphanumeric characteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }



    public void setUser(User user) {
        this.user = user;
    }


    public void getUserServer(String email,final String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

                JsonObjectRequest req = new JsonObjectRequest(MainActivity.URL_GET_USER,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            User user1 = new User();
                            user1.setEmail(response.getString("email"));
                            user1.setName(response.getString("nom"));
                            user1.setPassword(password);
                            user1.setActive(response.getBoolean("active"));
                            setUser(user1);
                            if( user1!=null){
                                db.dropTableUsers();
                                db.insererUser(user.getName(),user.getEmail(),user.getPassword());
                                session.setLoggedIn(true, user);
                                Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                                progressDialog.dismiss();
                                startActivity(intent);
                                finish();

                            }else {
                                progressDialog.dismiss();
                                onLoginFailed();
                            }
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

        VolleySingleton.getInstance(this).addToRequestQueue(req);

    }


}
