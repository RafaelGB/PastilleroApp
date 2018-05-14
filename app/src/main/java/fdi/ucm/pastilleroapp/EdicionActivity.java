package fdi.ucm.pastilleroapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class EdicionActivity extends AppCompatActivity {

    private static final String TAG = "EdicionActivity";

    private TableLayout tableLayout;
    private  ArrayList<Medicina> listaMedicinas;
    private String[] dias_semana;
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

            case android.R.id.home: this.finish(); return true;

            case R.id.agregador: {
                View vista_edicion = LayoutInflater.from(this).inflate(R.layout.fila_medicamento, null);
                tableLayout.addView(vista_edicion);
                return true;
            }

            case R.id.guardar: {
                getInformacionInterfaz();
              /*  Intent intent = getIntent();
                receta = intent.getParcelableExtra("Receta");*/

               /* Intent intent_receta = new Intent();
                intent_receta.putExtra("Receta", receta);
                setResult(RESULT_OK, intent_receta);*/

                Intent intent = new Intent();
                /*intent.putParcelableArrayListExtra("Medicamentos", receta.getArray_receta());
                intent.putExtra("Hora", receta.getHora());
                intent.putExtra("Minutos", receta.getMinuto());*/
                intent.putExtra("Receta", receta);
                setResult(RESULT_OK, intent);

                this.finish();
                return true;
            }
            default: return true;
        }

    }

    public void getInformacionInterfaz() {

        View view;
        EditText editText_cantidad;
        EditText editText_nombre;
        int cantidad, hora, minuto;
        String nombre_medicamento;

        listaMedicinas = new ArrayList<>();
        //Leo toda la info y la guardo en listaMedicinas
        EditText editTextReceta = (EditText) findViewById(R.id.editTextNombre);
        String nombre_receta = editTextReceta.getText().toString();

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker1);

        int cuenta = tableLayout.getChildCount();
        for(int i = 1; i < cuenta; ++i) {
            view = tableLayout.getChildAt(i);
            editText_cantidad = (EditText)view.findViewById(R.id.editTextCant);
            cantidad =  Integer.parseInt(editText_cantidad.getText().toString());

            editText_nombre = (EditText)view.findViewById(R.id.editTextMed);
            nombre_medicamento = editText_nombre.getText().toString();

            listaMedicinas.add(new Medicina(nombre_medicamento, cantidad));
        }

        hora = timePicker.getCurrentHour();
        minuto = timePicker.getCurrentMinute();

        onCheckboxClicked();
        receta = new Receta(nombre_receta, listaMedicinas, dias_semana, hora, minuto);
    }


    public void onCheckboxClicked() {

        dias_semana= new String[7];
        CheckBox checkBox;

        checkBox = findViewById(R.id.checkBLunes);
        if(checkBox.isChecked()) dias_semana[0] = "Lunes";
        checkBox = findViewById(R.id.checkBMartes);
        if(checkBox.isChecked()) dias_semana[1] = "Martes";
        checkBox = findViewById(R.id.checkBMiercoles);
        if(checkBox.isChecked()) dias_semana[2] = "Miercoles";
        checkBox = findViewById(R.id.checkBJueves);
        if(checkBox.isChecked()) dias_semana[3] = "Jueves";
        checkBox = findViewById(R.id.checkBViernes);
        if(checkBox.isChecked()) dias_semana[4] = "Viernes";
        checkBox = findViewById(R.id.checkBSabado);
        if(checkBox.isChecked()) dias_semana[5] = "Sabado";
        checkBox = findViewById(R.id.checkBDomingo);
        if(checkBox.isChecked()) dias_semana[6] = "Domingo";

    }

    public ArrayList<Medicina> getListaMedicinas() {
        return listaMedicinas;
    }

    public Receta getReceta() {
        return receta;
    }
}
