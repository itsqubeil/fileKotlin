package test.dapuk.filekotlin

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreateFileActivity : AppCompatActivity() {

    private lateinit var etFileName: EditText
    private lateinit var etFileContent: EditText
    private lateinit var btnSave: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_file)

        etFileName = findViewById(R.id.etFileName)
        etFileContent = findViewById(R.id.etFileContent)
        btnSave = findViewById(R.id.btnSave)
        toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)

        btnSave.setOnClickListener {
            saveFile()
        }
    }

    private fun saveFile() {
        val fileName = etFileName.text.toString().trim()
        val fileContent = etFileContent.text.toString()

        val externalFilesDir = getExternalFilesDir(null)
        val file = File(externalFilesDir, "$fileName.txt")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            fos.write(fileContent.toByteArray())
            fos.close()

            setResult(RESULT_OK)
            finish()

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
