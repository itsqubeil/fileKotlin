package test.dapuk.filekotlin

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import test.dapuk.filekotlin.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class EditFileActivity : AppCompatActivity() {

    private lateinit var etEditContent: EditText
    private lateinit var etFileName: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var toolbar: Toolbar

    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_file)

        etFileName = findViewById(R.id.etFileName)
        etEditContent = findViewById(R.id.etEditContent)
        btnSaveChanges = findViewById(R.id.btnSave)
        toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)

        fileName = intent.getStringExtra("fileName") ?: ""
        val fileNameWithoutExt = fileName.substringBeforeLast(".")
        etFileName.setText(fileNameWithoutExt)

        loadFileContent(fileName)

        btnSaveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadFileContent(fileName: String) {
        val externalFilesDir = getExternalFilesDir(null)
        val file = File(externalFilesDir, fileName)

        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val scanner = java.util.Scanner(fis)

            val sb = StringBuilder()
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n")
            }
            etEditContent.setText(sb.toString())

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveChanges() {
        val newFileName = etFileName.text.toString().trim()
        val fileContent = etEditContent.text.toString()

        val externalFilesDir = getExternalFilesDir(null)
        val oldFile = File(externalFilesDir, fileName)
        val newFile = File(externalFilesDir, "$newFileName.txt")

        var fos: FileOutputStream? = null
        try {
            if (oldFile.renameTo(newFile)) {
                fos = FileOutputStream(newFile)
                fos.write(fileContent.toByteArray())
                fos.close()

                setResult(RESULT_OK)
                finish()
            } else {

            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
