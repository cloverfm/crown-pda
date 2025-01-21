package com.sf.kpaint.util

import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException

class ApiPublicMask(addr:String) {
    /*
    var addr: String

    init{
        this.addr = addr
    }

    var lats = ArrayList<String>()
    var lngs = ArrayList<String>()
    var name = ArrayList<String>()
    var adapter : ListViewAdapter = ListViewAdapter()
    val storesURL = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/stores/json"
    val salesURL = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/sales/json"
    val storesbyaddrURL = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByAddr/json"

    fun main(): ListViewAdapter{

        var text: String? = null
        try {
            text = URLEncoder.encode(addr, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("검색어 인코딩 실패", e)
        }

        val url = "$storesbyaddrURL?address=$text"
        val responseBody = get(url)
        parseData(responseBody)

        return adapter
    }

    private operator fun get(apiUrl: String): String {
        var responseBody: String = ""
        try {
            val url = URL(apiUrl)
            val `in` = url.openStream()
            responseBody = readBody(`in`)

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return responseBody
    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)

        try {
            BufferedReader(streamReader).use({ lineReader ->
                val responseBody = StringBuilder()

                var line: String? = lineReader.readLine()
                while ( line != null) {
                    responseBody.append(line)
                    line = lineReader.readLine()
                }
                return responseBody.toString()
            })
        } catch (e: IOException) {
            throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }

    }

    private fun parseData(responseBody: String) {
        var storeName: String
        var remain_stat: String
        var lat: String
        var lng: String
        var jsonObject = JSONObject()
        try {
            jsonObject = JSONObject(responseBody)
            val jsonArray = jsonObject.getJSONArray("stores")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                storeName = item.getString("name")
                remain_stat = item.getString("remain_stat")
                lat = item.getString("lat")
                lng = item.getString("lng")
                name.add(storeName)
                lats.add(lat)
                lngs.add(lng)
                adapter.addItem(storeName, remain_stat)

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

     */
}