package fdi.ucm.pastilleroapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.PopupMenu;


import java.util.ArrayList;
import java.util.List;

public class RecetaListAdapter extends ArrayAdapter<Receta> {

    private static final String TAG = "RecetaListAdapter";
    private Context mContext;
    private int mResource;

    public RecetaListAdapter(@NonNull Context context, int resource, @NonNull List<Receta> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String nombre = getItem(position).getNombre();
        ArrayList<Medicina> lista_medicina = getItem(position).getArray_receta();
        Receta receta = new Receta(nombre,lista_medicina);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvnombre = (TextView) convertView.findViewById(R.id.lista_text1);
        TextView tvmedicina1 = (TextView) convertView.findViewById(R.id.lista_text2);
        TextView tvmedicina2 = (TextView) convertView.findViewById(R.id.lista_text3);

        tvnombre.setText(nombre);

        Medicina medicina = lista_medicina.get(0);
        tvmedicina1.setText(medicina.getNombre() + " - " + medicina.getCantidad());

        if(lista_medicina.size() > 1) {
            Medicina medicina2 = lista_medicina.get(1);
            tvmedicina2.setText(medicina2.getNombre() + " - " + medicina2.getCantidad());
        } else tvmedicina2.setText("");
        

        return convertView;
    }
}
