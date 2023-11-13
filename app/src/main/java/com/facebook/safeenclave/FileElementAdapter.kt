package com.facebook.safeenclave

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class FileElementAdapter(private val context: Activity, private val files: ArrayList<FileStruct>) : ArrayAdapter<FileStruct>(context, R.layout.file_element, files) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.file_element, null)

        val name = view.findViewById<TextView>(R.id.file_name)
        name.text = files[position].name

        val openButton = view.findViewById<ImageButton>(R.id.open_button)

        openButton.setOnClickListener {
            println("WANTED TO OPEN " + files[position].name + " AT " + files[position].path)
        }

//        return super.getView(position, convertView, parent)
        return view
    }

    fun readFile(filePath: String): ByteArray {
        val file = File(filePath)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return fileContents
    }

    fun saveFile(fileData: ByteArray, path: String) {
        val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()
    }

    fun encrypt(key: String, fileData: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
        return cipher.doFinal(fileData)
    }

    fun decrypt(key: String, fileData: ByteArray): ByteArray {
        val sKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.DECRYPT_MODE, sKey, IvParameterSpec(ByteArray(cipher.getBlockSize())))
        return cipher.doFinal(fileData)
    }
}