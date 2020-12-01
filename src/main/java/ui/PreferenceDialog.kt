package ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import utils.Constants
import utils.Constants.ELContextFileName
import utils.Constants.baseAppContext
import utils.FileProcessor
import utils.LocalPersistenceUtils
import utils.Utils
import utils.Utils.getAllLayoutXMLFiles
import utils.Utils.getAllSrcJavaFiles
import utils.Utils.getAllSrcKotlinFiles
import utils.Utils.getJavaFile
import utils.Utils.getKotlinFile
import utils.Utils.getStringXMLFiles
import javax.swing.*

class PreferenceDialog(project: Project, closeDialogListener: CloseDialogListener) : DialogWrapper(project) {
    var jpanel: JPanel? = null
    var infoLable: JLabel? = null
    var prefixTextField: JTextField? = null
    var applicationClassTextField: JTextField? = null
    var submitButton: JButton? = null
    var hardCodedStringTableDialog: JFrame? = null


    init {

        prefixTextField?.text = LocalPersistenceUtils.getData(Constants.PREFERENCES.PREFIX)
        applicationClassTextField?.text = LocalPersistenceUtils.getData(Constants.PREFERENCES.APPLICATION_CLASS_NAME)
        infoLable?.text = null

        submitButton?.addActionListener {
            val prefix = prefixTextField?.text?.trim()
            val baseApplicationClassName = applicationClassTextField?.text?.trim() ?: ""

            if (prefix.isNullOrBlank()) {
                infoLable?.text = Constants.LABELS.EMPTY_PREFIX_FIELD
            } else if (prefix.isNullOrBlank()) {
                infoLable?.text = Constants.LABELS.INVALID_APPLICATION_NAME_FIELD
            } else {
                val javaVirtualFile = getJavaFile(baseApplicationClassName, project)
                val kotlinVirtualFile = getKotlinFile(baseApplicationClassName, project)

                if ((javaVirtualFile != null || kotlinVirtualFile != null) && isValidApplicationClass(javaVirtualFile, kotlinVirtualFile)) {
                    LocalPersistenceUtils.setData(Constants.PREFERENCES.PREFIX, prefix)
                    LocalPersistenceUtils.setData(Constants.PREFERENCES.APPLICATION_CLASS_NAME, baseApplicationClassName)

                    val allFiles = arrayListOf<VirtualFile>()
                    allFiles.addAll(getAllLayoutXMLFiles(project))
                    allFiles.addAll(getAllSrcJavaFiles(project) ?: emptyList())
                    allFiles.addAll(getAllSrcKotlinFiles(project) ?: emptyList())

                    if (javaVirtualFile != null) {
                        infoLable?.text = Constants.LABELS.LOADING_STRINGS
                        fetchAndShowStrings(project, allFiles, getStringXMLFiles(project), javaVirtualFile)
                    } else if (kotlinVirtualFile != null) {
                        infoLable?.text = Constants.LABELS.LOADING_STRINGS
                        fetchAndShowStrings(project, allFiles, getStringXMLFiles(project), kotlinVirtualFile)
                    }
                    closeDialogListener.closeDialog()
                } else
                    infoLable?.text = Constants.LABELS.INVALID_APPLICATION_NAME_FIELD
            }

        }


    }

    private fun isValidApplicationClass(javaVirtualFile: VirtualFile?, kotlinVirtualFile: VirtualFile?): Boolean {
        return if (javaVirtualFile != null)
            checkOnCreateExistInFile(javaVirtualFile)
        else if (kotlinVirtualFile != null)
            checkOnCreateExistInFile(kotlinVirtualFile)
        else
            false
    }

    private fun checkOnCreateExistInFile(virtualFile: VirtualFile): Boolean {
        val fileContent = FileProcessor.readFileContent(virtualFile)

        return when (virtualFile.fileType) {
            Utils.javaFileType -> {
                fileContent.contains("${ELContextFileName}.setContext(this)") || fileContent.contains("super.onCreate();")
            }
            Utils.kotlinFileType -> {
                fileContent.contains("${ELContextFileName}.${baseAppContext}") || fileContent.contains("super.onCreate()")
            }
            else -> false
        }

    }

    private fun fetchAndShowStrings(project: Project, fileList: List<VirtualFile>, stringXMLFile: VirtualFile, appBaseClass: VirtualFile) {
        if (hardCodedStringTableDialog != null && hardCodedStringTableDialog!!.isVisible) {
            hardCodedStringTableDialog?.dispose()
        }
        hardCodedStringTableDialog = JFrame()
        hardCodedStringTableDialog?.contentPane?.add(HardCodedStringTableDialog(project, fileList, stringXMLFile, hardCodedStringTableDialog!!, appBaseClass).mainPanel)
        hardCodedStringTableDialog?.pack()
        hardCodedStringTableDialog?.setLocationRelativeTo(null)
        hardCodedStringTableDialog?.isVisible = true
    }

    override fun createCenterPanel() = jpanel

    interface CloseDialogListener {
        fun closeDialog()
    }

}
