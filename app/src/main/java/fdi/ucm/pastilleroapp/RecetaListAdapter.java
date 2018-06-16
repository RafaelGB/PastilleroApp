package fdi.ucm.pastilleroapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
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
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        String nombre = getItem(position).getNombre();
        ArrayList<Medicina> lista_medicina = getItem(position).getArray_receta();
        Receta receta = getItem(position);
        ArrayList<String> semana = receta.getSemana();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvnombre = (TextView) convertView.findViewById(R.id.lista_text1);
        TextView tvdias = (TextView) convertView.findViewById(R.id.lista_text2);
        TextView tvhora = (TextView) convertView.findViewById(R.id.lista_text3);

        tvnombre.setText(nombre);

        String dias = "";
        for(int i = 0; i < semana.size(); ++i) {
            switch (semana.get(i)) {
                case "Lunes": dias += "L "; break;
                case "Martes": dias += "M "; break;
                case "Miercoles": dias += "X "; break;
                case "Jueves": dias += "J "; break;
                case "Viernes": dias += "V "; break;
                case "Sabado": dias += "S "; break;
                case "Domingo": dias += "D "; break;
            }
        }

        tvdias.setText(dias);

        String minuto;
        if(receta.getMinuto() < 10) minuto = "0" + String.valueOf(receta.getMinuto());
        else minuto = String.valueOf(receta.getMinuto());

        String horas = "" + String.valueOf(receta.getHora()) + ":" + minuto;
        tvhora.setText(horas);


        ImageButton img_button = (ImageButton) convertView.findViewById(R.id.imageButton);
        img_button.setClickable(true);
        img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //performItemClick method calls the OnItemClickListener if it is defined.
                ((ListView)parent).performItemClick(v,position,0);
            }
        });

        return convertView;
    }
}
