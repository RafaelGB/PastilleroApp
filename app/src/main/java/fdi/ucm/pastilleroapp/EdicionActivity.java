package fdi.ucm.pastilleroapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.PopupMenu;

import java.util.ArrayList;


public class EdicionActivity extends AppCompatActivity {

    private static final String TAG = "EdicionActivity";

    private TableLayout tableLayout;
    private  ArrayList<Medicina> listaMedicinas;
    private Receta receta;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_tareas);
        Log.d(TAG, "onCreate: Starting");

        tableLayout = (TableLayout) findViewById(R.id.tableEdicion);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);

        getSupportActionBar().setTitle("Editar");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_accion, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: this.finish();

            case R.id.agregador: {
                View vista_edicion = LayoutInflater.from(this).inflate(R.layout.fila_medicamento, null);
                tableLayout.addView(vista_edicion);
                return true;
            }

            case R.id.guardar: {
                getInformacionInterfaz();
                return true;
            }
            default: return true;
        }

    }

    public void getInformacionInterfaz() {

        View view;
        EditText editText_cantidad;
        EditText editText_nombre;
        int cantidad;
        String nombre_medicamento;

        listaMedicinas = new ArrayList<>();
        //Leo toda la info y la guardo en listaMedicinas
        EditText editTextReceta = (EditText) findViewById(R.id.editTextNombre);
        String nombre_receta = editTextReceta.getText().toString();

        int cuenta = tableLayout.getChildCount();
        for(int i = 1; i < cuenta; ++i) {
            view = tableLayout.getChildAt(i);
            editText_cantidad = (EditText)view.findViewById(R.id.editTextCant);
            cantidad =  Integer.parseInt(editText_cantidad.getText().toString());

            editText_nombre = (EditText)view.findViewById(R.id.editTextMed);
            nombre_medicamento = editText_nombre.getText().toString();

            listaMedicinas.add(new Medicina(nombre_medicamento, cantidad));
        }

        receta = new Receta(nombre_receta, listaMedicinas);
    }

    public ArrayList<Medicina> getListaMedicinas() {
        return listaMedicinas;
    }

    public Receta getReceta() {
        return receta;
    }
}
