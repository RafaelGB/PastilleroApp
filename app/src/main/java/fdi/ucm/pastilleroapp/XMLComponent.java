package fdi.ucm.pastilleroapp;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

public class XMLComponent {

    private static final String FILE_NAME = "datos.xml";

    public void writeToXmlFile(ArrayList<Receta> listaRecetas) {

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            serializer.startTag("","lista recetas");
            serializer.attribute("","numero", String.valueOf(listaRecetas.size()));
            for(Receta receta: listaRecetas) {
                ArrayList<Medicina> listaMedicinas = receta.getArray_receta();
                ArrayList<String> semana = receta.getSemana();

                serializer.startTag("","receta");
                serializer.attribute("","nombre",receta.getNombre());
                serializer.startTag("","lista medicamentos");
                serializer.attribute("","nMedicamentos", String.valueOf(listaMedicinas.size()));
                for(Medicina medicina: listaMedicinas) {
                    serializer.startTag("","medicina");
                    serializer.attribute("","nombre",medicina.getNombre());
                    serializer.attribute("","cantidad",String.valueOf(medicina.getCantidad()));
                    serializer.endTag("","medicina");
                }
                serializer.endTag("","lista medicamentos");

                serializer.startTag("","fechas");
                serializer.attribute("","hora",String.valueOf(receta.getHora()));
                serializer.attribute("","minuto",String.valueOf(receta.getMinuto()));
                serializer.attribute("","numero dias",String.valueOf(semana.size()));
                //Almaceno los dias de la semana
                for(String dia: semana) {
                    serializer.startTag("","dia");
                    serializer.attribute("","nombre",dia);
                    serializer.endTag("","dia");
                }
                serializer.endTag("","fechas");
                serializer.endTag("","receta");
            }
            serializer.endTag("","lista recetas");
            serializer.endDocument();
            String result = writer.toString();
            writeToFile(result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void writeToFile(String str) {
        //FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
    }

}
