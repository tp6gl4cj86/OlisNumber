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

/** 與 OlisNumber.dp2px 相同（四捨五入成整數 px） */
fun Float.dp2px(): Int {
    return OlisNumber.dp2px(this)
}

fun Float.px2dp(): Float {
    return this / OlisNumber.getDensity()
}