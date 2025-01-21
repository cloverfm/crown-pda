package com.sf.cwms.util

import android.os.Environment
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class SFDataList {
    var jsonArray: JSONArray? = null
    var jsonString: String? = null
    var type = 0

    constructor(list_type:Int){
        this.type = list_type;
        this.jsonArray = JSONArray("[]")
    }
    constructor(list_type:Int, body:String):this(list_type){
        this.type = list_type;
        this.jsonString = body;
        this.jsonArray = JSONArray(body);
    }

    constructor(list_type:Int,body: String, table: String):this(list_type) {
        this.jsonString = body
        this.type = list_type;
        try {
            this.jsonArray = JSONObject(body).getJSONArray(table)

        } catch (ex: JSONException) {
        }
    }
    constructor(list_type:Int, jsonArray: JSONArray):this(list_type){
        this.type = list_type;
        this.jsonArray = jsonArray;
    }

    fun Add(jsonString: String?) {
        try {
            if (jsonArray == null) jsonArray = JSONArray()
            val jsonObject = JSONObject(jsonString)
            jsonArray!!.put(jsonObject)
        } catch (ex: JSONException) {
        }
    }

    fun Add(sfData: SFData) {
        if (jsonArray == null) jsonArray = JSONArray()
        jsonArray!!.put(sfData._jobjRec)
    }

    fun Remove(pos: Int) {
        try {
            jsonArray!!.remove(pos)
        } catch (e: Exception) {
        }
    }

    fun Remove() {
        try {
            jsonArray!!.remove(0)
        } catch (e: Exception) {
        }
    }
    fun getJsonString(pos: Int): String {
        return try {
            jsonArray!!.getJSONObject(pos).toString()
        } catch (ex: JSONException) {
            ""
        }
    }

    fun FileToData(filename: String) {
        val jsonString = jsonArray.toString()
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        var JsonFile: String? = path
        JsonFile += filename
        try {
            val br = BufferedReader(FileReader(JsonFile))
            var readStr = ""
            var str: String? = null
            while (br.readLine().also { str = it } != null) {
                readStr += """
                $str
                
                """.trimIndent()
            }
            br.close()
            UserLog.userLog("FileToDate=$readStr")
            jsonArray = JSONArray(readStr)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun DataToFile(filename: String) {
        val jsonString = jsonArray.toString()
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        var JsonFile: String? = path
        JsonFile += filename
        try {
            UserLog.userLog("DateToWrite=$jsonString")
            val bw = BufferedWriter(FileWriter(JsonFile, false))
            bw.write(jsonString)
            bw.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun returnArrayList(jtype: Int): ArrayList<SFData> {
        var aList: ArrayList<SFData> = ArrayList<SFData>()
        for (row in 0 until getSize()) {
            var sfData = getRecord(jtype, row);
            aList.add(sfData!!);
        }
        return aList
    }


    fun getSize(): Int {
        return if (jsonArray == null) 0 else jsonArray!!.length()
    }

    fun Key(findkey: String): Boolean {
        return try {
            val jsonObject = jsonArray!!.getJSONObject(0)
            val i: Iterator<*> = jsonObject.keys() // key값들을 모두 얻어옴.
            while (i.hasNext()) {
                val b = i.next().toString()
                if (b == findkey) {
                    return true
                }
            }
            false
        } catch (e: JSONException) {
            e.printStackTrace()
            false
        }
    }

    fun find_row(field_name: String, field_val: String): Int {
        val len = getSize()
        for (i in 0 until len) {
            if (getFieldString(i, field_name) == field_val) {
                return i
            }
        }
        return -1
    }

    fun find_row(field_name: String, field_val: String, startrow: Int): Int {
        val len = getSize()
        for (i in startrow until len) {
            if (getFieldString(i, field_name) == field_val) {
                return i
            }
        }
        return -1
    }
    /*
    fun select(find_name: String, field_val: String): SFDataList? {
        var sfDataList: SFDataList = SFDataList()
        var len = getSize()
        for (i in 0 until len) {
            if (getFieldString(i, find_name) == field_val) {
                val sfData: SFData? = getRecord(type, i)
                sfDataList.setRecord(sfData!!)
            }
        }
        return sfDataList
    }
  */
    fun getSum(field_name: String): Int? {
        var tot = 0
        for (row in 0 until getSize()) {

            tot += getFieldInteger(row, field_name)
        }
        return tot
    }


    fun getRecord(RecType: Int): SFData? {
        return try {
            val jsonObject = jsonArray!!.getJSONObject(0)
            return SFData(RecType, jsonArray!!.getJSONObject(0))
        } catch (ex: JSONException) {
            null
        }
    }

    fun getRecord(RecType: Int, pos: Int): SFData? {
        return try {
            SFData(RecType, jsonArray!!.getJSONObject(pos))
        } catch (ex: JSONException) {
            null
        }
    }

    fun setRecord(kData: SFData) {
        jsonArray!!.put(kData._jobjRec)
    }


    fun getFieldString(fieldn: String): String? {
        return try {
            SFData(type, jsonArray!!.getJSONObject(0)).getFieldString(fieldn)
        } catch (ex: JSONException) {
            ""
        }
    }

    fun getFieldString(pos: Int, fieldn: String): String? {
        return try {
            SFData(type, jsonArray!!.getJSONObject(pos) ).getFieldString(fieldn)
        } catch (ex: JSONException) {
            return ""
        }
    }

    fun getFieldInteger(fieldn: String): Int {
        return try {
            SFData(type, jsonArray!!.getJSONObject(0)).getFieldInteger(fieldn)
        } catch (ex: JSONException) {
            0
        }
    }

    fun getFieldInteger(pos: Int, fieldn: String): Int {
        return try {
            SFData(type, jsonArray!!.getJSONObject(pos)).getFieldInteger(fieldn)
        } catch (ex: JSONException) {
            0
        }
    }

    fun setField(field_name: String, field_val: String?) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(0)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }

    fun setField(field_name: String, field_val: Int) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(0)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }

    fun setField(field_name: String, field_val: Double) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(0)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }

    fun setField(pos: Int, field_name: String, field_val: String?) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(pos)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }

    fun setField(pos: Int, field_name: String, field_val: Int) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(pos)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }

    fun setField(pos: Int, field_name: String, field_val: Double) {
        try {
            val _jobjRec = jsonArray!!.getJSONObject(pos)
            _jobjRec.put(field_name, field_val)
        } catch (ex: JSONException) {
            UserLog.userLog("KDataList_SetField:field_name" + field_name + " error" + ex.message)
            return
        }
    }
}