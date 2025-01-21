package com.sf.cwms.api

import com.sf.cwms.data.InbViewModel
import com.sf.cwms.data.PickViewModel
import com.sf.cwms.data.OubViewModel
import com.sf.cwms.data.UserViewModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("api/WmPda/GetLogin")
    fun GetLogin(
        @HeaderMap headers: Map<String?, String?>?,
        @Body userViewModel: UserViewModel?
    ): Call<String?>?

    @GET("api/WmPda/GetLocation")
    fun GetLocation(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("loc_type") loc_type: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("item_class") item_class: String?,
    ): Call<String?>?

    @GET("api/WmPda/GetCombo")
    fun GetCombo(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("class_cd") class_cd: String?,
        @Query("code_cd") code_cd: String?,
    ): Call<String?>?

    @GET("api/WmPda/StockList")
    fun StockList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("item_cd") item_cd: String?,
    ): Call<String?>?

    @POST("api/WmPda/MoveReg")
    fun MoveReg(
        @HeaderMap headers: Map<String?, String?>?,
        @Body invViewModel: InbViewModel?
    ): Call<String?>?


    @GET("api/WmPda/InbPltInfo")
    fun InbPltInfo(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("plan_dno") plan_dno: String?,
    ): Call<String?>?

    @GET("api/WmPda/InbLocList")
    fun InbLocList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("item_cd") item_cd: String?,
        @Query("item_class_cd") item_class_cd: String?
    ): Call<String?>?


    @POST("api/WmPda/InbReg")
    fun InbReg(
        @HeaderMap headers: Map<String?, String?>?,
        @Body invViewModel: InbViewModel?
    ): Call<String?>?

    @GET("api/WmPda/PickList")
    fun PickList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("pk_no") pk_no: String?,
        @Query("pk_grp_no") pk_grp_no: String?,
        @Query("item_cd") item_cd: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("pk_yn") pk_yn: String?
    ): Call<String?>?

    @GET("api/WmPda/PickUserList")
    fun PickUserList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("pk_no") pk_no: String?,
        @Query("pk_grp_no") pk_grp_no: String?,
        @Query("item_cd") item_cd: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("pk_yn") pk_yn: String?,
        @Query("uid") uid: String?
    ): Call<String?>?



    @GET("api/WmPda/PKList")
    fun PKList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("fr_dat") fr_dat: String?,
        @Query("pk_grp_no") pk_grp_no: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("item_cd") item_cd: String?,
        @Query("pk_yn") pk_yn: String?
    ): Call<String?>?

    @GET("api/WmPda/PKUserList")
    fun PKUserList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("fr_dat") fr_dat: String?,
        @Query("pk_grp_no") pk_grp_no: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("item_cd") item_cd: String?,
        @Query("pk_yn") pk_yn: String?,
        @Query("uid") uid: String?
    ): Call<String?>?

    @POST("api/WmPda/PickReg")
    fun PickReg(
        @HeaderMap headers: Map<String?, String?>?,
        @Body pickViewModel: PickViewModel?
    ): Call<String?>?

    @GET("api/WmPda/OubList")
    fun OubList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("plan_gno") plan_gno: String?,
        @Query("plan_dno") plan_dno: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("step") step: String?,
        @Query("oub_yn") oub_yn: String?
    ): Call<String?>?

    @GET("api/WmPda/OubUserList")
    fun OubUserList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("plan_gno") plan_gno: String?,
        @Query("plan_dno") plan_dno: String?,
        @Query("plt_bar") plt_bar: String?,
        @Query("step") step: String?,
        @Query("oub_yn") oub_yn: String?,
        @Query("uid") uid: String?
    ): Call<String?>?


    @GET("api/WmPda/OubPList")
    fun OubPList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("fr_dat") fr_dat: String?,
        @Query("to_dat") to_dat: String?,
        @Query("carno") carno: String?,
        @Query("oub_yn") oub_yn: String?
    ): Call<String?>?

    @GET("api/WmPda/OubUserPList")
    fun OubUserPList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("fr_dat") fr_dat: String?,
        @Query("to_dat") to_dat: String?,
        @Query("carno") carno: String?,
        @Query("oub_yn") oub_yn: String?,
        @Query("uid") uid: String?
    ): Call<String?>?

    @GET("api/WmPda/OubPltList")
    fun OubPltList(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("action") action: String?,
        @Query("cent_cd") cent_cd: String?,
        @Query("plan_dno") plan_dno: String?,
        @Query("item_cd") item_cd: String?,
        @Query("loc_cd") loc_cd: String?,
        @Query("pk_no") pk_no: String?,
    ): Call<String?>?

    @POST("api/WmPda/OubReg")
    fun OubReg(
        @HeaderMap headers: Map<String?, String?>?,
        @Body oubViewModel: OubViewModel?
    ): Call<String?>?


    @POST("api/WmPda/OubAutoReg")
    fun OubAutoReg(
        @HeaderMap headers: Map<String?, String?>?,
        @Body oubViewModel: OubViewModel?
    ): Call<String?>?

    @POST("api/WmPda/OubConfirm")
    fun OubConfirm(
        @HeaderMap headers: Map<String?, String?>?,
        @Body oubViewModel: OubViewModel?
    ): Call<String?>?

    @POST("api/WmPda/OubRegCan")
    fun OubRegCan(
        @HeaderMap headers: Map<String?, String?>?,
        @Body oubViewModel: OubViewModel?
    ): Call<String?>?

    // Call<Res_Model>InbReg(@HeaderMap Map<String, String> headers, @Body IO_ViewModel req_viewModel);
    companion object {
        const val BASE_URL = "http://192.168.35.145:11101/"
    }
}