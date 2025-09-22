package tw.com.tp6gl4cj86.olis_number

import android.view.View

fun View.initViewGroupFromXML() {
    OlisNumber.initViewGroupFromXML(this)
}

fun View.initViewGroupFromXML(scale: Float) {
    OlisNumber.initViewGroupFromXML(this, scale)
}

fun View.initViewGroupFromXML(olisNumberObject: OlisNumberObject, scale: Float) {
    OlisNumber.initViewGroupFromXML(this, olisNumberObject, scale)
}

fun Float.px(): Int {
    return OlisNumber.getPX(this)
}

fun Float.dp2px(): Float {
    return this * OlisNumber.getDensity() + 0.5f
}

fun Float.px2dp(): Float {
    return this / OlisNumber.getDensity()
}