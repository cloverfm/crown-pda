package com.sf.cwms.util
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.content.FileProvider
import com.sf.cwms.BuildConfig
import com.sf.cwms.util.UserLog.Companion.userLog
import okio.ByteString.Companion.readByteString
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.net.URL
import kotlin.jvm.Synchronized

class UrlFindDownload :Thread{

    private var bOK: Boolean? = null
    private var mFile: String? = null
    private var mUrl: String? = null
    private var mHandler: Handler? = null
    private var mRcvType: String? = null
    private var mContext:Context? = null

    object MessageTypeClass {
        const val ERROR = 0
        const val CONNECTED = 1
        const val JSON = 2
        const val FILE = 3
    }

    constructor(context: Context?, url: String, rcvType:String, handler: Handler?) {
        mUrl = url;
        mHandler = handler
        mRcvType = rcvType
        mContext = context

    }
    public fun progresss(msg:String){
    }

    private fun makeMessage(what: Int, obj: Any?) {
        val msg = Message.obtain()
        msg.what = what //what.ordinal();
        msg.obj = obj
        mHandler!!.sendMessage(msg)
    }

    public fun DownloadEnd()
    {
        UserLog.userLog("DownloadEnd >>"  +mFile )
        makeMessage(MessageTypeClass.FILE, mFile)

        try {
            val apkfile = File(mFile)
            val apkUri = FileProvider.getUriForFile(
                mContext!!, BuildConfig.APPLICATION_ID + ".fileprovider", apkfile
            )
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)

            intent.data = apkUri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            mContext!!.startActivity(intent)

        } catch (e: Exception) {
            makeMessage(0, e.message)
            Log.d("Download File Async", "error:" + e.message)
        }
    }
    override fun run() {
        var count = 0
        bOK = false
        try {
            Thread.sleep(100)
            UserLog.userLog("DownloadFileAsync"+ mUrl!!)
            val url = URL(mUrl)
            val conexion = url.openConnection()
            conexion.connect()
            if ( mRcvType.equals("json")){
                val lenghtOfFile = conexion.contentLength
                val input: InputStream = BufferedInputStream(url.openStream())
                val data = ByteArray(1024)
                var total: Long = 0
                //		publishProgress("max" + (int) ((total * 100) / lenghtOfFile));
                var recvData = ""
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    val str = String.format("%d", total)
                    userLog(str)
                    progresss("" + (total * 100 / lenghtOfFile).toInt() + str)
                    recvData += data.toString(Charsets.UTF_8)

                }
                bOK = true

                input.close()
                var tot = total.toInt()
                progresss("Task " + 1 + " number");
                makeMessage(MessageTypeClass.JSON, recvData.substring(0,tot))
            }
            else {
                UserLog.userLog("Download File Async connected")
                mFile =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString()
                mFile += "/PDA.APK"
                //mFile = Environment.getExternalStorageDirectory().toString() + "/Download/PDA.APK";
                UserLog.userLog("filename>$mFile")
                val lenghtOfFile = conexion.contentLength
                val input: InputStream = BufferedInputStream(url.openStream())
                val output: OutputStream = FileOutputStream(mFile)
                UserLog.userLog("filename opend>$mFile")
                val data = ByteArray(1024)
                var total: Long = 0
                //		publishProgress("max" + (int) ((total * 100) / lenghtOfFile));
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    val str = String.format("%d", total)
                    progresss("" + (total * 100 / lenghtOfFile).toInt() + str)
                    output.write(data, 0, count)
                }
                bOK = true
                output.flush()
                output.close()
                input.close()
                userLog("Download File Async")
                progresss("Task " + 1 + " number");
                DownloadEnd()
            }
        } catch (e: InterruptedException) {
            userLog("Download File Async," + e.localizedMessage)
            e.printStackTrace()

            makeMessage(0, e.message)
        } catch (e: IOException) {
            e.printStackTrace()
            userLog("Download File Async,"+ e.localizedMessage)
            makeMessage(0, e.message)
        }
    }
}