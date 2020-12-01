package utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object FileProcessor {

    fun saveAllFile() = FileDocumentManager.getInstance().saveAllDocuments()


    fun writeFileContent(file: VirtualFile, content: String) {
        ApplicationManager.getApplication().runWriteAction {
            val outputStream = file.getOutputStream(this)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.write(addPackageImportIfNotAlready(file, content))
            writer.close()
        }
    }

    private fun addPackageImportIfNotAlready(file: VirtualFile, content: String): String {
        if ((file.fileType == Utils.javaFileType || file.fileType == Utils.kotlinFileType)) {
            val fileContent = readFileContent(file)
            val importPackageString = LocalPersistenceUtils.getData(Constants.PREFERENCES.IMPORT_PACKAGE)
            if (importPackageString != null && !fileContent.contains(importPackageString)) {
                return content.replaceFirst("\n", "\n" + importPackageString + "\n")
            }
        }
        return content
    }

    fun readFileContent(file: VirtualFile): String {
        val reader = BufferedReader(InputStreamReader(file.inputStream))
        val builder = StringBuilder()
        reader.forEachLine {
            builder.appendln(it)
        }
        reader.close()
        return builder.toString()
    }

}