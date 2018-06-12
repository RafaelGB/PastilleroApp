package fdi.ucm.pastilleroapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //String canalID = intent.getStringExtra("canal ID");
        ArrayList<String> medicamentos = intent.getStringArrayListExtra("lista medicinas");
        int id_notificacion = intent.getIntExtra("id notificacion", 1);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notification = notificationHelper.getChannelNotification(medicamentos);
        notificationHelper.getManager().notify(id_notificacion, notification.build());

        //Asi agrego varias notificaciones
        //notificationManager.notify(Unique_Integer_Number, notification);
    }
}
