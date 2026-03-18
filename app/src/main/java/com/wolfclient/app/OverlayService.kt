package com.wolfclient.app

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var fabView: View? = null

    companion object {
        var isRunning = false
        const val CHANNEL_ID = "wolf_overlay"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(1, buildNotification())
        showFab()
    }

    private fun showFab() {
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
            x = 20; y = 300
        }

        var ix = 0; var iy = 0; var tx = 0f; var ty = 0f; var moved = false

        fabView!!.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { moved=false; ix=params.x; iy=params.y; tx=e.rawX; ty=e.rawY }
                MotionEvent.ACTION_MOVE -> {
                    val dx=(e.rawX-tx).toInt(); val dy=(e.rawY-ty).toInt()
                    if (Math.abs(dx)>5||Math.abs(dy)>5) moved=true
                    params.x=ix+dx; params.y=iy+dy
                    windowManager.updateViewLayout(fabView, params)
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        val i = Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(i)
                    }
                }
            }
            true
        }
        windowManager.addView(fabView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        fabView?.let { windowManager.removeView(it) }
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(CHANNEL_ID, "Wolf Client", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Wolf Client")
        .setContentText("Overlay active — tap W button to open")
        .setSmallIcon(R.drawable.ic_wolf)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}
