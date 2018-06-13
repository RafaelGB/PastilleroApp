package fdi.ucm.pastilleroapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class NotificationHelper extends ContextWrapper {

    public static final String MYCHANNEL_ID = "MyChannelID";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        manager = getSystemService(NotificationManager.class);
        createChannel();
    }

    //Creo canal para notificacion, hay un canal por Receta.
    private void createChannel() {

        //Si la API es igual o superior a 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MYCHANNEL_ID, "Mi Canal", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Este es el canal de PastilleroApp");

            manager.createNotificationChannel(channel);
        }
    }


    public NotificationCompat.Builder getChannelNotification(ArrayList<String> medicamentos) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Lista Medicamentos");
        inboxStyle.setSummaryText("Medicamentos");
        int lista = medicamentos.size();

        for (int i = 0; i < lista; ++i) {
            inboxStyle.addLine(medicamentos.get(i));
        }

        return new NotificationCompat.Builder(getApplicationContext(), MYCHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle("Recordatorio Medicamentos")
                .setContentText("Tienes que tomar las siguientes medicinas")
                .setColor(Color.MAGENTA)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
    }

    public NotificationManager getManager() {
        if(manager == null) {
            manager = getSystemService(NotificationManager.class);
        }
        return manager;
    }
}
