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
        //cargar_datos();

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
        listaRecetas.remove(posicion);
        cancelAlarm(posicion);
    }

    public void edit_receta(Receta receta, int posicion) {
        listaRecetas.set(posicion, receta);
    }

    public void guardar_datos() {
        int nrecetas = listaRecetas.size();
        int nmedicinas;
        String ndias;
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME_1, MODE_PRIVATE);
            fos.write(String.valueOf(nrecetas).getBytes());
            fos.write("\n".getBytes());

            for(int i = 0; i < nrecetas; ++i) {
                Receta r = listaRecetas.get(i);
                fos.write(r.getNombre().getBytes());
                fos.write("\n".getBytes());
                fos.write(getFecha(r).getBytes());
                fos.write("\n".getBytes());
                nmedicinas = r.getArray_receta().size();
                fos.write(String.valueOf(nmedicinas).getBytes());
                fos.write("\n".getBytes());
                insertarMedicamentos(fos, r);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getFecha(Receta receta) {
        String fecha = "";
        int ndias = 0;
        ArrayList<String> dias = receta.getSemana();
        //String[] dias = receta.getSemana();
        for(int i = 0; i < dias.size(); i++) {
            ndias++;
            fecha += dias.get(i);
            fecha += " ";
            /*if(dias[i] != null) {
                ndias++;
                fecha += dias[i];
                fecha += " ";
            }*/
        }

        fecha = ndias + " " + fecha + " " + receta.getHora() + ":" + receta.getMinuto();

        return fecha;
    }

    public void insertarMedicamentos(FileOutputStream fos, Receta receta) {
        int nmedicinas = receta.getArray_receta().size();
        Medicina medicina;
        for(int i = 0; i < nmedicinas; ++i) {
            try {
                medicina = receta.getMedicina(i);
                fos.write(String.valueOf(medicina.getCantidad()).getBytes());
                fos.write(" ".getBytes());
                fos.write(medicina.getNombre().getBytes());
                fos.write("\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarAlarmas() {
       for(Receta receta: listaRecetas) {
           programarAlarmas(listaRecetas.indexOf(receta));
        }
    }

    public void cargar_datos() {
        FileInputStream fis = null;
        Scanner scanner = null;
        File file = getApplicationContext().getFileStreamPath(FILE_NAME_1);

        if (file.exists()) {

            try {
                fis = openFileInput(FILE_NAME_1);

                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String texto = "";
                Receta receta;

                scanner = new Scanner(fis);
                texto = scanner.nextLine();
                int nrecetas = Integer.parseInt(texto);

                if (nrecetas > 0) {
                    for (int i = 0; i < nrecetas; ++i) {
                        //Leo nombre receta
                        texto = scanner.nextLine();
                        receta = new Receta(texto);

                        //Agrego a la receta datos de fecha y hora
                        extraeFecha(scanner, receta);

                        //Obtengo numero medicamentos
                        texto = scanner.next();
                        int nmedicamentos = Integer.parseInt(texto);
                        for (int j = 0; j < nmedicamentos; ++j) {
                            receta.agregar_medicina(extraeMedicina(scanner));
                        }

                        listaRecetas.add(receta);
                    }
                }


            /*texto = br.readLine();
            while((texto = br.readLine())!= null) {

            }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                scanner.close();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void extraeFecha(Scanner scanner, Receta receta) {

        //Numero dias de la semana
        String texto = scanner.next();
        int ndias = Integer.parseInt(texto);
        //String[] dias_semana = new String[7];
        ArrayList<String> dias_semana = new ArrayList<>();

        for(int i = 0; i < ndias; ++i) {
            texto = scanner.next();
            switch(texto) {
                case "Lunes": dias_semana.add(texto); break;
                case "Martes": dias_semana.add(texto); break;
                case "Miercoles": dias_semana.add(texto); break;
                case "Jueves": dias_semana.add(texto); break;
                case "Viernes": dias_semana.add(texto); break;
                case "Sabado": dias_semana.add(texto); break;
                case "Domingo": dias_semana.add(texto); break;
            }
        }

        texto = scanner.next();
        String[] partes = texto.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        receta.setSemana(dias_semana);
        receta.setHora(hora);
        receta.setMinuto(minuto);
    }

    public Medicina extraeMedicina(Scanner scanner) {

        String texto = scanner.next();
        double cantidad = Double.parseDouble(texto);
        //Nombre medicamento
        texto = scanner.nextLine();

        return new Medicina(texto, cantidad);
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
                Toast.makeText(getApplicationContext(),"Farmacias",Toast.LENGTH_SHORT).show();
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
                case "Lunes": setAlarm(posicion,Calendar.MONDAY); break;
                case "Martes": setAlarm(posicion,Calendar.TUESDAY); break;
                case "Miercoles": setAlarm(posicion,Calendar.WEDNESDAY); break;
                case "Jueves": setAlarm(posicion,Calendar.THURSDAY); break;
                case "Viernes": setAlarm(posicion,Calendar.FRIDAY); break;
                case "Sabado": setAlarm(posicion,Calendar.SATURDAY); break;
                case "Domingo": setAlarm(posicion,Calendar.SUNDAY); break;
            }
        }
    }

    private void setAlarm(int posicion, int dia_semana) {
        /*Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, dia_semana);
        c.set(Calendar.HOUR_OF_DAY, listaRecetas.get(posicion).getHora());
        c.set(Calendar.MINUTE, listaRecetas.get(posicion).getMinuto());
        c.set(Calendar.SECOND, 0);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 7);
        }*/

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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, posicion, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }


    private void cancelAlarm(int posicion) {
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, posicion, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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











