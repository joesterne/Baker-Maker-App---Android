package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class BreadNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "crustcraft_baking_alerts"
        const val CHANNEL_NAME = "Bread Maker Baking & Ingredient Alerts"
        const val NOTIFICATION_ID_INGREDIENT = 1001
        const val NOTIFICATION_ID_STAGE = 1002
        const val NOTIFICATION_ID_COMPLETE = 1003
        const val NOTIFICATION_ID_STEP_FINISHED = 1004
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Critical countdown timer alerts for sourdough proofing, paddle removal, and baking completion."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300, 150, 500)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun canSendNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun sendStepCompleteNotification(
        completedStageName: String,
        nextStageName: String?,
        guidance: String,
        isProofingStep: Boolean
    ) {
        if (!canSendNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_STEP_FINISHED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val title = if (isProofingStep) "⏳ Proofing Complete: $completedStageName" else "⏰ Step Complete: $completedStageName"
        val nextText = if (nextStageName != null) "Next: $nextStageName\n$guidance" else guidance

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(guidance)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$title\n\n$nextText")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 350, 150, 350, 150, 450))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STEP_FINISHED, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun sendIngredientAlert(
        title: String,
        message: String,
        stageName: String,
        itemsToAdd: List<String>
    ) {
        if (!canSendNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_INGREDIENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val itemsSummary = if (itemsToAdd.isNotEmpty()) {
            "Items: ${itemsToAdd.joinToString(", ")}"
        } else message

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🍞 $title")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n$itemsSummary\nStage: $stageName")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 350, 150, 350, 150, 500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_INGREDIENT, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun sendBakeCompleteNotification(recipeName: String) {
        if (!canSendNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_COMPLETE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✨ Baking Complete: $recipeName")
            .setContentText("Your golden loaf is ready! Remove bread pan within 15 mins to keep the crust crispy.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_COMPLETE, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
