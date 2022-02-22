package com.ark.bookedapps.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.HomeApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class ServiceNotification extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String receiver = remoteMessage.getData().get("receiver");
        String uidReceiver = remoteMessage.getData().get("uid_receiver");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        if (receiver.equals("Customer")){
            if (user.getUid().equals(uidReceiver)){
                showNotification(remoteMessage);
            }
        }else{
            reference.child("users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                    modelUser.setKey(task.getResult().getKey());
                    if (modelUser.getRole().equals("Admin")){
                        showNotification(remoteMessage);
                    }
                }
            });
        }


    }

    private void showNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");


        final String CHANNEL_ID = "NOTIFICATION";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification new order",
                    NotificationManager.IMPORTANCE_HIGH
            );

            Intent intent = new Intent(this, HomeApp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // pending intent
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notifBuilder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo_app)
                    .setContentIntent(notifyPendingIntent);

            NotificationManagerCompat.from(this).notify(1, notifBuilder.build());
        }
    }


}
