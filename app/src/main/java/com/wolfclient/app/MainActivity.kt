package com.wolfclient.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wolfclient.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {

        // Launch MCPE button
        binding.btnLaunchMcpe.setOnClickListener {
            launchMinecraft()
        }

        // Overlay toggle
        binding.btnToggleOverlay.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val running = OverlayService.isRunning
                if (running) {
                    stopService(Intent(this, OverlayService::class.java))
                    binding.btnToggleOverlay.text = "Enable Overlay"
                    binding.overlayStatus.text = "Overlay: OFF"
                    binding.overlayStatusDot.setBackgroundResource(R.drawable.dot_off)
                } else {
                    startForegroundService(Intent(this, OverlayService::class.java))
                    binding.btnToggleOverlay.text = "Disable Overlay"
                    binding.overlayStatus.text = "Overlay: ON"
                    binding.overlayStatusDot.setBackgroundResource(R.drawable.dot_on)
                }
            } else {
                requestOverlayPermission()
            }
        }

        // Settings button
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // GitHub / Discord placeholder
        binding.btnDiscord.setOnClickListener {
            Toast.makeText(this, "Discord link coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchMinecraft() {
        val packages = listOf(
            "com.mojang.minecraftpe",
            "com.mojang.minecraftpeedu"
        )
        for (pkg in packages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                startActivity(intent)
                return
            }
        }
        Toast.makeText(this, "Minecraft is not installed!", Toast.LENGTH_SHORT).show()
    }

    private fun requestOverlayPermission() {
        Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_LONG).show()
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh overlay status
        if (OverlayService.isRunning) {
            binding.btnToggleOverlay.text = "Disable Overlay"
            binding.overlayStatus.text = "Overlay: ON"
            binding.overlayStatusDot.setBackgroundResource(R.drawable.dot_on)
        } else {
            binding.btnToggleOverlay.text = "Enable Overlay"
            binding.overlayStatus.text = "Overlay: OFF"
            binding.overlayStatusDot.setBackgroundResource(R.drawable.dot_off)
        }
    }
}
