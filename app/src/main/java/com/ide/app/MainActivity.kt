package com.ide.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var rvFiles: RecyclerView
    private lateinit var tvCurrentPath: TextView
    private lateinit var tvConsole: TextView
    private lateinit var svConsole: android.widget.ScrollView
    private lateinit var fileAdapter: FileAdapter

    private lateinit var btnRun: Button
    private lateinit var btnNewFile: Button
    private lateinit var btnNewFolder: Button
    private lateinit var btnRename: Button
    private lateinit var btnDelete: Button

    private lateinit var currentDir: File
    private var selectedFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvFiles = findViewById(R.id.rvFiles)
        tvCurrentPath = findViewById(R.id.tvCurrentPath)
        tvConsole = findViewById(R.id.tvConsole)
        svConsole = findViewById(R.id.svConsole)

        btnRun = findViewById(R.id.btnRun)
        btnNewFile = findViewById(R.id.btnNewFile)
        btnNewFolder = findViewById(R.id.btnNewFolder)
        btnRename = findViewById(R.id.btnRename)
        btnDelete = findViewById(R.id.btnDelete)

        currentDir = filesDir

        fileAdapter = FileAdapter(emptyList(), { file ->
            if (file.name == "..") {
                val parent = currentDir.parentFile
                if (parent != null) {
                    currentDir = parent
                    selectedFile = null
                    updateFileList()
                }
            } else if (file.isDirectory) {
                currentDir = file
                selectedFile = null
                updateFileList()
            } else {
                selectedFile = file
                logToConsole("Selected file: ${file.name}")
                Toast.makeText(this, "Selected: ${file.name}", Toast.LENGTH_SHORT).show()
            }
        }, { file ->
            if (file.name != "..") {
                selectedFile = file
                logToConsole("Long clicked: ${file.name}")
                Toast.makeText(this, "Long clicked: ${file.name}", Toast.LENGTH_SHORT).show()
            }
        })
        rvFiles.layoutManager = LinearLayoutManager(this)
        rvFiles.adapter = fileAdapter

        updateFileList()
        logToConsole("IDE initialized.")

        btnRun.setOnClickListener {
            logToConsole("Build started...")
            // TODO: Implement actual build logic
            logToConsole("Build completed.")
        }

        btnNewFile.setOnClickListener {
            showInputDialog("New File", "Enter file name:") { fileName ->
                val newFile = File(currentDir, fileName)
                try {
                    if (newFile.createNewFile()) {
                        logToConsole("File created: $fileName")
                        updateFileList()
                    } else {
                        logToConsole("File already exists: $fileName")
                    }
                } catch (e: Exception) {
                    logToConsole("Error creating file: ${e.message}")
                }
            }
        }

        btnNewFolder.setOnClickListener {
            showInputDialog("New Folder", "Enter folder name:") { folderName ->
                val newFolder = File(currentDir, folderName)
                if (newFolder.mkdir()) {
                    logToConsole("Folder created: $folderName")
                    updateFileList()
                } else {
                    logToConsole("Failed to create folder: $folderName")
                }
            }
        }

        btnRename.setOnClickListener {
            val fileToRename = selectedFile
            if (fileToRename == null) {
                logToConsole("No file selected to rename.")
                return@setOnClickListener
            }

            showInputDialog("Rename", "Enter new name:", fileToRename.name) { newName ->
                val newFile = File(currentDir, newName)
                if (fileToRename.renameTo(newFile)) {
                    logToConsole("Renamed to: $newName")
                    selectedFile = null
                    updateFileList()
                } else {
                    logToConsole("Failed to rename file.")
                }
            }
        }

        btnDelete.setOnClickListener {
            val fileToDelete = selectedFile
            if (fileToDelete == null) {
                logToConsole("No file selected to delete.")
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete ${fileToDelete.name}?")
                .setPositiveButton("Yes") { _, _ ->
                    if (fileToDelete.deleteRecursively()) {
                        logToConsole("Deleted: ${fileToDelete.name}")
                        selectedFile = null
                        updateFileList()
                    } else {
                        logToConsole("Failed to delete file.")
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showInputDialog(title: String, message: String, defaultText: String = "", onConfirm: (String) -> Unit) {
        val input = EditText(this)
        input.setText(defaultText)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    onConfirm(text)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateFileList() {
        tvCurrentPath.text = "Path: ${currentDir.absolutePath}"
        val files = mutableListOf<File>()

        if (currentDir.parentFile != null) {
            files.add(File(currentDir, ".."))
        }

        val dirFiles = currentDir.listFiles()?.toList()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
        files.addAll(dirFiles)

        fileAdapter.updateFiles(files)
    }

    private fun logToConsole(message: String) {
        val currentText = tvConsole.text.toString()
        val newText = if (currentText.isEmpty()) message else "$currentText\n$message"
        tvConsole.text = newText
        svConsole.post {
            svConsole.fullScroll(android.widget.ScrollView.FOCUS_DOWN)
        }
    }
}
