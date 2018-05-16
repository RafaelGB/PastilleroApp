package fdi.ucm.pastilleroapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.PopupMenu;


public class EdicionActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_tareas);

        getSupportActionBar().setTitle("Editar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (TableLayout) findViewById(R.id.tableEdicion);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_accion, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.agregador) {
            View vista_edicion = LayoutInflater.from(this).inflate(R.layout.fila_medicamento, null);
            tableLayout.addView(vista_edicion);

            return true;
        }
        else return false;
    }

}
