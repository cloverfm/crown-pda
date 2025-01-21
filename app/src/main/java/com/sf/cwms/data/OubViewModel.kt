package com.sf.cwms.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sf.cwms.CWmsApp
import com.sf.cwms.util.SFData

class OubViewModel {
    @SerializedName("action")
    @Expose
    var action: String = ""

    @SerializedName("cent_cd")
    @Expose
    var cent_cd: String = CWmsApp.cent_cd

    @SerializedName("trn_type")
    @Expose
    var trn_type: String? = null

    @SerializedName("trn_sub_type")
    @Expose
    var trn_sub_type: String? = null

    @SerializedName("plt_bar")
    @Expose
    var plt_bar: String? = null

    @SerializedName("qty")
    @Expose
    var qty: String? = null

    @SerializedName("item_cd")
    @Expose
    var item_cd: String? = null

    @SerializedName("cust_cd")
    @Expose
    var cust_cd: String? = null

    @SerializedName("plan_gno")
    @Expose
    var plan_gno: String? = null

    @SerializedName("plan_dno")
    @Expose
    var plan_dno: String? = null

    @SerializedName("oub_no")
    @Expose
    var oub_no: String? = null

    @SerializedName("step")
    @Expose
    var step: String? = null

    @SerializedName("next_step")
    @Expose
    var next_step: String? = null

    @SerializedName("loc_cd")
    @Expose
    var loc_cd: String? = null

    @SerializedName("exp_type")
    @Expose
    var exp_type: String? = null

    @SerializedName("exp_date")
    @Expose
    var exp_date: String? = null


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