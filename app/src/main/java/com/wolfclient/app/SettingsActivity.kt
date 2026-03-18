package com.wolfclient.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wolfclient.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("wolf_settings", MODE_PRIVATE)

        // Load saved states
        binding.switchFps.isChecked        = prefs.getBoolean("fps_overlay", false)
        binding.switchCoords.isChecked     = prefs.getBoolean("coords_overlay", false)
        binding.switchKeystrokes.isChecked = prefs.getBoolean("keystrokes", false)
        binding.switchCps.isChecked        = prefs.getBoolean("cps_counter", false)
        binding.switchPing.isChecked       = prefs.getBoolean("ping_display", false)
        binding.switchArmorHud.isChecked   = prefs.getBoolean("armor_hud", false)
        binding.switchAutoTotem.isChecked  = prefs.getBoolean("auto_totem", false)
        binding.switchOffhand.isChecked    = prefs.getBoolean("offhand_slot", false)

        // Save on change
        binding.switchFps.setOnCheckedChangeListener        { _, v -> prefs.edit().putBoolean("fps_overlay", v).apply() }
        binding.switchCoords.setOnCheckedChangeListener     { _, v -> prefs.edit().putBoolean("coords_overlay", v).apply() }
        binding.switchKeystrokes.setOnCheckedChangeListener { _, v -> prefs.edit().putBoolean("keystrokes", v).apply() }
        binding.switchCps.setOnCheckedChangeListener        { _, v -> prefs.edit().putBoolean("cps_counter", v).apply() }
        binding.switchPing.setOnCheckedChangeListener       { _, v -> prefs.edit().putBoolean("ping_display", v).apply() }
        binding.switchArmorHud.setOnCheckedChangeListener   { _, v -> prefs.edit().putBoolean("armor_hud", v).apply() }
        binding.switchAutoTotem.setOnCheckedChangeListener  { _, v -> prefs.edit().putBoolean("auto_totem", v).apply() }
        binding.switchOffhand.setOnCheckedChangeListener    { _, v -> prefs.edit().putBoolean("offhand_slot", v).apply() }
    }
}
