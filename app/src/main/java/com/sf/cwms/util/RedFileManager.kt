package com.sf.cwms.util

import android.R
import android.content.Context
import android.os.Environment
import java.io.*
import java.util.*


class RedFileManager {
    companion object {
        // is File Exists
        fun fileExists(filename: String?): Boolean {
            return File(filename).exists()
        }
        //get Charset from a File
        fun getCharset(file: File?): String? {
            var charset = "GBK" // ?????
            val first3Bytes = ByteArray(3)
            try {
                var checked = false
                val bis = BufferedInputStream( FileInputStream(file) )
                bis.mark(0)
                var read: Int = bis.read(first3Bytes, 0, 3)
                if (read == -1) return charset
                if (first3Bytes[0] == 0xFF.toByte()
                    && first3Bytes[1] == 0xFE.toByte()
                ) {
                    charset = "UTF-16LE"
                    checked = true
                } else if (first3Bytes[0] == 0xFE.toByte()
                    && first3Bytes[1] == 0xFF.toByte()
                ) {
                    charset = "UTF-16BE"
                    checked = true
                } else if (first3Bytes[0] == 0xEF.toByte() && first3Bytes[1] == 0xBB.toByte() && first3Bytes[2] == 0xBF.toByte()) {
                    charset = "UTF-8"
                    checked = true
                }
                bis.reset()
                if (!checked) {
                    var loc = 0
                    while (bis.read().also { read = it } != -1) {
                        loc++
                        if (read >= 0xF0) break
                        //??????BF???????GBK
                        if (0x80 <= read && read <= 0xBF) break
                        if (0xC0 <= read && read <= 0xDF) {
                            read = bis.read()
                            if (0x80 <= read && read <= 0xBF) // ????? (0xC0 - 0xDF)
                            // (0x80 -
                            // 0xBF),??????GB????
                                continue else break
                            // ????????????????
                        } else if (0xE0 <= read && read <= 0xEF) {
                            read = bis.read()
                            if (0x80 <= read && read <= 0xBF) {
                                read = bis.read()
                                if (0x80 <= read && read <= 0xBF) {
                                    charset = "UTF-8"
                                    break
                                } else break
                            } else break
                        }
                    }
                    println(loc.toString() + " " + Integer.toHexString(read))
                }
                bis.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return charset
        }
        // get File Stream Encoding - Android File Input Output
        @Throws(IOException::class)
        fun getStreamEncoding(`is`: InputStream?): String? {
            val bis = BufferedInputStream(`is`)
            bis.mark(2) //from www  .j ava 2s .  c  om
            val first3bytes = ByteArray(3)
            bis.read(first3bytes)
            bis.reset()
            var encoding: String? = null
            encoding =
                if (first3bytes[0] == 0xEF.toByte() && first3bytes[1] == 0xBB.toByte() && first3bytes[2] == 0xBF.toByte()) {
                    "utf-8"
                } else if (first3bytes[0] == 0xFF.toByte()
                    && first3bytes[1] == 0xFE.toByte()
                ) {
                    "unicode"
                } else if (first3bytes[0] == 0xFE.toByte()
                    && first3bytes[1] == 0xFF.toByte()
                ) {
                    "utf-16be"
                } else if (first3bytes[0] == 0xFF.toByte()
                    && first3bytes[1] == 0xFF.toByte()
                ) {
                    "utf-16le"
                } else {
                    "GBK"
                }
            return encoding
        }

        // traverse Dir and return a list of file
        fun traverseDir(dirPath: String?): LinkedList<File>? {
            val files: LinkedList<File> = LinkedList<File>()
            val dir = File(dirPath)
            var file = dir.listFiles()
            for (aFile in file) {
                if (aFile.isDirectory) {
                    files.add(aFile) /*w ww  .  j  av  a 2 s  .c o m*/
                }
            }
            for (aFile in files) {
                files.removeFirst()
                if (aFile.isDirectory) {
                    file = aFile.listFiles()
                    for (tFile in file) {
                        files.add(tFile)
                    }
                }
            }
            return files
        }

        // getRootFilePath
        fun getRootFilePath(context: Context): String? {
            return if (hasSDCard()) {
                //return Environment.getExternalStorageDirectory().getAbsolutePath();// filePath:/sdcard/android
                context.getExternalCacheDir()
                    ?.getAbsolutePath() // filePath:/sdcard/android/+packageName+/cache
            } else {
                context.getCacheDir().getAbsolutePath() // filePath: /data/data/
            }
        }

        fun hasSDCard(): Boolean {
            val status: String = Environment.getExternalStorageState()
            return if (status != Environment.MEDIA_MOUNTED) {
                false
            } else true
        }

        public fun getDownloadDir(context: Context): File? {
            return if (isExternalStorageWritable()) context
                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) else context.getFilesDir()
        } //from  w  ww .j  a  va  2 s.co m


        public fun isExternalStorageWritable(): Boolean {
            val state: String = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED.equals(state)
        }

        fun makeDirectory(dirPath: String?): Boolean {
            val file = File(dirPath)
            return file.mkdirs() || file.isDirectory
        }

        fun exportSystemFolder(context: Context?) {
            val packageName = R::class.java.getPackage().name
            val systemFolder = File("/data/data/$packageName")
            if (!systemFolder.exists()) {
                return
            } /*ww  w  . ja  va 2 s  . c o  m*/
            try {
                copyFolder(
                    systemFolder, File(
                        "/sdcard/debug_folder/"
                                + packageName
                    )
                )
                UserLog.userLog("Export system file success.")
            } catch (e: IOException) {
                e.printStackTrace()
                UserLog.userLog("export system file fail.")
            }
        }

        @Throws(IOException::class)
        private fun copyFolder(fromFolder: File, toFolder: File) {
            if (!fromFolder.exists()) {
                return
            }
            if (toFolder.exists()) {
                deleteFileAndFolder(toFolder)
            }
            copyFolder(fromFolder.absolutePath, toFolder.absolutePath)
        }

        @Throws(IOException::class)
        fun copyFolder(sourceDir: String, targetDir: String) {
            (File(targetDir)).mkdirs()
            val file = (File(sourceDir)).listFiles()
            for (i in file.indices) {
                if (file[i].isFile) {
                    val sourceFile = file[i]
                    val targetFile = File(
                        (File(targetDir).absolutePath
                                + File.separator + file[i].name)
                    )
                    copyFile(sourceFile, targetFile)
                }
                if (file[i].isDirectory) {
                    val dir1 = sourceDir + "/" + file[i].name
                    val dir2 = targetDir + "/" + file[i].name
                    copyFolder(dir1, dir2)
                }
            }
        }

        private fun deleteFileAndFolder(fileOrFolder: File?) {
            if (fileOrFolder == null || !fileOrFolder.exists()) {
                return
            }
            if (fileOrFolder.isDirectory) {
                val children = fileOrFolder.listFiles()
                if (children != null) {
                    for (childFile: File in children) {
                        deleteFileAndFolder(childFile)
                    }
                }
            } else {
                fileOrFolder.delete()
            }
        }

        private fun copyFile(from: File, to: File) {
            if (!from.exists()) {
                return
            }
            var `in`: FileInputStream? = null
            var out: FileOutputStream? = null
            try {
                `in` = FileInputStream(from)
                out = FileOutputStream(to)
                val bt = ByteArray(1024)
                var c: Int
                while ((`in`.read(bt).also { c = it }) > 0) {
                    out.write(bt, 0, c)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                try {
                    `in`?.close()
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }


    }

}