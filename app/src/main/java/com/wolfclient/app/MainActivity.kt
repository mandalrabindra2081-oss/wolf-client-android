package com.wolfclient.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wolfclient.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ModuleAdapter
    private var currentCategory = Category.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ModuleRegistry.load(this)
        setupCategoryTabs()
        setupModuleList()
        setupSearch()
        setupBottomButtons()
    }

    private fun setupCategoryTabs() {
        val categories = listOf(Category.ALL, Category.HUD, Category.VISUAL, Category.UTILITY, Category.MOVEMENT)
        val tabRow = binding.categoryTabRow

        categories.forEach { cat ->
            val chip = layoutInflater.inflate(R.layout.chip_category, tabRow, false) as TextView
            chip.text = cat.displayName
            chip.isSelected = cat == currentCategory
            chip.setOnClickListener {
                currentCategory = cat
                for (i in 0 until tabRow.childCount) {
                    tabRow.getChildAt(i).isSelected = false
                }
                chip.isSelected = true
                filterModules(binding.searchInput.text.toString())
            }
            tabRow.addView(chip)
        }
    }

    private fun setupModuleList() {
        adapter = ModuleAdapter(getFilteredModules()) { module, enabled ->
            module.enabled = enabled
            ModuleRegistry.save(this)
        }
        binding.moduleList.layoutManager = LinearLayoutManager(this)
        binding.moduleList.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filterModules(s.toString())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterModules(query: String) {
        val filtered = ModuleRegistry.modules.filter { mod ->
            val matchesCategory = currentCategory == Category.ALL || mod.category == currentCategory
            val matchesQuery = query.isEmpty() || mod.name.contains(query, ignoreCase = true) || mod.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
        adapter.updateList(filtered)
    }

    private fun getFilteredModules(): List<Module> {
        return if (currentCategory == Category.ALL) ModuleRegistry.modules
        else ModuleRegistry.modules.filter { it.category == currentCategory }
    }

    private fun setupBottomButtons() {
        binding.btnLaunchMc.setOnClickListener { launchMinecraft() }

        binding.btnOverlay.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                if (OverlayService.isRunning) {
                    stopService(Intent(this, OverlayService::class.java))
                    binding.btnOverlay.text = "Enable Overlay"
                } else {
                    startForegroundService(Intent(this, OverlayService::class.java))
                    binding.btnOverlay.text = "Disable Overlay"
                }
            } else {
                Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
        }
    }

    private fun launchMinecraft() {
        val pkgs = listOf("com.mojang.minecraftpe", "com.mojang.minecraftpeedu")
        for (pkg in pkgs) {
            packageManager.getLaunchIntentForPackage(pkg)?.let {
                startActivity(it); return
            }
        }
        Toast.makeText(this, "Minecraft not found", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.btnOverlay.text = if (OverlayService.isRunning) "Disable Overlay" else "Enable Overlay"
    }
}

class ModuleAdapter(
    private var modules: List<Module>,
    private val onToggle: (Module, Boolean) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.moduleName)
        val desc: TextView = view.findViewById(R.id.moduleDesc)
        val toggle: Switch = view.findViewById(R.id.moduleToggle)
        val category: TextView = view.findViewById(R.id.moduleCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_module, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mod = modules[position]
        holder.name.text = mod.name
        holder.desc.text = mod.description
        holder.category.text = mod.category.displayName
        holder.toggle.isChecked = mod.enabled
        holder.toggle.setOnCheckedChangeListener(null)
        holder.toggle.setOnCheckedChangeListener { _, checked ->
            onToggle(mod, checked)
        }
        holder.itemView.setOnClickListener {
            holder.toggle.isChecked = !holder.toggle.isChecked
        }
    }

    override fun getItemCount() = modules.size

    fun updateList(newList: List<Module>) {
        modules = newList
        notifyDataSetChanged()
    }
}
