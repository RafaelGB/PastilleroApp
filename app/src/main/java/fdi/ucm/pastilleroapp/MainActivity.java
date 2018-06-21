package fdi.ucm.pastilleroapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FILE_NAME_1 = "copia.txt";
    private static int nAlarmas;

    private ListView listView;

    private ArrayList<Receta> listaRecetas;
    private RecetaListAdapter adapter;
    private FileComponent fileComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.d(TAG, "onCreate: Starting");

        fileComponent = new FileComponent();
        listaRecetas = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listaTareas);

        //Cargo datos almacenados en un xml privado y los cargo en listaRecetas.
        listaRecetas = fileComponent.readXmlPullParser(getApplicationContext());
        cargarAlarmas();

        adapter = new RecetaListAdapter(this, R.layout.adapter_view_layout, listaRecetas);
        listView.setAdapter(adapter);
        //listView.setSaveEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                long viewId = v.getId();
                int pos = parent.getPositionForView(v);

                if (viewId == R.id.imageButton) {

                    PopupMenu popup =new PopupMenu(MainActivity.this, v);
                    popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editar:
                                    Intent intent = new Intent(MainActivity.this, EdicionActivity.class);
                                    intent.putExtra("Intencion", "Editar");
                                    intent.putExtra("Posicion", position);
                                    intent.putExtra("Receta Editar", listaRecetas.get(position));
                                    startActivityForResult(intent,666);
                                    break;
                                case R.id.eliminar:
                                    eliminar_receta(position);
                                    break;
                            }

                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                }

            }
        });

    }

    public void add_receta(Receta receta) {
        listaRecetas.add(receta);
    }

    public void eliminar_receta(int posicion) {
        ArrayList<Integer> idAlarmas = listaRecetas.get(posicion).getIdAlarmas();
        for(int id: idAlarmas) {
            cancelAlarm(id);
        }
        listaRecetas.remove(posicion);
    }

    public void edit_receta(Receta receta, int posicion) {
        listaRecetas.set(posicion, receta);
    }

    private void cargarAlarmas() {
        nAlarmas = 0;

       for(Receta receta: listaRecetas) {
           ArrayList<Integer> idAlarmas = receta.getIdAlarmas();
           for(int id: idAlarmas) {
               cancelAlarm(id);
           }

           receta.deleteAlarms();
           programarAlarmas(listaRecetas.indexOf(receta));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tareas, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.agregador: {
                Log.d(TAG, "onOptionsItemSelected: ClickBtnEdit");
                Intent intent = new Intent(MainActivity.this, EdicionActivity.class);
                intent.putExtra("Intencion", "Crear");
                startActivityForResult(intent,999);
                return true;
            }
            case R.id.farmacias: {
                Log.d(TAG, "onOptionsItemSelected: ClickFarmacias");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                return true;
            }
            default: return true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode == RESULT_OK) {
            Receta receta = data.getParcelableExtra("Receta");
            add_receta(receta);

            programarAlarmas(listaRecetas.indexOf(receta));
        }

        else if(requestCode == 666 && resultCode == RESULT_OK) {
            Receta receta = data.getParcelableExtra("Receta");
            int posicion = data.getIntExtra("Posicion",0);
            edit_receta(receta, posicion);

            programarAlarmas(posicion);
        }

        adapter.notifyDataSetChanged();
    }

    private void programarAlarmas(int posicion) {
        ArrayList<String> semana = listaRecetas.get(posicion).getSemana();
        for (String dia: semana) {
            switch (dia) {
                case "Lunes": setAlarm(posicion,Calendar.MONDAY, nAlarmas); break;
                case "Martes": setAlarm(posicion,Calendar.TUESDAY, nAlarmas); break;
                case "Miercoles": setAlarm(posicion,Calendar.WEDNESDAY, nAlarmas); break;
                case "Jueves": setAlarm(posicion,Calendar.THURSDAY, nAlarmas); break;
                case "Viernes": setAlarm(posicion,Calendar.FRIDAY, nAlarmas); break;
                case "Sabado": setAlarm(posicion,Calendar.SATURDAY, nAlarmas); break;
                case "Domingo": setAlarm(posicion,Calendar.SUNDAY, nAlarmas); break;
            }
            listaRecetas.get(posicion).setIdAlarmas(nAlarmas);
            nAlarmas++;
        }
    }

    private void setAlarm(int posicion, int dia_semana, int idAlarma) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, dia_semana);
        c.set(Calendar.HOUR_OF_DAY, listaRecetas.get(posicion).getHora());
        c.set(Calendar.MINUTE, listaRecetas.get(posicion).getMinuto());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if(c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 7);
        }

        ArrayList<String> nombres = getNames(listaRecetas.get(posicion));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        Bundle extra = new Bundle();
        extra.putStringArrayList("medicinas", nombres);
        extra.putInt("posicion", posicion);

        intent.putExtras(extra);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idAlarma, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
    }


    private void cancelAlarm(int idAlarma) {
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idAlarma, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    private ArrayList<String> getNames(Receta receta) {
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<Medicina> medicinas = receta.getArray_receta();

        for (Medicina m: medicinas) {
            nombres.add(getName(m));
        }

        return nombres;
    }

    private String getName(Medicina medicina) {
        String receta = "";
        receta += String.valueOf(medicina.getCantidad()) + " - " + medicina.getNombre();
        return receta;
    }

    @Override
    protected void onStop() {
        super.onStop();
        fileComponent.getRecetas(listaRecetas);
        fileComponent.writeToTxtFile(getApplicationContext());
        fileComponent.writeToXmlFile(getApplicationContext());
    }

}











