package com.sf.cwms.util

import android.util.Log

class UserLog {
    companion object {
        val program_log = "pda_log"

        fun userLog(msg: String?) {
            Log.d(program_log, msg!!)
        }

        fun userLog(msg: String?, user_val: Int) {
            val str = String.format("%s>%d", msg, user_val)
            Log.d(program_log, str)
        }
    }
}