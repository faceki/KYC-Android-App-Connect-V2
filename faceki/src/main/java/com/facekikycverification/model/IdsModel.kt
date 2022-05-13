package com.facekikycverification.model

import java.io.Serializable

class IdsModel : Serializable {
    var idName: String = ""
    var sideImageDark: Int = 0
    var sideImageWhite: Int = 0
    var side: String = ""
    var desc: String = ""
    var imagePath: String = ""
    var uploadingStatus: String = "pending"
}