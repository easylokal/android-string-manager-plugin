package utils

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
        name = "LocalPersistenceUtils",
        storages = [Storage(Constants.PREFERENCES.EL_PREFERENCES)])

object LocalPersistenceUtils : PersistentStateComponent<LocalPersistenceUtils.State> {

    object State {
        var dataMap = mutableMapOf<String, String>()
    }

    var stateCopy: State? = State

    override fun getState() = stateCopy

    override fun loadState(state: State) {
        stateCopy = state
    }

    fun getData(key: String): String? {
        return State.dataMap.get(key)
    }

    fun setData(key: String, value: String) {
        State.dataMap.put(key, value)
    }

}
