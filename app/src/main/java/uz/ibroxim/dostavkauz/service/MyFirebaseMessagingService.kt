package uz.techie.mexmash.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import uz.ibroxim.dostavkauz.LoginActivity
import uz.ibroxim.dostavkauz.R
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var notificationManager: NotificationManager? = null

    val CHANNEL_PRIMARY = "channel_fcm_messaging"
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("TAG", "onMessageReceived: $p0")

        p0.let {
            Log.d("TAG", "onMessageReceived: title ${it.notification?.title}")
            Log.d("TAG", "onMessageReceived: message ${it.notification?.body}")
            Log.d("TAG", "onMessageReceived: image ${it.notification?.imageUrl.toString()}")
            Log.d("TAG", "onMessageReceived: clickAction ${it.notification?.clickAction.toString()}")
            Log.d("TAG", "onMessageReceived: tag ${it.notification?.tag.toString()}")



//            val title = it.notification?.title
//            val message = it.notification?.body
//            val imgUrl: String = it.notification?.imageUrl.toString()
//            val clickAction = it.notification?.clickAction.toString()

            val title = it.data["title"]
            val message = it.data["body"]
            val imgUrl = it.data["image"]
            val clickAction = it.data["action"]
            val productId = it.data["product_id"]

            Log.d("TAG", "onMessageReceived: data title "+title)
            Log.d("TAG", "onMessageReceived: data message "+message)
            Log.d("TAG", "onMessageReceived: data imgUrl "+imgUrl)
            Log.d("TAG", "onMessageReceived: data clickAction "+clickAction)
            Log.d("TAG", "onMessageReceived: data productId "+productId)



            createNotification(title, message, imgUrl, clickAction, productId)

        }


    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }




    private fun createNotification(title: String?, message: String?, imgUrl: String?, clickAction:String?, productId:String?) {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_PRIMARY,
                "channel_FCM_Messaging",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "app messaging channel"
            notificationManager!!.createNotificationChannel(channel)
        }

        try {

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("clickAction", clickAction)
            intent.putExtra("productId", productId)
            val pendingIntent = PendingIntent.getActivity(
                this,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


            val randomId = Random.nextInt(2000)
            Log.d("TAG", "createNotification: randomId "+randomId)

            if (imgUrl?.let { getBitmapFromURL(it) } == null) {
                val builder = NotificationCompat.Builder(this, CHANNEL_PRIMARY)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)

                notificationManager?.notify(randomId, builder.build())
            }
            else {
                val bitmap = getBitmapFromURL(imgUrl)

                val builder = NotificationCompat.Builder(this, CHANNEL_PRIMARY)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                    .setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)

                notificationManager?.notify(randomId, builder.build())
            }

        } catch (e: Exception) {
            Log.e("TAG", "createNotification: ", e)
        }


    }


    fun getBitmapFromURL(urlPath: String): Bitmap? {
        try {
            val url = URL(urlPath)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)

            return bitmap
        } catch (e: Exception) {
            return null
        }

    }


}