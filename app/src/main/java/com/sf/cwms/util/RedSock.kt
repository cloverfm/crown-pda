package com.sf.cwms.util

import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.*
import java.net.Socket

class RedSock : Thread {
    private var mSocket: Socket? = null
    private var buffData: DataInputStream? = null
    private var buffRecv: BufferedReader? = null
    private var buffSend: BufferedWriter? = null
    private var mAddr = "localhost"
    private var mPort = 20001

    @get:Synchronized
    var isConnected = false
        private set
    private var mHandler: Handler? = null
    private var mData: String? = null

    object MessageTypeClass {
        const val SIMSOCK_ERROR = 0
        const val SIMSOCK_CONNECTED = 1
        const val SIMSOCK_DATA = 2
        const val SIMSOCK_DISCONNECTED = 3
    }

    enum class MessageType {
        SIMSOCK_ERROR, SIMSOCK_CONNECTED, SIMSOCK_DATA, SIMSOCK_DISCONNECTED
    }

    constructor(addr: String, port: Int, handler: Handler?) {
        mAddr = addr
        mPort = port
        mHandler = handler
    }

    constructor(addr: String, port: Int, handler: Handler?, Data: String?) {
        mAddr = addr
        mPort = port
        mHandler = handler
        mData = Data
    }

    private fun makeMessage(what: MessageType, obj: Any) {
        val msg = Message.obtain()
        msg.what = what.ordinal
        msg.obj = obj
        mHandler!!.sendMessage(msg)
    }

    private fun makeMessage(what: Int, obj: Any?) {
        val msg = Message.obtain()
        msg.what = what //what.ordinal();
        msg.obj = obj
        mHandler!!.sendMessage(msg)
    }

    private fun connect(addr: String, port: Int): Boolean {
        mSocket = try {
            // InetSocketAddress socketAddress  = new InetSocketAddress (InetAddress.getByName(addr), port);
            UserLog.userLog("connect start")
            Socket(addr, port)
            // mSocket.connect(socketAddress, 5000);
        } catch (e: IOException) {
            Log.d("SimpleSocket", e.message!!)
            println(e)
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun run() {
        //      ProgressDialog dlg = ProgressDialog.show(mContext, "전송", "전송중입니다.", true, true);
        UserLog.userLog(mAddr)
        UserLog.userLog(String.format("%d", mPort))
        if (!connect(mAddr, mPort)) {
            isConnected = false
            makeMessage(MessageTypeClass.SIMSOCK_ERROR, "Connected Failed")
            //       dlg.dismiss();
            return  // connect failed
        }
        if (mSocket == null) {
            makeMessage(MessageTypeClass.SIMSOCK_ERROR, "Invalid Socket")
            //         dlg.dismiss();
            return  // connect failed
        }
        UserLog.userLog("send")
        try {
            // buffRecv = BufferedReader(InputStreamReader(mSocket!!.getInputStream(), "euc-kr"))
            buffRecv = BufferedReader(InputStreamReader(mSocket!!.getInputStream(), "utf-8"))
            //buffData = DataInputStream(mSocket!!.getInputStream())
            buffSend = BufferedWriter(OutputStreamWriter(mSocket!!.getOutputStream(), "euc-kr"))
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            makeMessage(0, e.message)
        }
        UserLog.userLog("ConnectedOK")
        isConnected = true
        makeMessage(MessageTypeClass.SIMSOCK_CONNECTED, "Connected OK")
        // makeMessage(MessageType.SIMSOCK_CONNECTED, "");
        Log.d("SimpleSocket", "socket_thread loop started")
        var msgData : String  = ""
        var aLine: String? = null
        var bStart : Boolean = false
        var bEnd : Boolean = false

        // sendString(mData);
        // buffSend.write( mData, 0, mData.length() );
        val out = PrintWriter(buffSend, true)

        out.println(mData)
        UserLog.userLog("received")


        while (!interrupted()) {
            try {

                aLine = buffRecv!!.readLine()
                if (aLine != null) {
                    UserLog.userLog("RedSocket=>received Size" + aLine.length)
                    msgData = msgData + aLine
                    if ( msgData.length > 7 && msgData.substring(0, 7).equals("<START>")){
                        bStart = true
                    }
                    if ( msgData.length > 12 && msgData.substring(msgData.length - 5, msgData.length).equals("<END>")) {
                        bEnd = true
                    }
                    if ( bStart == true && bEnd == true) {
                        UserLog.userLog("RedSocket=>MessageSend")
                        var msgSendData = msgData.replace("<START>", "").replace("<END>", "")
                        makeMessage(
                            Red_Socket.MessageTypeClass.SIMSOCK_DATA,
                            msgSendData
                        )
                        break
                    }
                }
                else {
                    UserLog.userLog("RedSocket => dataReceive None break")
                    break
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        UserLog.userLog("disconnect")
        makeMessage(MessageTypeClass.SIMSOCK_DISCONNECTED, "Disconnected")
        //makeMessage(MessageType.SIMSOCK_DISCONNECTED, "");
        Log.d("SimpleSocket", "socket_thread loop terminated")
        try {
            buffRecv!!.close()
            buffSend!!.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        //    dlg.dismiss();
        isConnected = false
    }

    fun sendString(str: String?) {
        val out = PrintWriter(buffSend, true)
        out.println(str)
    }
}