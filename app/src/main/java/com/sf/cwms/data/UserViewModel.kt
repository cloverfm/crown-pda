package com.sf.cwms.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserViewModel {
    @SerializedName("action")
    @Expose
    var action: String? = null

    @SerializedName("cent_cd")
    @Expose
    var cent_cd: String? = null

    @SerializedName("user_cd")
    @Expose
    var user_cd: String? = null

    @SerializedName("passwd")
    @Expose
    var passwd: String? = null

}