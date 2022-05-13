package com.facekikycverification.utils

class DataManager {
    var checklist: String? = null
    var checklistName: String? = null
    var amount = 0
    var isLatLang = false

    //    public boolean isGetStore() {
    var isCreateOccacion = false
    var lat = 0.0
    var lang = 0.0

    //        return isGetStore;
    //    }
    //
    //    public void setGetStore(boolean getStore) {
    //        isGetStore = getStore;
    //    }
    companion object {
        var dManager: DataManager? = null
        val instance: DataManager?
            get() = if (dManager == null) {
                dManager = DataManager()
                dManager
            } else {
                dManager
            }
    }
}