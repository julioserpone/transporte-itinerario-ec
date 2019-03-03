package com.barragan.transportesec.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.barragan.transportesec.models.Ruta;

import java.lang.reflect.Array;
import java.util.List;

public class SpinRutaAdapter extends ArrayAdapter<Ruta> {

    private Context context;
    private List values;
    private List ListaRutas;

    public SpinRutaAdapter(Context context, int textViewResourceId,
                           List values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.size();
    }
/*
    @Override
    public Object getItem(int position){
        return values.get(position);
    }
*/
    @Override
    public long getItemId(int position){
        return position;
    }
}
