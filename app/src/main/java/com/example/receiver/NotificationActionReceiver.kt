package com.example.receiver

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.data.local.AppDatabase
import com.example.notification.SecurityAlertManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ACKNOWLEDGE_ALERT = "com.example.cybershield.ACTION_ACKNOWLEDGE_ALERT"
        const val ACTION_COPY_REMEDIATION = "com.example.cybershield.ACTION_COPY_REMEDIATION"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }

        when (intent.action) {
            ACTION_ACKNOWLEDGE_ALERT -> {
                val alertId = intent.getStringExtra(SecurityAlertManager.EXTRA_ALERT_ID)
                if (!alertId.isNullOrBlank()) {
                    val db = AppDatabase.getInstance(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.threatDao().acknowledgeAlert(alertId)
                    }
                }
                Toast.makeText(context, "✅ Security alert acknowledged", Toast.LENGTH_SHORT).show()
            }
            ACTION_COPY_REMEDIATION -> {
                val snippet = intent.getStringExtra(SecurityAlertManager.EXTRA_SNIPPET) ?: ""
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Remediation Fix", snippet)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "📋 Remediation command copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
