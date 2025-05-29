package com.example.spotify.presentation.custom_components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.spotify.R

class AppBottomNavigation @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    private val navItems = mutableListOf<NavItemView>()
    private var selectedIndex = 0
    private var onItemSelectedListener: ((Int) -> Unit)? = null


    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_color)
        style = Paint.Style.FILL
    }

    private val indicatorWidth = 20.dpToPx()
    private val indicatorHeight = 4.dpToPx()
    private var indicatorX = 0f
    private val indicatorY = 0.dpToPx()
    private var targetIndicatorX = 0f

    private val indicatorAnimator = ValueAnimator().apply {
        duration = 200
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animator ->
            indicatorX = animator.animatedValue as Float
            invalidate()
        }
    }

    private val itemsContainer = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    private val centerImage = ImageView(context).apply {
        val size = 64.dpToPx().toInt()
        layoutParams = LayoutParams(size, size).apply {
            gravity = Gravity.CENTER
            topMargin = (-24).dpToPx().toInt()
        }
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        setBackgroundColor(Color.TRANSPARENT)
        setImageResource(R.drawable.navigation_bar_icon)
        elevation = 8.dpToPx()
    }

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_color))
        setPadding(0, 35, 0, 35)
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        getDefaultNavItems()
        addView(itemsContainer)
        addView(centerImage)
        setWillNotDraw(false)

        clipChildren = false
        clipToPadding = false

        if (parent is ViewGroup) {
            (parent as ViewGroup).clipChildren = false
            (parent as ViewGroup).clipToPadding = false
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        var currentParent = parent
        while (currentParent is ViewGroup) {
            currentParent.clipChildren = false
            currentParent.clipToPadding = false
            currentParent = currentParent.parent
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(navItems.isNotEmpty()) {

            canvas.drawArc(
                indicatorX,
                indicatorY - indicatorHeight,
                indicatorX + indicatorWidth,
                indicatorY + indicatorHeight ,
                0f,
                180f,
                true,
                indicatorPaint
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed && navItems.isNotEmpty()) {
            updateIndicatorPosition(selectedIndex, false)
        }
    }

    fun addNavItem(defaultIcon: Int, title: String, selectedIcon: Int) {
        val itemView = NavItemView(context).apply {
            setupItem(defaultIcon, title, selectedIcon)
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            setOnClickListener { selectItem(navItems.indexOf(this)) }
        }

        navItems.add(itemView)
        itemsContainer.addView(itemView)

        if (navItems.size == 1) {
            itemView.isSelected = true
            post { updateIndicatorPosition(0, false) }
        }
    }

    private fun selectItem(index: Int) {
        if (index == selectedIndex || index >= navItems.size || index == 2) return

        navItems[selectedIndex].setSelected(false)

        navItems[index].setSelected(true)
        selectedIndex = index

        updateIndicatorPosition(index, true)

        onItemSelectedListener?.invoke(index)
    }

    private fun updateIndicatorPosition(index: Int, animate: Boolean) {
        if (navItems.isEmpty() || width == 0) return

        val itemWidth = width.toFloat() / navItems.size
        targetIndicatorX = (itemWidth * index) + (itemWidth - indicatorWidth) / 2

        if (animate) {
            indicatorAnimator.setFloatValues(indicatorX, targetIndicatorX)
            indicatorAnimator.start()
        } else {
            indicatorX = targetIndicatorX
            invalidate()
        }
    }

    private fun Int.dpToPx(): Float {
        return this * context.resources.displayMetrics.density
    }

    fun getDefaultNavItems() {
        addNavItem(R.drawable.home_gray_outline, "Home", R.drawable.homepage)
        addNavItem(R.drawable.music_filter_gray, "Playlist", R.drawable.music_filter)
        addNavItem(0, "", 0)
        addNavItem(R.drawable.clock_gray, "Playlist", R.drawable.clock)
        addNavItem(R.drawable.profile_gray, "Playlist", R.drawable.profile)
    }

    fun setOnItemSelectedListener(listener: (Int) -> Unit) {
        this.onItemSelectedListener = listener
    }
}

class NavItemView(context: Context): LinearLayout(context) {
    private val ivIcon: ImageView
    private val tvTitle: TextView
    private var selectedIcon: Int = 0
    private var defaultIcon: Int = 0


    private val colorSelected by lazy { ContextCompat.getColor(context, R.color.primary_color) }
    private val colorDefault by lazy { ContextCompat.getColor(context, R.color.white_60) }

    private fun Int.dpToPx(): Float {
        return this * context.resources.displayMetrics.density
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        ivIcon = ImageView(context).apply {
            layoutParams = LayoutParams(24.dpToPx().toInt(), 24.dpToPx().toInt()).apply {
                bottomMargin = 5.dpToPx().toInt()
            }
        }
        addView(ivIcon)

        tvTitle = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(colorDefault)
        }
        addView(tvTitle)
    }

    fun setupItem(defaultIcon: Int, titleText: String, selectedIcon: Int) {
        this.defaultIcon = defaultIcon
        this.selectedIcon = selectedIcon
        tvTitle.text = titleText
        this.ivIcon.setImageResource(defaultIcon)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if(isSelected) {
            ivIcon.setImageResource(selectedIcon)
            tvTitle.setTextColor(colorSelected)
        } else {
            ivIcon.setImageResource(defaultIcon)
            tvTitle.setTextColor(colorDefault)
        }
    }
}