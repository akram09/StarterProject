
package oxxy.kero.roiaculte.team7.starterproject.utils.extension
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T> LiveData<T>.observeK(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}

inline fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    this.observe(owner, Observer { it?.run(observer) })
}