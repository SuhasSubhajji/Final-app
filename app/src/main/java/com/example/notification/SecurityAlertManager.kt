package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.SecurityAlert
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.receiver.NotificationActionReceiver
import java.util.UUID

object SecurityAlertManager {

    const val CHANNEL_CRITICAL_ID = "channel_threat_alerts_critical"
    const val CHANNEL_ADVISORIES_ID = "channel_threat_alerts_advisory"

    const val EXTRA_THREAT_ID = "extra_threat_id"
    const val EXTRA_ALERT_ID = "extra_alert_id"
    const val EXTRA_SNIPPET = "extra_remediation_snippet"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL_ID,
                context.getString(R.string.channel_threat_alerts_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_threat_alerts_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
            }

            val advisoryChannel = NotificationChannel(
                CHANNEL_ADVISORIES_ID,
                "DevSecOps Advisories",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General security recommendations and updates"
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(criticalChannel)
            notificationManager.createNotificationChannel(advisoryChannel)
        }
    }

    fun sendActionableThreatNotification(
        context: Context,
        threat: ThreatItem,
        alertId: String = UUID.randomUUID().toString()
    ): SecurityAlert {
        initNotificationChannels(context)

        val notificationId = (threat.id.hashCode() and 0x7FFFFFFF)
        val isCritical = threat.severity == ThreatSeverity.CRITICAL

        // 1. Primary Tap Intent -> Opens Threat Detail in MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_THREAT_ID, threat.id)
            putExtra(EXTRA_ALERT_ID, alertId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Action: Review Fix (Opens Detail)
        val reviewIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_THREAT_ID, threat.id)
            putExtra(EXTRA_ALERT_ID, alertId)
        }
        val reviewPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 1,
            reviewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Action: Acknowledge (Dismisses notification & marks resolved)
        val ackIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_ACKNOWLEDGE_ALERT
            putExtra(EXTRA_ALERT_ID, alertId)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val ackPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2,
            ackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Action: Copy Remediation Command
        val remediationCode = threat.aiMitigationCode ?: "# Review fix for ${threat.title}"
        val copyIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COPY_REMEDIATION
            putExtra(EXTRA_SNIPPET, remediationCode)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val copyPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 3,
            copyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isCritical) CHANNEL_CRITICAL_ID else CHANNEL_ADVISORIES_ID
        val prefix = if (isCritical) "🚨 [CRITICAL ALERT]" else "⚠️ [SECURITY ADVISORY]"
        val cveLabel = if (threat.cveIds.isNotEmpty()) " (${threat.cveIds.first()})" else ""

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("$prefix$cveLabel: ${threat.source.displayName}")
            .setContentText(threat.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${threat.title}\n\n${threat.description}\n\nRecommended: Apply vendor patch or run CI/CD remediation.")
            )
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_view,
                context.getString(R.string.action_review_mitigation),
                reviewPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_save,
                "Copy Fix",
                copyPendingIntent
            )
            .addAction(
                android.R.drawable.checkbox_on_background,
                context.getString(R.string.action_acknowledge),
                ackPendingIntent
            )

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission may need to be requested in Android 13+
        }

        return SecurityAlert(
            id = alertId,
            threatItemId = threat.id,
            title = threat.title,
            snippet = threat.description.take(150),
            severity = threat.severity,
            timestampMillis = System.currentTimeMillis(),
            isAcknowledged = false,
            remediationQuickFix = remediationCode
        )
    }
}
