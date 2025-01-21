package com.sf.cwms.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sf.cwms.CWmsApp
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData

class InbViewModel {
    @SerializedName("action")
    @Expose
    var action: String = ""

    @SerializedName("cent_cd")
    @Expose
    var cent_cd: String =  CWmsApp.cent_cd

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

    @SerializedName("io_no")
    @Expose
    var io_no: String? = null

    @SerializedName("item_cd")
    @Expose
    var item_cd: String? = null

    @SerializedName("item_nm")
    @Expose
    var item_nm: String? = null

    @SerializedName("mfg_dat")
    @Expose
    var mfg_dat: String? = null

    @SerializedName("dn_flg")
    @Expose
    var dn_flg: String? = null

    @SerializedName("loc_cd")
    @Expose
    var loc_cd: String? = null

    @SerializedName("in_loc_cd")
    @Expose
    var in_loc_cd: String? = null

    @SerializedName("cust_cd")
    @Expose
    var cust_cd: String? = null

    @SerializedName("cust_nm")
    @Expose
    var cust_nm: String? = null

    @SerializedName("plan_dno")
    @Expose
    var plan_dno: String? = null

    @SerializedName("inb_no")
    @Expose
    var inb_no: String? = null

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


    fun set(action:String, sfData: SFData){
        this.action = action;
        plt_bar = sfData.getFieldString(DataInfo.plt_bar)
        trn_type = sfData.getFieldString(DataInfo.trn_type)
        trn_sub_type = sfData.getFieldString(DataInfo.trn_sub_type)
        item_cd = sfData.getFieldString(DataInfo.item_cd)
        item_nm = sfData.getFieldString(DataInfo.item_nm)
        mfg_dat = sfData.getFieldString(DataInfo.mfg_dat)
        dn_flg = sfData.getFieldString(DataInfo.dn_flg)
        qty = sfData.getFieldString(DataInfo.qty)
        loc_cd = sfData.getFieldString(DataInfo.loc_cd)
        in_loc_cd = sfData.getFieldString(DataInfo.loc_cd)
        plan_dno = sfData.getFieldString(DataInfo.plan_dno)
        inb_no = ""
        cust_cd = sfData.getFieldString(DataInfo.cust_cd)
        cust_nm = sfData.getFieldString(DataInfo.cust_nm)
        reg_user = CWmsApp.user_cd
        reg_name = CWmsApp.user_nm
        reg_dev = CWmsApp.pda_no
        reg_ip = CWmsApp.reg_ip
    }
}