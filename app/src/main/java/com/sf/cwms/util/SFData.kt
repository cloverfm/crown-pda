package com.sf.cwms.util

import android.database.Cursor
import com.sf.cwms.util.UserLog.Companion.userLog
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class SFData{
    companion object {
        const val TEXT_MSG = 0
        const val GRID_COL = 1
        const val GRID_COL1 = 2
        const val GRID_COL1T = 3
        const val GRID_COL2 = 4
        const val GRID_COL3 = 5
        const val GRID_COL3T = 6
        const val GRID_COLSEL = 7
        const val GRID_LOCSEL = 8
        const val GRID_PLTBAR = 9
        const val GRID_EMPTY_LOC = 10

        const val GRID_1100 = 1100
        const val GRID_2000 = 2000
        const val GRID_2100 = 2100
        const val GRID_2101 = 2101
        const val GRID_2102 = 2102
        const val GRID_2200 = 2200
        const val GRID_2201 = 2201
        const val GRID_2202 = 2202
        const val GRID_3100 = 3100
        const val GRID_3101 = 3101
        const val GRID_3200 = 3200
        const val GRID_3201 = 3201
    }

    var type = 0
    var IsSelected = false
    var _jobjRec: JSONObject? = null
    var _row = 0
    var IsChecked = false

    // type: List Type RecyclerView에서  보이는 View 형태
    constructor(list_type:Int){
        this.type = list_type;
    }

    constructor(list_type:Int, cursor: Cursor):this(list_type){
        this.type = list_type;
        this._jobjRec = JSONObject("{}")
        var colsize = cursor.columnCount
        var colcnt = 0
        while(colcnt < colsize) {
            var fieldname = cursor.getColumnName(colcnt)
            if ( cursor.isNull(colcnt) == true ){
                var fieldval = ""
                setField(fieldname, fieldval)
            }
            else {
                var fieldval = cursor.getString(colcnt)
                setField(fieldname, fieldval)
            }
            colcnt++
        }
    }

    constructor(list_type:Int, fname:String, fvalue:String):this(list_type){
        this.type = list_type;
        this._jobjRec = JSONObject("{}");
        setField(fname, fvalue);
    }

    constructor(list_type:Int, fname1:String, fvalue1:String, fname2:String, fvalue2:String, fname3:String, fvalue3:String):this(list_type){
        this.type = list_type;
        this._jobjRec = JSONObject("{}");
        setField(fname1, fvalue1);
        setField(fname2, fvalue2);
        setField(fname3, fvalue3);
    }

    constructor(list_type:Int, field_code_name:String, fiend_code_data:String, field_name:String, fiend_data:String):this(list_type){
        this.type = list_type;
        this._jobjRec = JSONObject("{}")
        setField(field_code_name, fiend_code_data)
        setField(field_name, fiend_data)
    }

    constructor(list_type:Int, body:String):this(list_type){
        this.type = list_type;
        this._jobjRec = JSONObject(body)
    }
    constructor(list_type:Int, jobj: JSONObject):this(list_type){
        this.type = list_type;
        this._jobjRec = jobj
    }


    constructor(list_type:Int, row:Int, jobj: JSONObject):this(list_type, jobj){
        this.type = list_type;
        this._jobjRec = jobj
        this._row = row;
    }

    fun IsData(): Boolean {
        return if (_jobjRec == null) false else true
    }
    fun setField(field_name: String, field_val: String) {
        try {
            _jobjRec!!.put(field_name, field_val)
            val field = _jobjRec!!.getString(field_name)
        } catch (ex: JSONException) {
            return
        }
    }

    fun setField(field_name: String, field_val: Boolean) {
        try {
            _jobjRec!!.put(field_name, field_val)
            val field = _jobjRec!!.getString(field_name)
        } catch (ex: JSONException) {
            return
        }
    }

    fun setField(field_name: String, field_val: Int) {
        try {
            _jobjRec!!.put(field_name, field_val)
            val field = _jobjRec!!.getString(field_name)
        } catch (ex: JSONException) {
            return
        }
    }

    fun setField(field_name: String, field_val: Double) {
        try {
            _jobjRec!!.put(field_name, field_val)
            val field = _jobjRec!!.getString(field_name)
        } catch (ex: JSONException) {
            return
        }
    }

    fun getFieldString(field_name: String): String {
        return try {
            if ( _jobjRec == null) return "";
            _jobjRec!!.getString(field_name)
        } catch (ex: JSONException) {
            ""
        }
    }

    fun getFieldInteger(field_name: String): Int {
        return try {
            if ( _jobjRec == null) return 0;
            _jobjRec!!.getInt(field_name)
        } catch (ex: JSONException) {
            0
        }
    }

    fun getFieldDouble(field_name: String): Double {
        return try {
            if ( _jobjRec == null) return 0.0;
            _jobjRec!!.getDouble(field_name)
        } catch (ex: JSONException) {
            0.0
        }
    }

    fun getFieldBoolean(field_name: String): Boolean {
        return try {
            if ( _jobjRec == null) return false;

            _jobjRec!!.getBoolean(field_name)
        } catch (ex: JSONException) {
            false
        }
    }
    fun getRow(): String {
        return String.format("%d", _row)
    }

}