package com.sf.cwms.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.sf.cwms.R
import com.sf.cwms.util.CustomProgressDialog
import com.sf.cwms.util.SimpleToast
import com.sf.cwms.util.UserLog
import io.reactivex.disposables.CompositeDisposable
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

abstract class BaseActivity<T:ViewDataBinding>(@LayoutRes val layoutId:Int):AppCompatActivity(){
    lateinit var binding : T
    private val REQUEST_PERMISSIONS = 1
    private val compositeDisposable = CompositeDisposable()
    private var pre_fragment:Fragment?=null
    var fileContent:String = ""
    var prgbarDialog: CustomProgressDialog? = null
    var imm: InputMethodManager? = null

    val networkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // 네트워크가 연결될 때 호출됩니다.
            networkAvailable();
        }
        override fun onLost(network: Network) {
            // 네트워크가 끊길 때 호출됩니다.
            networkLost();
        }
    }

    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallBack)
    }

    // 콜백을 해제하는 함수
    private fun terminateNetworkCallback() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.unregisterNetworkCallback(networkCallBack)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        beforeSetContentView()
        super.onCreate(savedInstanceState)
        // do something on create
        // 뷰 바인딩 초기화 등
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner=this
        prgbarDialog = CustomProgressDialog(binding!!.root.context)
        prgbarDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initView()
        initViewModel()
        initListener()
        afterOnCreate()
    }

    override fun onResume() {
        super.onResume()
        registerNetworkCallback()
    }

    override fun onStop() {
        super.onStop()
        terminateNetworkCallback()
    }

    public fun checkPermission() {
        var permission = mutableMapOf<String, String>()
        permission["camera"] = Manifest.permission.CAMERA
        permission["storageRead"] = Manifest.permission.READ_EXTERNAL_STORAGE
        permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE

        // 현재 권한 상태 검사
        var denied = permission.count { ContextCompat.checkSelfPermission(this, it.value)  == PackageManager.PERMISSION_DENIED }

        // 마시멜로 버전 이후
        if(denied > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission.values.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // do something on destory
        // disposable 정리 등
        compositeDisposable.clear();
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme
        }
    }
    // 화면 touch 시 키보드 hidden
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass
                .getName().startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    public fun showToast(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        //SimpleToast.createToast(this, msg)?.show()
    }

    protected fun userLog(msg:String)
    {
        UserLog.userLog(msg)
    }

    protected open fun beforeSetContentView() {}
    protected open fun initView() {}
    protected open fun initViewModel() {}
    protected open fun initListener() {}
    protected open fun afterOnCreate() {}
    protected open fun networkAvailable() {}
    protected open fun networkLost() {}

    public fun showProgressbar(msg:String){
        prgbarDialog!!.show()
    }

    public fun hideProgressbar(){
        prgbarDialog!!.hide()
    }

    public fun startCustomActivity(cls:Class<*>, bundle:Bundle){
        var intent = Intent(this, cls)
        intent.putExtras(bundle)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    public fun startCustomActivity(cls:Class<*>){
        var intent = Intent(this, cls)
        startActivity(intent)
    }

    public fun getIntentBundle() : Bundle
    {
        var intent = getIntent()
        var bundle = intent.getBundleExtra("bundle") as Bundle
        return bundle
    }

    protected fun isCurrentFragment(fragment_name: String): Boolean? {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        UserLog.userLog("current fragment class name:" + fragment!!.javaClass.name)
        // 현재 Fragment_name
        return fragment.javaClass.name == fragment_name
    }

    protected fun setDefaultFragment(fragment: Fragment?, text: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayout, fragment!!)
        transaction.commit()
    }

    public fun setTitle(textView:TextView, title: String){
    }

    // replace
    protected fun replaceFragment(fragment: Fragment?, text: String?) {
        // 현재 fragment 명을 저장
        // pre_fragment = getFragmentManager().findFragmentById(R.id.frameLayout).getClass().getName();
        pre_fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment!!)
        transaction.commit()
    }

    // 파라메터 전달이 있는 경우
    protected fun replaceFragment(fragment: Fragment, text: String?, bundle: Bundle?) {
        pre_fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        //binding!!.topBar.txName.setText(text)
    }

    protected fun currentFragment():Fragment{
        return supportFragmentManager.findFragmentById(R.id.frameLayout) as Fragment
    }

    open fun readFile(fileTitle:String) {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

        val file = File(filePath, fileTitle)
        try {
            val reader = BufferedReader(FileReader(file))
            var result = ""
            var line: String
            while (reader.readLine().also { line = it } != null) {
                result += line
            }
            fileContent = result
            userLog("불러온 내용 : $result")
            reader.close()
        } catch (e1: FileNotFoundException) {
            userLog("readFile error1=" + e1.message)
        } catch (e2: IOException) {
            userLog("readFile error2=" + e2.message)
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    open fun Sha256encrypt(text: String): String? {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        md.update(text.toByteArray())
        return bytesToHex(md.digest())
    }

    open fun bytesToHex(bytes: ByteArray): String? {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }
/*
    fun SoundPlay(context: Context?, soundfile_path: String?) {
        val mSoundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        mSoundPool.setOnLoadCompleteListener { soundPool, soundId, status ->
            soundPool.play(
                soundId,
                100f,
                100f,
                1,
                0,
                1.0f
            )
        }
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

 */
}