package com.barragan.transportesec;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.barragan.transportesec.models.Bus;
import com.barragan.transportesec.models.Cooperativa;
import com.barragan.transportesec.models.Horario;
import com.barragan.transportesec.models.Licencia;
import com.barragan.transportesec.models.Pasajero;
import com.barragan.transportesec.models.PasajeroBus;
import com.barragan.transportesec.models.Ruta;
import com.barragan.transportesec.models.Usuario;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    public String fecha_servidor;
    public Usuario usuario;
    public Licencia licencia;
    public Cooperativa cooperativa;
    public Bus bus;
    public Horario hora;
    public Ruta ruta;
    public PasajeroBus itinerario;
    public JSONArray rutas;
    public JSONArray horarios;
    public JSONArray pasajeros;
    public List<Ruta> ListaRutas;
    public List<Horario> ListaHorarios;
    public List<Pasajero> ListaPasajeros;
    private TextView tvNombresChofer;
    private TextView tvNombreCooperativa;
    private TextView tvNumeroUnidad;
    private TextView tvRuta;
    private TextView tvHora;
    private TextView tvNombresPasajero;
    private TextView tvApellidosPasajero;
    private TextView tvTotalPasajeros;

    private AlertDialog.Builder builder;
    private View mProgressView;
    private View mMainFormView;

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton useFlash;
    private EditText barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeValue = (EditText)findViewById(R.id.barcode_value);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        //Valores de informacion del usuario
        Intent intent = getIntent();
        fecha_servidor = intent.getStringExtra("fecha_servidor");
        usuario = (new Gson()).fromJson(intent.getStringExtra("usuario"), Usuario.class);
        licencia = (new Gson()).fromJson(intent.getStringExtra("licencia"), Licencia.class);
        cooperativa = (new Gson()).fromJson(intent.getStringExtra("cooperativa"), Cooperativa.class);
        bus = (new Gson()).fromJson(intent.getStringExtra("bus"), Bus.class);
        hora = (new Gson()).fromJson(intent.getStringExtra("hora"), Horario.class);
        ruta = (new Gson()).fromJson(intent.getStringExtra("ruta"), Ruta.class);
        itinerario = (new Gson()).fromJson(intent.getStringExtra("itinerario"), PasajeroBus.class);
        Type typeListaRuta = new TypeToken<List<Ruta>>() { }.getType();
        ListaRutas = (new Gson()).fromJson(intent.getStringExtra("rutas"), typeListaRuta);
        Type typeListaHorarios = new TypeToken<List<Horario>>() { }.getType();
        ListaHorarios = (new Gson()).fromJson(intent.getStringExtra("horarios"), typeListaHorarios);
        Type typeListaPasajeros = new TypeToken<List<Pasajero>>() { }.getType();
        ListaPasajeros = (new Gson()).fromJson(intent.getStringExtra("pasajeros"), typeListaPasajeros);

        tvNombresChofer = findViewById(R.id.nombre_chofer);
        tvNombresChofer.setText(usuario.getNombres());
        tvNombreCooperativa = findViewById(R.id.nombre_cooperativa);
        tvNombreCooperativa.setText(cooperativa.getNombre());
        tvNumeroUnidad = findViewById(R.id.numero_unidad);
        tvNumeroUnidad.setText("Numero Unidad: " + bus.getNumeroUnidad());
        tvRuta = findViewById(R.id.ruta_bus);
        tvRuta.setText(ruta.getOrigen() + '-' + ruta.getDestino());
        tvHora = findViewById(R.id.hora);
        tvHora.setText(hora.getHoraSalida());
        tvNombresPasajero = findViewById(R.id.nombres);
        tvApellidosPasajero = findViewById(R.id.apellidos);
        tvTotalPasajeros = findViewById(R.id.total_pasajeros);

        builder = new AlertDialog.Builder(this);
        mMainFormView = findViewById(R.id.main_form);
        mProgressView = findViewById(R.id.main_progress);

        //Cuando se ingresa una cedula manualmente, se realiza una busqueda de la misma en la lista de pasajeros actual
        barcodeValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 10) {
                    for (Pasajero pasajero : ListaPasajeros) {
                        if (pasajero.getCedula().equals(s.toString())) {
                            tvNombresPasajero.setText(pasajero.getNombres());
                            tvApellidosPasajero.setText(pasajero.getApellidos());
                        }
                    }
                } else {
                    tvNombresPasajero.setText("");
                    tvApellidosPasajero.setText("");
                }
            }
        });

        //Impresion del total de pasajeros que estan cargados en el itinerario actual
        tvTotalPasajeros.setText(String.format(getString(R.string.total_pasajeros), String.valueOf(itinerario.ListaPasajeros.size())));

        //Definicion de Eventos para los botones de pausa y cierre de lista y para el boton de guardar pasajero en el itinerario
        Button bGuardarPasajeroLista = (Button) findViewById(R.id.guardar_pasajero_lista);
        bGuardarPasajeroLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cedula = barcodeValue.getText().toString();
                String nombres = tvNombresPasajero.getText().toString();
                String apellidos = tvApellidosPasajero.getText().toString();
                View focusView = null;

                //Cedula correcta
                if (TextUtils.isEmpty(cedula) || !isCedulaValida(cedula)) {
                    barcodeValue.setError(getString(R.string.error_cedula));
                    focusView = barcodeValue;
                    focusView.requestFocus();
                    return;
                }

                //Nombres correctos
                if (TextUtils.isEmpty(nombres) || !isTextoValido(nombres)) {
                    tvNombresPasajero.setError(getString(R.string.error_texto));
                    focusView = tvNombresPasajero;
                    focusView.requestFocus();
                    return;
                }

                //Apellidos correctos
                if (TextUtils.isEmpty(apellidos) || !isTextoValido(apellidos)) {
                    tvApellidosPasajero.setError(getString(R.string.error_texto));
                    focusView = tvApellidosPasajero;
                    focusView.requestFocus();
                    return;
                }

                //Validar que no se encuentre en la lista
                if (isPasajeroRegistradoItinerario(cedula)) {
                    barcodeValue.setText("");
                    tvNombresPasajero.setText("");
                    tvApellidosPasajero.setText("");
                    focusView = barcodeValue;
                    focusView.requestFocus();
                    Toast.makeText(getApplicationContext(), R.string.pasajero_existe_lista, Toast.LENGTH_LONG).show();
                    return;
                }

                //Si se pasan todas estas validaciones, quiere decir que el pasajero cumple con lo exigido y se registra en la lista de pasajeros del itinerario actual
                Pasajero nuevo_pasajero = new Pasajero();
                nuevo_pasajero.setCedula(cedula);
                nuevo_pasajero.setNombres(nombres);
                nuevo_pasajero.setApellidos(apellidos);

                //Guardado del pasajero
                itinerario.ListaPasajeros.add(nuevo_pasajero);

                //Inicializacion de los campos
                barcodeValue.setText("");
                tvNombresPasajero.setText("");
                tvApellidosPasajero.setText("");
                //Foco de nuevo en el campo de cedula
                focusView = barcodeValue;
                focusView.requestFocus();

                //Impresion del total de pasajeros cargados
                tvTotalPasajeros.setText(String.format(getString(R.string.total_pasajeros), String.valueOf(itinerario.ListaPasajeros.size())));
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.pasajero_cargado),
                        nuevo_pasajero.getNombres() + " " + nuevo_pasajero.getApellidos()), Toast.LENGTH_LONG).show();
            }
        });

        //Pausar carga
        Button bPausarLista = (Button) findViewById(R.id.pausar_lista);
        bPausarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();

                //Listado de rutas disponibles
                Type typeListaRutas = new TypeToken<List<Ruta>>() {}.getType();
                String jsonListaRutas = gson.toJson(ListaRutas, typeListaRutas);

                //Listado de horarios disponibles por ruta
                Type typeListaHorarios = new TypeToken<List<Horario>>() {}.getType();
                String jsonListaHorarios = gson.toJson(ListaHorarios, typeListaHorarios);

                //Listado de pasajeros registrados hasta el momento en el sistema
                Type typeListaPasajeros = new TypeToken<List<Pasajero>>() {}.getType();
                String jsonListaPasajeros = gson.toJson(ListaPasajeros, typeListaPasajeros);

                itinerario.setListaCerrada(1);
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
                myMenu.putExtra("fecha_servidor", fecha_servidor);
                startActivity(myMenu);
            }
        });

        //Cerrar lista
        Button bCerrarLista = (Button) findViewById(R.id.cerrar_lista);
        bCerrarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setMessage(R.string.cerrar_lista_mensaje)
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                guardarLista();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("ADVERTENCIA");
                alert.show();
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == RC_BARCODE_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        //Obtencion del valor leido con la camara desde la otra actividad (Pantalla BarcodeCapture)
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        Toast.makeText(getApplicationContext(), R.string.barcode_success, Toast.LENGTH_LONG).show();
                        barcodeValue.setText(barcode.displayValue);

                        //Busqueda de la cedula en el maestro de Pasajeros.
                        //PARA LA FASE 2, SE DEBE CREAR UNA CLASE QUE SE CONECTE CON REGISTRO CIVIL Y CONSULTE LOS DATOS DEL USUARIO
                        for(Pasajero pasajero: ListaPasajeros){
                            if (pasajero.getCedula().equals(barcode.displayValue)) {
                                tvNombresPasajero.setText(pasajero.getNombres());
                                tvApellidosPasajero.setText(pasajero.getApellidos());
                            }
                        }
                        //Log.d(TAG, "Codigo de Barra Nro: " + barcode.displayValue);
                    } else {
                        tvNombresPasajero.setText("");
                        tvApellidosPasajero.setText("");
                        Toast.makeText(getApplicationContext(), R.string.barcode_failure, Toast.LENGTH_LONG).show();
                        //Log.d(TAG, "No barcode captured, intent data is null");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.barcode_error),
                            CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_LONG).show();
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());
        }

    }

    private boolean isTextoValido(String texto) {

        return (texto.length() > 2) && (!texto.matches(".*\\d+.*"));
    }

    private boolean isCedulaValida(String texto) {

        return (texto.length() < 11) && (texto.matches(".*\\d+.*"));
    }

    private boolean isPasajeroRegistradoItinerario(String cedula) {

        boolean encontrado = false;

        //Realizamos una copia de la lista original de Pasajeros en el itinerario, pero haciendo un cast a ArrayList
        ArrayList<Pasajero> pasajeros_registrados = new ArrayList<Pasajero>(itinerario.ListaPasajeros);

        try {

            for (Pasajero p : pasajeros_registrados) {
                if (p.getCedula().equals(cedula)) {
                    encontrado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encontrado;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void guardarLista() {

        // Muestra el Spinner de progreso, y envia al background la tarea para realizar consulta de autenticacion con el servidor web
        showProgress(true);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = getString(R.string.servidor).concat(getString(R.string.guardar_itinerario));
        Map<String, String> parametros = new HashMap();
        parametros.put("usuario", (new Gson()).toJson(usuario));
        parametros.put("itinerario", (new Gson()).toJson(itinerario));

        JSONObject parameters = new JSONObject(parametros);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.isNull("error")) {

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

                        //Inicializamos nuevamente el objeto Itinerario
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

                        Toaster.makeLongToast(getApplicationContext(), getString(R.string.mensaje_lista_enviada), 4000);

                        //Iniciar MENU
                        startActivity(myMenu);

                    } else {
                        Toaster.makeLongToast(getApplicationContext(), response.getString("error"), 5000);
                        //Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}