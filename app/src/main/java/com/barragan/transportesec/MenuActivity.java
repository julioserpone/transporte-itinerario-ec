package com.barragan.transportesec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Type;

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

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    public String fecha_servidor;
    public Usuario usuario;
    public Licencia licencia;
    public Cooperativa cooperativa;
    public Bus bus;
    public Horario hora;
    public Ruta ruta;
    public PasajeroBus itinerario;
    public List ListaRutas;
    public List ListaHorarios;
    public List ListaPasajeros;
    private TextView tvNombresChofer;
    private TextView tvRucCooperativa;
    private TextView tvNombreCooperativa;
    private TextView tvMarcaBus;
    private TextView tvNumeroUnidad;
    private AlertDialog.Builder builderAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
        tvRucCooperativa = findViewById(R.id.ruc_cooperativa);
        tvRucCooperativa.setText("RUC: " + cooperativa.getRuc());
        tvNombreCooperativa = findViewById(R.id.nombre_cooperativa);
        tvNombreCooperativa.setText(cooperativa.getNombre());
        tvMarcaBus = findViewById(R.id.marca_bus);
        tvMarcaBus.setText("Marca: " + bus.getMarca() + " " + bus.getModelo());
        tvNumeroUnidad = findViewById(R.id.numero_unidad);
        tvNumeroUnidad.setText("Numero Unidad: " + bus.getNumeroUnidad());

        builderAlertDialog = new AlertDialog.Builder(this);

        Button bContinuarCargando = (Button) findViewById(R.id.continuar_cargando);
        bContinuarCargando.setOnClickListener(new View.OnClickListener() {
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

                Intent menuRutaHora = new Intent(getBaseContext(), RutaActivity.class);

                //Parametrizacion de valores entre pantallas (datos del chofer, itinerario, pasajeros, etc
                menuRutaHora.putExtra("usuario", (new Gson()).toJson(usuario));
                menuRutaHora.putExtra("licencia", (new Gson()).toJson(licencia));
                menuRutaHora.putExtra("cooperativa", (new Gson()).toJson(cooperativa));
                menuRutaHora.putExtra("bus", (new Gson()).toJson(bus));
                menuRutaHora.putExtra("hora", (new Gson()).toJson(hora));
                menuRutaHora.putExtra("ruta", (new Gson()).toJson(ruta));
                menuRutaHora.putExtra("itinerario", (new Gson()).toJson(itinerario));
                menuRutaHora.putExtra("rutas", jsonListaRutas);
                menuRutaHora.putExtra("horarios", jsonListaHorarios);
                menuRutaHora.putExtra("pasajeros", jsonListaPasajeros);
                menuRutaHora.putExtra("fecha_servidor", fecha_servidor);
                startActivity(menuRutaHora);
            }
        });

        Button bCerrarAplicacion = (Button) findViewById(R.id.salir);
        bCerrarAplicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builderAlertDialog.setMessage(R.string.cerrar_app)
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builderAlertDialog.create();
                //Setting the title manually
                alert.setTitle("ADVERTENCIA");
                alert.show();
            }
        });

        if (itinerario.getListaCerrada() == 0) {
            bContinuarCargando.setVisibility(View.INVISIBLE);
            bCargarPasajeros.setVisibility(View.VISIBLE);
        } else {
            bContinuarCargando.setVisibility(View.VISIBLE);
            bCargarPasajeros.setVisibility(View.INVISIBLE);
        }

    }
}
