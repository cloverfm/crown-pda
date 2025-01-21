package com.sf.cwms.base

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.ConfirmDialog
import com.sf.cwms.util.UserLog
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


abstract class BaseFragment<B: ViewDataBinding> (@LayoutRes val layoutId:Int) : Fragment() {
    lateinit var binding : B
    var bScanFlag : Boolean = false
    var apiService: ApiService? = null
    var bDataExist:Boolean = false
    val scanMfgMsg:String = "24-03-16이전 생산분은 팔레트 선택해서 등록하세요"
    var mMediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        initViewModel()
        initListener()
        afterOnCreate()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
        destoryView()
    }

    protected open fun initView() {}
    protected open fun initViewModel() {}
    protected open fun initListener() {}
    protected open fun afterOnCreate() {}
    protected open fun callmainActivity() {}
    protected open fun destoryView(){

    }

    protected fun setTextView(tv: TextView,tv_text:String, @ColorRes bg:Int, @ColorRes fg:Int)
    {
        var bg_color = ContextCompat.getColor (binding.root.context, bg)
        var fg_color = ContextCompat.getColor (binding.root.context, fg)

        tv.text = tv_text

        tv.setBackgroundColor(bg_color)
        tv.setTextColor(fg_color)
    }
    protected fun setTextView(tv: TextView,tv_text:String, @ColorRes bg:Int, @ColorRes fg:Int, textSize:Float)
    {
        var bg_color = ContextCompat.getColor (binding.root.context, bg)
        var fg_color = ContextCompat.getColor (binding.root.context, fg)

        tv.text = tv_text

        tv.setBackgroundColor(bg_color)
        tv.setTextColor(fg_color)

        tv.setTextSize(Dimension.SP, textSize)
    }

    public fun userLog(msg:String)
    {
        UserLog.userLog(msg)
    }

    protected fun CurrentDate(): String {
        val mSimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val currentTime = Date()
        return mSimpleDateFormat.format(currentTime)
    }

    protected fun CurrentTime(): String {
        val mSimpleDateFormat =
            SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val currentTime = Date()
        return mSimpleDateFormat.format(currentTime)
    }
    open fun hideKeyboard() {
        // Check if no view has focus:
        //val view = ( activity as MainActivity?)!!.currentFocus
        val view = getView()
        if (view != null) {
            val inputManager =
                (activity as MainActivity?)!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    public fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            userLog("Exception=File write failed: " + e.toString())
        }
    }

    public fun readFromFile(context: Context): String? {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("config.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            userLog("File not found: " + e.toString())
        } catch (e: IOException) {
            userLog( "Can not read file: $e")
        }
        return ret
    }


    /*
        public final String GetLocalIpAddress(){
            try{
                for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.has)
            }
        }
        */
    // 알림 메세지 박스

    /*
        public final String GetLocalIpAddress(){
            try{
                for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.has)
            }
        }
        */
    // 알림 메세지 박스
    fun setCustomInfo(context: Context?, msg: String?) {

        //백그라운드 변경
        //    TextView view = new TextView(context);
        //    view.setText(msg);
        //    view.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        // SoundPlay(context, R.raw.err);
        // 위치변경
        //ViewGroup(context, R.raw.info)
        val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        val group = toast.view as ViewGroup?
        val messageTextView = group!!.getChildAt(0) as TextView
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }


    fun setCustomToast(context: Context?, msg: String?) {

        //백그라운드 변경
        //    TextView view = new TextView(context);
        //    view.setText(msg);
        //    view.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        SoundPlay(context, R.raw.err)
        //위치변경
        val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }
    open fun ShowProgressbar(msg: String?) {
        (activity as MainActivity?)!!.progressShow(msg)
    }

    open fun HideProgressBar() {
        (activity as MainActivity?)!!.progressHide()
        // progressDialog.dismiss();
    }

    class ResultMsg(var result_cd: String, var result_msg: String, var total: String) {
        var ntotal: Int

        init {
            ntotal = total.toInt()
        }
    }

    class ResultMsg2(var result_cd: String, var result_msg: String)
    class PdaVersion(var version: String, var pda_apk: String)
    open fun getHeader(): HashMap<String?, String?>? {
        val sharedPref: SharedPreferences = context!!.getSharedPreferences("authInfo", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        val headers = HashMap<String?, String?>()
        headers["Accept"] = "application/json, text/plain, */*"
        headers["Content-Type"] = "application/json;charset=UTF-8"
        headers["Authorization"] = "Bearer $token"
        return headers
    }

    open fun getResultData(body: String?): ResultMsg? {
        return try {
            val jobj = JSONObject(body)
            ResultMsg(
                jobj[ "result_cd"].toString(),
                jobj["result_msg"].toString(),
                jobj["total"].toString()
            )
        } catch (jex: JSONException) {
            null
        }
    }

    open fun getResultRData(body: String?): ResultMsg2? {
        return try {
            val jobj = JSONObject(body)
            ResultMsg2(jobj["result_cd"].toString(), jobj["result_msg"].toString())
        } catch (jex: JSONException) {
            null
        }
    }

    open fun res_fail(res_code: Int, res_errbody: String?) {
        try {
            val jObjError = JSONObject(res_errbody)
            setCustomToast(context, jObjError.getString("message"))
        } catch (e: Exception) {
            setCustomToast(context, e.message)
        }
        //clsCommon.setCustomToast(getContext(), "Code: " + res_code+res_errbody);
        return
    }

    public open fun notifyMsg(result:Boolean, msgtype:String, msg:String){
        HideProgressBar()
    }

    fun callApi(call : Call<String?>?, prg_id:String, api_name:String, action:String) {
        userLog("callApi<" + prg_id  + "> apiname <"+ api_name +"> action <" +  action + ">") ;
        CWmsApp.apiSet(prg_id, api_name,action)
        ShowProgressbar("")
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                HideProgressBar()
                if ( !CWmsApp.isTest) userLog(response.toString());
                if (!response.isSuccessful) {
                    userLog( "onResponse err : " +response.errorBody().toString())
                    res_fail(response.code(), response.errorBody().toString())
                    return
                } //석세스 아닐 경우
                val body = response.body()
                userLog( "onResponse data : " + body.toString())
                notifyMsg(true, AppInfo.MSG_COMM, body.toString() );
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                HideProgressBar()
                userLog( "onFailure data :  $t" );

                setCustomToast(context, "Code: " + t.message)
                notifyMsg(false, AppInfo.MSG_COMM, t.message.toString() );
            }
        })
    }

    fun ShowMessageBox(msg:String, action:String){
        val builder = AlertDialog.Builder(context) // 여기서 this는 Activity의 this
        builder.setTitle("알림") // 제목 설정
            .setMessage(msg) // 메세지 설정
            .setCancelable(false) // 뒤로 버튼 클릭시 취소 가능 설정
            .setPositiveButton("확인") { dialog, whichButton ->
                notifyMsg(true, AppInfo.MSG_BOX,  action)
            }
        val alertDialog = builder.create()
        alertDialog.show()
        notifyMsg(false, AppInfo.MSG_BOX,  action)
    }

    // 확인창 표시
    public fun Confirm(msg:String, title:String, action:String)
    {
        val dialog = ConfirmDialog()
        dialog.setTitle(msg, title)
        dialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
            override fun onOkClicked() {
                notifyMsg(true, AppInfo.MSG_CONFIRM, action)
            }
            override fun onCancelClicked() {
                notifyMsg(false, AppInfo.MSG_CONFIRM, action)
            }
        })
        dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
    }

    public fun ScanDataCheck(scanData:String):Boolean{
        return true
        /*
        val wordsBits = scanData.split("-")
        if ( wordsBits.size >3 ) {
            if (wordsBits[0].equals("P")) {
                var mfg_dat = wordsBits[2]
                userLog("scan mfg_dat = " + mfg_dat)
                if ( mfg_dat.toInt() < 20240316){
                    userLog("confirm scan mfg_dat = " + mfg_dat)
                    return false
                }
            }
        }
        return true

         */
    }

    public fun Confirm(msg:String, title:String, action:String, param:String)
    {
        val dialog = ConfirmDialog()
        dialog.setTitle(msg, title)
        dialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
            override fun onOkClicked() {
                notifyMsg(true, action, param )
            }
            override fun onCancelClicked() {
                notifyMsg(false, action, param )
            }
        })
        dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
    }


    open fun getVersion(body: String?): PdaVersion? {
        return try {
            val jobj = JSONObject(body)
            PdaVersion(
                jobj[ "version"].toString(),
                jobj[ "pda_apk"].toString()
            )
        } catch (jex: JSONException) {
            userLog("getVersion" + jex.message)
            null
        }
    }
    open fun VersionProcess(msg:String):String{
        val ver = msg.split(",")
        if (!ver.get(0).equals(AppInfo.VERSION)){
            CWmsApp.api_apkurl = ver.get(1)
            CWmsApp.api_down = true
            var mess =  "버전이 변경되었습니다. 프로그램 다운받으세요"
            return mess
        }
        return ""
    }


    fun SoundPlay1(context: Context?, soundfile_path: Int?) {
        val mSoundId: Int

        val mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder()
                .setMaxStreams(10)
                .build()
        } else {
            SoundPool(10, AudioManager.STREAM_MUSIC, 1)
        }

        //mSoundId = mSoundPool.load(context, soundfile_path!!, 1)
        mSoundId = mSoundPool.load(context, R.raw.err, 1)
        mSoundPool.play(mSoundId, 1f, 1f, 1, 3, 1f)

    }
    fun SoundPlay(context: Context?, resId: Int) {
        playSound(context, resId)
    }


    fun playSound(context: Context?, resId:Int) {
        if (resId == R.raw.err || resId == R.raw.ok){
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(context, resId)
                mMediaPlayer!!.isLooping = false
                mMediaPlayer!!.start()
            } else mMediaPlayer!!.start()
            mMediaPlayer!!.start()
            mMediaPlayer!!.start()

        }
        else {
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(context, resId)
                mMediaPlayer!!.isLooping = false
                mMediaPlayer!!.start()
            } else mMediaPlayer!!.start()
        }
    }

    // 2. Pause playback
    fun pauseSound() {
        if (mMediaPlayer?.isPlaying == true) mMediaPlayer?.pause()
    }

    // 3. Stops playback
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Destroys the MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

}