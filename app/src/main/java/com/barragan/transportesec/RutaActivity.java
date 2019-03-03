package com.barragan.transportesec;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.barragan.transportesec.models.PasajeroBus;
import com.barragan.transportesec.models.Usuario;
import com.barragan.transportesec.models.Ruta;
import com.barragan.transportesec.models.Horario;
import com.barragan.transportesec.models.Pasajero;
import com.barragan.transportesec.models.Licencia;
import com.barragan.transportesec.models.Cooperativa;
import com.barragan.transportesec.models.Bus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RutaActivity extends AppCompatActivity {

    public String fecha_servidor;
    public Usuario usuario;
    public Licencia licencia;
    public Cooperativa cooperativa;
    public Bus bus;
    public Horario hora;
    public Ruta ruta;
    public PasajeroBus itinerario;
    public List ListaRutas;
    public ArrayList<Ruta> rutas;
    public JSONArray rutas_activas;
    public JSONArray horarios_activos;
    public List ListaHorarios;
    public List ListaPasajeros;
    private TextView tvNombresChofer;
    private TextView tvRucCooperativa;
    private TextView tvNombreCooperativa;
    private TextView tvMarcaBus;
    private TextView tvNumeroUnidad;
    private Spinner spinnerRuta;
    private Spinner spinnerHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

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
        rutas = new ArrayList<Ruta>(ListaRutas);

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

        //Carga de listas a los spinners
        spinnerRuta=(Spinner)findViewById(R.id.ruta);
        spinnerHora=(Spinner)findViewById(R.id.hora);

        //Validar que el chofer posee rutas activas
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = getString(R.string.servidor).concat(getString(R.string.rutas_chofer));
        Map<String, String> parametros = new HashMap();
        parametros.put("id", Integer.toString(usuario.getId()));
        JSONObject parameters = new JSONObject(parametros);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.isNull("error")) {

                        Gson gson = new Gson();

                        //Listado de rutas disponibles
                        rutas_activas = response.getJSONArray("rutas");
                        ListaRutas = Arrays.asList(gson.fromJson(rutas_activas.toString(), Ruta[].class));
                        rutas = new ArrayList<Ruta>(ListaRutas);

                        //Listado de horarios disponibles por ruta
                        horarios_activos = response.getJSONArray("horarios");
                        ListaHorarios = Arrays.asList(gson.fromJson(horarios_activos.toString(), Horario[].class));

                    } else {
                        Toaster.makeLongToast(getApplicationContext(), response.getString("error"), 6000);
                        //Mostramos el mensaje de que no posee rutas activas y cerramos la sesion
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

        //Cargar rutas disponibles para el chofer
        loadRutaData();

        spinnerRuta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String ruta =  spinnerRuta.getItemAtPosition(spinnerRuta.getSelectedItemPosition()).toString();
                loadHoraData();
                //Toast.makeText(getApplicationContext(), ruta, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        //Definicion del evento para el menu de cargar pasajeros
        Button bCargarPasajeros = (Button) findViewById(R.id.cargar_pasajeros);
        bCargarPasajeros.setOnClickListener(new View.OnClickListener() {
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

                //COMENZAMOS A CREAR EL ITINERARIO
                ruta = (Ruta) spinnerRuta.getSelectedItem();
                hora = (Horario) spinnerHora.getSelectedItem();

                itinerario.setChoferId(usuario.getId());
                itinerario.setListaCerrada(0);
                itinerario.setCooperativaRutaId(hora.getId());
                itinerario.ListaPasajeros = new ArrayList<Pasajero>();

                Intent lectorCodigoBarra = new Intent(getBaseContext(), MainActivity.class);

                //Parametrizacion de valores entre pantallas (datos del chofer, itinerario, pasajeros, etc
                lectorCodigoBarra.putExtra("usuario", (new Gson()).toJson(usuario));
                lectorCodigoBarra.putExtra("licencia", (new Gson()).toJson(licencia));
                lectorCodigoBarra.putExtra("cooperativa", (new Gson()).toJson(cooperativa));
                lectorCodigoBarra.putExtra("bus", (new Gson()).toJson(bus));
                lectorCodigoBarra.putExtra("hora", (new Gson()).toJson(hora));
                lectorCodigoBarra.putExtra("ruta", (new Gson()).toJson(ruta));
                lectorCodigoBarra.putExtra("itinerario", (new Gson()).toJson(itinerario));
                lectorCodigoBarra.putExtra("rutas", jsonListaRutas);
                lectorCodigoBarra.putExtra("horarios", jsonListaHorarios);
                lectorCodigoBarra.putExtra("pasajeros", jsonListaPasajeros);
                lectorCodigoBarra.putExtra("fecha_servidor", fecha_servidor);
                startActivity(lectorCodigoBarra);
            }
        });
    }

    private void loadRutaData() {

        ArrayAdapter<Ruta> adapterRutas = new ArrayAdapter<>(RutaActivity.this, android.R.layout.simple_spinner_dropdown_item, ListaRutas);
        adapterRutas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRuta.setAdapter(adapterRutas);
    }

    private void loadHoraData() {

        //Realizamos una copia de la lista original de Horarios preestablecidos, pero haciendo un cast a ArrayList
        ArrayList<Horario> horarios_disponibles = new ArrayList<Horario>(ListaHorarios);
        //Creamos objeto que recibira los horarios disponibles para esa ruta
        ArrayList<Horario> horario_ruta = new ArrayList<>();
        try {
            Ruta ruta_seleccionada = (Ruta) spinnerRuta.getSelectedItem();
            for (Horario c : horarios_disponibles) {
                if (c.getRutaId() == ruta_seleccionada.getId()) {
                    horario_ruta.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<Horario> adapterHorarios = new ArrayAdapter<>(RutaActivity.this, android.R.layout.simple_spinner_dropdown_item, horario_ruta);
        adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHora.setAdapter(adapterHorarios);
    }
}
