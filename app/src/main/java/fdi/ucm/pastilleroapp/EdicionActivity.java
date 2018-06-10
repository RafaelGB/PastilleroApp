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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class EdicionActivity extends AppCompatActivity {

    private static final String TAG = "EdicionActivity";

    private TableLayout tableLayout;
    private String[] dias_semana;
    private Receta receta;
    private boolean receta_finalizada;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_tareas);
        Log.d(TAG, "onCreate: Starting");

        tableLayout = (TableLayout) findViewById(R.id.tableEdicion);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);

        dias_semana= new String[7];
        Intent intent = getIntent();
        String intencion = intent.getStringExtra("Intencion");
        if(intencion.equals("Editar")) {
            receta = intent.getParcelableExtra("Receta Editar");
            cargarDatos(receta);
        }

        getSupportActionBar().setTitle("Crear Tarea");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void cargarDatos(Receta receta) {
        ArrayList<Medicina> lista_medicinas = receta.getArray_receta();
        String[] dias_semana = receta.getSemana();
        String nombre = receta.getNombre();
        Medicina medicina;
        View view;
        int hora = receta.getHora();
        int minuto = receta.getMinuto();

        EditText editText_cantidad;
        EditText editText_nombre;
        EditText editTextReceta = (EditText) findViewById(R.id.editTextNombre);
        editTextReceta.setText(nombre, TextView.BufferType.EDITABLE);

        medicina = lista_medicinas.get(0);

        editText_cantidad = (EditText) findViewById(R.id.editTextCant);
        editText_cantidad.setText(String.valueOf(medicina.getCantidad()), TextView.BufferType.EDITABLE);

        editText_nombre = (EditText) findViewById(R.id.editTextMed);
        editText_nombre.setText(medicina.getNombre(), TextView.BufferType.EDITABLE);

		//Primero agrego las vistas y luego escribo en ellas
        int cuenta = lista_medicinas.size();
        for(int i = 1; i < cuenta; ++i) {

            View vista_edicion = LayoutInflater.from(this).inflate(R.layout.fila_medicamento, null);
            tableLayout.addView(vista_edicion);

            view = tableLayout.getChildAt(i+1);
            medicina = lista_medicinas.get(i);
            editText_cantidad = (EditText)view.findViewById(R.id.editTextCant);
            editText_cantidad.setText(String.valueOf(medicina.getCantidad()), TextView.BufferType.EDITABLE);

            editText_nombre = (EditText)view.findViewById(R.id.editTextMed);
            editText_nombre.setText(medicina.getNombre(), TextView.BufferType.EDITABLE);
        }



        timePicker.setCurrentHour(receta.getHora());
        timePicker.setCurrentMinute(receta.getMinuto());

        for(int dia = 0; dia < dias_semana.length; dia++) {
            String d = dias_semana[dia];
            if(d != null)
                cargarDatosBoxes(d);
        }
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

                //dias semana comprobar
                if(!checkDiasSemana() || !receta_finalizada) {
                    Toast.makeText(getApplicationContext(),"Falta Informacion",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent_edit = getIntent();
                    String intencion = intent_edit.getStringExtra("Intencion");
                    int posicion = intent_edit.getIntExtra("Posicion", 0);
                    Intent intent = new Intent();

                    if(intencion.equals("Editar")) {
                        intent.putExtra("Receta", receta);
                        intent.putExtra("Posicion", posicion);
                        setResult(RESULT_OK, intent);
                    }
                    else {
                        intent.putExtra("Receta", receta);
                        setResult(RESULT_OK, intent);
                    }

                    this.finish();
                    return true;
                }

            }
            default: return true;
        }

    }

    public void getInformacionInterfaz() {

        View view;
        EditText editText_cantidad;
        EditText editText_nombre;
        int hora, minuto;
        String nombre_medicamento;
        double cantidad;

        receta_finalizada = false;

        ArrayList<Medicina> listaMedicinas = new ArrayList<>();
        //Leo toda la info y la guardo en listaMedicinas
        EditText editTextReceta = (EditText) findViewById(R.id.editTextNombre);

        if(editTextReceta.getText().toString().trim().length() == 0) return;
        String nombre_receta = editTextReceta.getText().toString();

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker1);

        int cuenta = tableLayout.getChildCount();
        for(int i = 1; i < cuenta; ++i) {
            view = tableLayout.getChildAt(i);
            editText_cantidad = (EditText)view.findViewById(R.id.editTextCant);

            if(editText_cantidad.getText().toString().trim().length() == 0) return;
            cantidad = Double.parseDouble(editText_cantidad.getText().toString());

            editText_nombre = (EditText)view.findViewById(R.id.editTextMed);

            if (editText_nombre.getText().toString().trim().length() == 0) return;
            nombre_medicamento = editText_nombre.getText().toString();

            listaMedicinas.add(new Medicina(nombre_medicamento, cantidad));
        }

        hora = timePicker.getCurrentHour();
        minuto = timePicker.getCurrentMinute();

        onCheckboxClicked();
        receta_finalizada = true;
        receta = new Receta(nombre_receta, listaMedicinas, dias_semana, hora, minuto);
    }


    private boolean checkDiasSemana() {
        boolean valido = false;
        for(int i = 0; i < 7; ++i) {
            if(dias_semana[i] != null) {
                valido = true;
                break;
            }
        }
        return valido;
    }


    public void cargarDatosBoxes(String dia) {
        CheckBox checkBox;
        switch(dia) {
            case "Lunes": checkBox = findViewById(R.id.checkBLunes);
                checkBox.setChecked(true);
                break;
            case "Martes": checkBox = findViewById(R.id.checkBMartes);
                checkBox.setChecked(true);
                break;
            case "Miercoles": checkBox = findViewById(R.id.checkBMiercoles);
                checkBox.setChecked(true);
                break;
            case "Jueves": checkBox = findViewById(R.id.checkBJueves);
                checkBox.setChecked(true);
                break;
            case "Viernes": checkBox = findViewById(R.id.checkBViernes);
                checkBox.setChecked(true);
                break;
            case "Sabado": checkBox = findViewById(R.id.checkBSabado);
                checkBox.setChecked(true);
                break;
            case "Domingo": checkBox = findViewById(R.id.checkBDomingo);
                checkBox.setChecked(true);
                break;
        }
    }


    public void onCheckboxClicked() {
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
}
