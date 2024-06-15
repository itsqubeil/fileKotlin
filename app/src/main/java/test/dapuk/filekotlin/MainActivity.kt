package test.dapuk.filekotlin

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnCreateFile: Button
    private lateinit var lvFiles: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var toolbar: Toolbar

    private val CREATE_FILE_REQUEST_CODE = 1
    private val EDIT_FILE_REQUEST_CODE = 2

    private var actionMode: ActionMode? = null
    private var isMultiSelect = false
    private val selectedItems = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        btnCreateFile = findViewById(R.id.btnCreateFile)
        lvFiles = findViewById(R.id.lvFiles)

        btnCreateFile.setOnClickListener {
            val intent = Intent(this, CreateFileActivity::class.java)
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
        }

        loadAndSortFilesList()

        lvFiles.setOnItemClickListener { _, _, position, _ ->
            if (isMultiSelect) {
                toggleSelection(position)
            } else {
                val fileName = lvFiles.getItemAtPosition(position) as String
                editFile(fileName.substringBefore(" - Last modified:"))
            }
        }

        lvFiles.setOnItemLongClickListener { _, _, position, _ ->
            if (!isMultiSelect) {
                selectedItems.clear()
                isMultiSelect = true
                if (actionMode == null) {
                    actionMode = startActionMode(actionModeCallback)
                }
            }
            toggleSelection(position)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CREATE_FILE_REQUEST_CODE, EDIT_FILE_REQUEST_CODE -> {
                    loadAndSortFilesList()
                }
            }
        }
    }

    private fun loadAndSortFilesList() {
        val externalFilesDir = getExternalFilesDir(null)
        val fileList = mutableListOf<String>()

        if (externalFilesDir != null) {
            val files = externalFilesDir.listFiles()

            files?.let {
                val fileLastModifiedList = mutableListOf<Pair<File, Long>>()

                for (file in it) {
                    if (file.isFile && file.name.endsWith(".txt")) {
                        fileLastModifiedList.add(Pair(file, file.lastModified()))
                    }
                }

                fileLastModifiedList.sortByDescending { it.second }

                for ((file, lastModified) in fileLastModifiedList) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val formattedDate = sdf.format(Date(lastModified))
                    fileList.add("${file.name} - Last modified: $formattedDate")
                }
            }
        } else {
        }

        adapter = object : ArrayAdapter<String>(this, R.layout.list_item, R.id.tvFileName, fileList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                return view
            }
        }

        lvFiles.adapter = adapter
    }

    private fun editFile(fileName: String) {
        val intent = Intent(this, EditFileActivity::class.java)
        intent.putExtra("fileName", fileName)
        startActivityForResult(intent, EDIT_FILE_REQUEST_CODE)
    }

    private fun deleteSelectedItems() {
        val externalFilesDir = getExternalFilesDir(null)

        for (fileName in selectedItems) {
            val file = File(externalFilesDir, fileName.substringBefore(" - Last modified:"))
            if (file.exists()) {
                file.delete()
            }
        }

        actionMode?.finish()
        loadAndSortFilesList()
    }

    private fun toggleSelection(position: Int) {
        val selectedItem = lvFiles.getItemAtPosition(position) as String
        if (selectedItems.contains(selectedItem)) {
            selectedItems.remove(selectedItem)
        } else {
            selectedItems.add(selectedItem)
        }

        if (selectedItems.isEmpty()) {
            actionMode?.finish()
        } else {
            actionMode?.title = "${selectedItems.size} selected"
        }

        adapter.notifyDataSetChanged()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.context_menu, menu)
            supportActionBar?.hide()
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.menu_delete -> {
                    deleteSelectedItems()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isMultiSelect = false
            selectedItems.clear()
            actionMode = null
            adapter.notifyDataSetChanged()
            supportActionBar?.show() }
    }
}
