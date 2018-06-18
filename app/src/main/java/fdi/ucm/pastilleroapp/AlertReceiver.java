package fdi.ucm.pastilleroapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class AlertReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        ArrayList<String> medicinas = extras.getStringArrayList("medicinas");
        int id_notificacion = extras.getInt("posicion");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notification = notificationHelper.getChannelNotification(medicinas, id_notificacion);
        notificationHelper.getManager().notify(id_notificacion, notification.build());

        //Asi agrego varias notificaciones
        //notificationManager.notify(Unique_Integer_Number, notification);
    }
}
