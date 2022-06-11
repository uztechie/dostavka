package uz.ibroxim.dostavkauz.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.yandex.mapkit.mapview.MapView

class CustomMapView:MapView {
    constructor(context: Context):
            super(context)
    constructor(context: Context, attributeSet: AttributeSet):
            super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, int: Int):
            super(context, attributeSet, int)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d("CustomWebView", "touchevent")
        when(event?.action) {
            MotionEvent.ACTION_UP -> {
                Log.d("CustomWebView", "disallow Intercept")
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d("CustomWebView", "allow Intercept")
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(event)
    }
}