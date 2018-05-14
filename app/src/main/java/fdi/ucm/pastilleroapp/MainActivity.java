package fdi.ucm.pastilleroapp;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView listView;
    private FloatingActionButton botonFlotante;

    private ArrayList<Medicina> listaMedicinas;
    private ArrayList<Receta> listaRecetas;
    private RecetaListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.d(TAG, "onCreate: Starting");

        listaMedicinas = new ArrayList<>();
        listaMedicinas.add(new Medicina("Paracetamol",500));
        listaMedicinas.add(new Medicina("Ibuprofeno",200));
        listaMedicinas.add(new Medicina("Ilvico",300));
        listaMedicinas.add(new Medicina("Citilsteina",100));
        listaMedicinas.add(new Medicina("Paranina",1000));
        listaMedicinas.add(new Medicina("Dalsi",700));

        listaRecetas = new ArrayList<>();
        listaRecetas.add(new Receta("Receta 1",listaMedicinas));
        listaRecetas.add(new Receta("Receta 2",listaMedicinas));
        listaRecetas.add(new Receta("Receta 3",listaMedicinas));
        listaRecetas.add(new Receta("Receta 4",listaMedicinas));

        listView = (ListView) findViewById(R.id.listaTareas);

        adapter = new RecetaListAdapter(this, R.layout.adapter_view_layout, listaRecetas);
        listView.setAdapter(adapter);
        //listView.setSaveEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                long viewId = v.getId();
                int pos = parent.getPositionForView(v);

                if (viewId == R.id.imageButton) {
                    Toast.makeText(getApplicationContext(),"Button " + pos,Toast.LENGTH_SHORT).show();

                    PopupMenu popup =new PopupMenu(MainActivity.this, v);
                    popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editar:
                                    Toast.makeText(getApplicationContext(),"Editar " + position,Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.eliminar:
                                    eliminar_receta(position);
                                    Toast.makeText(getApplicationContext(),"Eliminar " + position,Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"ListView " + pos,Toast.LENGTH_SHORT).show();
                }
                /*int pos = parent.getPositionForView(v);
                Toast.makeText(getApplicationContext(),pos+" ",Toast.LENGTH_SHORT).show();

                PopupMenu popup =new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                popup.show();*/
            }
        });

       /* botonFlotante = (FloatingActionButton) findViewById(R.id.boton_agregarTarea);
        botonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
            }
        });*/

    }


    public void add_receta(Receta receta) {
        listaRecetas.add(receta);
    }

    public void eliminar_receta(int posicion) {
        listaRecetas.remove(posicion);
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
                startActivityForResult(intent,999);
                return true;
            }
            case R.id.farmacias: {
                Log.d(TAG, "onOptionsItemSelected: ClickFarmacias");
                Toast.makeText(getApplicationContext(),"Farmacias",Toast.LENGTH_SHORT).show();
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
            adapter.notifyDataSetChanged();
        }
    }


}











