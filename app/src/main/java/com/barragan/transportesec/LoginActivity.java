package com.barragan.transportesec;

import android.content.Intent;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.Console;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.barragan.transportesec.models.PasajeroBus;
import com.barragan.transportesec.models.Usuario;
import com.barragan.transportesec.models.Ruta;
import com.barragan.transportesec.models.Horario;
import com.barragan.transportesec.models.Pasajero;
import com.barragan.transportesec.models.Licencia;
import com.barragan.transportesec.models.Cooperativa;
import com.barragan.transportesec.models.Bus;
import com.barragan.transportesec.Toaster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public Usuario usuario;
    public Licencia licencia;
    public Cooperativa cooperativa;
    public Bus bus;
    public PasajeroBus itinerario;
    public Horario hora;
    public Ruta ruta;
    public JSONArray rutas;
    public JSONArray horarios;
    public JSONArray pasajeros;
    public List ListaRutas;
    public List ListaHorarios;
    public List ListaPasajeros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        itinerario = new PasajeroBus();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Muestra el Spinner de progreso, y envia al background la tarea para realizar consulta de autenticacion con el servidor web
            showProgress(true);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String url = getString(R.string.servidor).concat(getString(R.string.login_url));
            Map<String, String> parametros = new HashMap();
            parametros.put("email", mEmailView.getText().toString());
            parametros.put("password", mPasswordView.getText().toString());

            JSONObject parameters = new JSONObject(parametros);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //StringBuilder formattedResult = new StringBuilder();
                        if (response.isNull("error") == true) {

                            Gson gson = new Gson();

                            //Datos del Usuario autenticado
                            usuario = gson.fromJson(response.getJSONObject("usuario").toString(), Usuario.class);

                            //Datos de la Licencia
                            licencia = gson.fromJson(response.getJSONObject("licencia").toString(), Licencia.class);

                            //Datos de la Cooperativa
                            cooperativa = gson.fromJson(response.getJSONObject("cooperativa").toString(), Cooperativa.class);

                            //Datos del Bus asignado
                            bus = gson.fromJson(response.getJSONObject("bus").toString(), Bus.class);

                            //Listado de rutas disponibles
                            rutas = response.getJSONArray("rutas");
                            ListaRutas = Arrays.asList(gson.fromJson(rutas.toString(), Ruta[].class));
                            Type typeListaRutas = new TypeToken<List<Ruta>>() {}.getType();
                            String jsonListaRutas = gson.toJson(ListaRutas, typeListaRutas);

                            //Listado de horarios disponibles por ruta
                            horarios = response.getJSONArray("horarios");
                            ListaHorarios = Arrays.asList(gson.fromJson(horarios.toString(), Horario[].class));
                            Type typeListaHorarios = new TypeToken<List<Horario>>() {}.getType();
                            String jsonListaHorarios = gson.toJson(ListaHorarios, typeListaHorarios);

                            //Listado de pasajeros registrados hasta el momento en el sistema
                            pasajeros = response.getJSONArray("pasajeros");
                            ListaPasajeros = Arrays.asList(gson.fromJson(pasajeros.toString(), Pasajero[].class));
                            Type typeListaPasajeros = new TypeToken<List<Pasajero>>() {}.getType();
                            String jsonListaPasajeros = gson.toJson(ListaPasajeros, typeListaPasajeros);

                            itinerario.setListaCerrada(0);

                            //Ventana (actividad) que muestra un menu con informacion del chofer
                            Intent myMenu = new Intent(getBaseContext(), MenuActivity.class);

                            //Parametrizacion de valores entre pantallas (datos del chofer, itinerario, pasajeros, etc
                            myMenu.putExtra("usuario", (new Gson()).toJson(usuario));
                            myMenu.putExtra("licencia", (new Gson()).toJson(licencia));
                            myMenu.putExtra("cooperativa", (new Gson()).toJson(cooperativa));
                            myMenu.putExtra("bus", (new Gson()).toJson(bus));
                            myMenu.putExtra("hora", (new Gson()).toJson(hora));
                            myMenu.putExtra("ruta", (new Gson()).toJson(ruta));
                            myMenu.putExtra("itinerario", (new Gson()).toJson(itinerario));
                            myMenu.putExtra("rutas", jsonListaRutas);
                            myMenu.putExtra("horarios", jsonListaHorarios);
                            myMenu.putExtra("pasajeros", jsonListaPasajeros);
                            myMenu.putExtra("fecha_servidor", response.get("fecha_servidor").toString());

                            //Iniciar MENU
                            startActivity(myMenu);

                        } else {
                            Toaster.makeLongToast(getApplicationContext(), response.getString("error"), 10000);
                        }

                    } catch (JSONException e) {
                        Toaster.makeLongToast(getApplicationContext(), e.getMessage(), 6000);
                    }
                    showProgress(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), error.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

