package com.wolfclient.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var fabView: View? = null
    private var hudView: View? = null
    private var hudVisible = false

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    companion object {
        var isRunning = false
        const val CHANNEL_ID = "wolf_overlay_channel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(1, buildNotification())
        showFloatingButton()
    }

    private fun showFloatingButton() {
        val inflater = LayoutInflater.from(this)
        fabView = inflater.inflate(R.layout.overlay_fab, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 200
        }

        fabView!!.setOnTouchListener(object : View.OnTouchListener {
            private var moved = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        moved = false
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) moved = true
                        params.x = initialX + dx
                        params.y = initialY + dy
                        windowManager.updateViewLayout(fabView, params)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!moved) toggleHud()
                    }
                }
                return true
            }
        })

        windowManager.addView(fabView, params)
    }

    private fun toggleHud() {
        if (hudVisible) {
            hudView?.let { windowManager.removeView(it) }
            hudView = null
            hudVisible = false
        } else {
            showHud()
            hudVisible = true
        }
    }

    private fun showHud() {
        val inflater = LayoutInflater.from(this)
        hudView = inflater.inflate(R.layout.overlay_hud, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 10
            y = 100
        }

        // Close button
        hudView!!.findViewById<ImageView>(R.id.btn_close_hud)?.setOnClickListener {
            toggleHud()
        }

        // Launch MCPE from HUD
        hudView!!.findViewById<LinearLayout>(R.id.btn_launch_mc)?.setOnClickListener {
            launchMinecraft()
        }

        windowManager.addView(hudView, params)
    }

    private fun launchMinecraft() {
        val packages = listOf("com.mojang.minecraftpe", "com.mojang.minecraftpeedu")
        for (pkg in packages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        fabView?.let { windowManager.removeView(it) }
        hudView?.let { windowManager.removeView(it) }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Wolf Client Overlay",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Wolf Client HUD overlay is running" }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Wolf Client")
            .setContentText("Overlay is active")
            .setSmallIcon(R.drawable.ic_wolf)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
