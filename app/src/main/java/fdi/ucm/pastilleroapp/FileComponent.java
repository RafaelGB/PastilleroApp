package fdi.ucm.pastilleroapp;

import android.content.Context;
import android.content.Intent;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class FileComponent {

    private static final String FILE_NAME = "datos.xml";
    private static final String FILE_NAME_TXT = "copia.txt";
    private ArrayList<Receta> listaRecetas;

    public void getRecetas(ArrayList<Receta> listaRecetas) {
        this.listaRecetas = listaRecetas;
    }

    public ArrayList<Receta> readXmlPullParser(Context context) {
        XmlPullParserFactory factory;
        FileInputStream fis = null;
        File file = context.getFileStreamPath(FILE_NAME);
        ArrayList<Receta> lista_recetas = new ArrayList<>();

        try {
            StringBuilder sb = new StringBuilder();
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            if(file == null || !file.exists()) return lista_recetas;

            fis = context.openFileInput(FILE_NAME);

            ArrayList<Medicina> lista_medicinas = new ArrayList<>();
            ArrayList<String> lista_semana = new ArrayList<>();
            Receta receta = null;
            Medicina medicina;

            xpp.setInput(fis, null);
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
               String tagname = xpp.getName();

               switch (eventType) {
                   case XmlPullParser.START_TAG: {
                       if (tagname.equals("receta")) {
                           receta = new Receta(xpp.getAttributeValue(0));
                       }
                       else if(tagname.equals("lista_medicamentos")) {
                           lista_medicinas = new ArrayList<>();
                       }
                       else if(tagname.equals("medicina")) {
                           double cantidad = Double.parseDouble(xpp.getAttributeValue(1));
                           medicina = new Medicina(xpp.getAttributeValue(0), cantidad);
                           lista_medicinas.add(medicina);
                       }
                       else if(tagname.equals("fechas")) {
                           lista_semana = new ArrayList<>();

                           int hora = Integer.parseInt(xpp.getAttributeValue("","hora"));
                           int minuto = Integer.parseInt(xpp.getAttributeValue("","minuto"));
                           receta.setHora(hora);
                           receta.setMinuto(minuto);
                       }
                       else if(tagname.equals("dia")) {
                           lista_semana.add(xpp.getAttributeValue("","nombre"));
                       }
                   } break;
                   case XmlPullParser.END_TAG: {
                       if (tagname.equals("receta")) {
                           lista_recetas.add(receta);
                       }
                       else if(tagname.equals("lista_medicamentos")) {
                            receta.setArray_receta(lista_medicinas);
                       }
                       else if(tagname.equals("fechas")) {
                           receta.setSemana(lista_semana);
                       }
                   }break;
               }

                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return lista_recetas;
        }

    }




    public void writeToXmlFile(Context context) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            serializer.startTag("","lista_recetas");
            serializer.attribute("","numero", String.valueOf(listaRecetas.size()));
            for(Receta receta: listaRecetas) {
                ArrayList<Medicina> listaMedicinas = receta.getArray_receta();
                ArrayList<String> semana = receta.getSemana();

                serializer.startTag("","receta");
                serializer.attribute("","nombre",receta.getNombre());
                serializer.startTag("","lista_medicamentos");
                serializer.attribute("","nMedicamentos", String.valueOf(listaMedicinas.size()));
                for(Medicina medicina: listaMedicinas) {
                    serializer.startTag("","medicina");
                    serializer.attribute("","nombre",medicina.getNombre());
                    serializer.attribute("","cantidad",String.valueOf(medicina.getCantidad()));
                    serializer.endTag("","medicina");
                }
                serializer.endTag("","lista_medicamentos");

                serializer.startTag("","fechas");
                serializer.attribute("","hora", String.valueOf(receta.getHora()));
                serializer.attribute("","minuto", String.valueOf(receta.getMinuto()));
                serializer.attribute("","numero_dias", String.valueOf(semana.size()));
                //Almaceno los dias de la semana
                for(String dia: semana) {
                    serializer.startTag("","dia");
                    serializer.attribute("","nombre", dia);
                    serializer.endTag("","dia");
                }
                serializer.endTag("","fechas");
                serializer.endTag("","receta");
            }
            serializer.endTag("","lista_recetas");
            serializer.endDocument();
            String result = writer.toString();
            writeToFile(result, FILE_NAME, context);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void writeToTxtFile(Context context) {

        String texto = "";

        int nrecetas = listaRecetas.size();
        texto += String.valueOf(nrecetas);
        texto += "\n";

        for(Receta receta: listaRecetas) {
            ArrayList<Medicina> listaMedicinas = receta.getArray_receta();
            ArrayList<String> semana = receta.getSemana();
            texto += receta.getNombre();
            texto += "\n";
            texto += getFecha(receta);
            texto += "\n";
            texto += String.valueOf(listaMedicinas.size());
            texto += "\n";
            texto += insertarMedicamentos(listaMedicinas);
        }

        writeToFile(texto, FILE_NAME_TXT, context);
    }


    private String getFecha(Receta receta) {
        String fecha = "";
        int ndias = 0;
        ArrayList<String> dias = receta.getSemana();
        for(String dia: dias) {
            ndias++;
            fecha += dia;
            fecha += " ";
        }

        fecha = String.valueOf(ndias) + " " + fecha + " " + String.valueOf(receta.getHora()) + ":" + String.valueOf(receta.getMinuto());

        return fecha;
    }


    private String insertarMedicamentos(ArrayList<Medicina> listaMedicinas) {

        String texto = "";
        for(Medicina medicina: listaMedicinas) {
            texto += String.valueOf(medicina.getCantidad());
            texto += " ";
            texto += medicina.getNombre();
            texto += "\n";
        }
        return texto;
    }


    private static void writeToFile(String str, String filename, Context c) {
        try {
            FileOutputStream fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(str.getBytes(), 0, str.length());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
