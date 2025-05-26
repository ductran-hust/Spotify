package com.example.spotify.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotify.R
import com.example.spotify.databinding.ActivityMainBinding
import androidx.core.view.get
import androidx.core.view.size

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpIndicator()
    }

    private fun setUpIndicator () {
        with(binding) {
            val ellipse = bottomNav.ellipseIndicator
            bottomNav.bottomNavigation.post {
                val itemView = (bottomNav.bottomNavigation.getChildAt(0) as? ViewGroup)?.getChildAt(0)
                itemView?.let {
                    val itemX = it.left + it.width / 2 - ellipse.width / 2
                    ellipse.translationX = itemX.toFloat()
                }
            }
            bottomNav.bottomNavigation.setOnItemSelectedListener { item ->
                val menu = bottomNav.bottomNavigation.menu
                val selectedIndex = (0 until menu.size).indexOfFirst { menu[it].itemId == item.itemId }

                bottomNav.bottomNavigation.post {
                    val itemView = (bottomNav.bottomNavigation.getChildAt(0) as? ViewGroup)?.getChildAt(selectedIndex)
                    itemView?.let {
                        val itemX = it.left + it.width / 2 - ellipse.width / 2
                        ellipse.animate()
                            .translationX(itemX.toFloat())
                            .setDuration(200)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}