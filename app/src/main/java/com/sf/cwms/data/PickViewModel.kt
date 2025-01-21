package com.sf.cwms.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sf.cwms.CWmsApp

class PickViewModel {
    @SerializedName("action")
    @Expose
    var action: String = ""

    @SerializedName("cent_cd")
    @Expose
    var cent_cd: String = CWmsApp.cent_cd

      @SerializedName("plt_bar")
    @Expose
    var plt_bar: String? = null

    @SerializedName("loc_cd")
    @Expose
    var loc_cd: String? = null

    @SerializedName("qty")
    @Expose
    var qty: String? = null

    @SerializedName("item_cd")
    @Expose
    var item_cd: String? = null

    @SerializedName("pk_no")
    @Expose
    var pk_no: String? = null

    @SerializedName("reg_user")
    @Expose
    var reg_user: String? = null

    @SerializedName("reg_name")
    @Expose
    var reg_name: String? = null

    @SerializedName("reg_ip")
    @Expose
    var reg_ip: String? = null

    @SerializedName("reg_dev")
    @Expose
    var reg_dev: String? = null

    fun default_set(p_action :String){
        action = p_action
        cent_cd = CWmsApp.cent_cd
        reg_user = CWmsApp.user_cd
        reg_name = CWmsApp.user_nm
        reg_dev = CWmsApp.pda_no
        reg_ip = CWmsApp.reg_ip
    }


}