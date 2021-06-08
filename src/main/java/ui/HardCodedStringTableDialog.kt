package ui

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import stringFinder.HardCodedStringFinder
import stringFinder.JavaAndKotlinHardCodedStringFinder
import stringFinder.LayoutXmlHardCodedStringFinder
import utils.*
import utils.Constants.ELContextFileName
import utils.Constants.baseAppContext
import java.awt.event.ActionEvent
import javax.swing.*


class HardCodedStringTableDialog(project: Project, fileList: List<VirtualFile>, stringXMLFile: VirtualFile, frame: JFrame, baseApplicationFile: VirtualFile) : JDialog() {
    var stringsTable: JTable? = null
    var mainPanel: JPanel? = null
    var convertHardCodedStringsButton: JButton? = null

    init {
        if (stringsTable?.model != null) {
            stringsTable?.model = RenderDataToTable(null)
        }
        contentPane = mainPanel
        val entries = processAllFilesAndGetEntries(project, fileList, stringXMLFile)
        stringsTable?.model = RenderDataToTable(entries)
        stringsTable?.isEnabled = true

        convertHardCodedStringsButton?.addActionListener(object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {

                val stringsToAddInStringXMLFile = StringBuilder()
                val isContextCodeAdded = addContextCodeInBaseApplicationFile(baseApplicationFile)
                if (isContextCodeAdded) {
                    createElContextFile(project, baseApplicationFile)
                    ApplicationManager.getApplication().invokeLater {
                        ApplicationManager.getApplication().runReadAction {
                            entries.filter { it.isSelected }.forEach {
                                if (it.virtualFile.fileType == XmlFileType.INSTANCE) {
                                    replaceHardCodedXMLStrings(it, stringsToAddInStringXMLFile)
                                } else {
                                    var fileContent = FileProcessor.readFileContent(it.virtualFile)
                                    if (it.virtualFile.fileType == Utils.kotlinFileType) {
                                        fileContent = fileContent.replaceFirst("\"${it.value}\"", "${ELContextFileName}.${baseAppContext}?.getString(R.string.${it.key})")
                                    } else if (it.virtualFile.fileType == Utils.javaFileType) {
                                        fileContent = fileContent.replaceFirst("\"${it.value}\"", "${ELContextFileName}.get${baseAppContext.capitalize()}().getString(R.string.${it.key})")
                                    }
                                    stringsToAddInStringXMLFile.appendIfNotContains("\n\t<string name=\"${it.key}\">${it.value}</string>")
                                    FileProcessor.writeFileContent(it.virtualFile, fileContent)
                                }
                            }
                            updateStringXMLFile(stringXMLFile, stringsToAddInStringXMLFile)
                            frame.dispose()
                        }
                    }
                }
            }
        })
    }

    private fun updateStringXMLFile(stringXMLFile: VirtualFile, stringsToAddInStringXMLFile: StringBuilder) {

        val stringXMLFileContent = FileProcessor.readFileContent(stringXMLFile)
        val stringBuilder = StringBuilder(stringXMLFileContent.substringBeforeLast("</resources>"))
        stringBuilder.append(stringsToAddInStringXMLFile).append("\n</resources>")

        FileProcessor.writeFileContent(stringXMLFile, stringBuilder.toString())
    }

    private fun processAllFilesAndGetEntries(project: Project, files: List<VirtualFile>, stringXMLFile: VirtualFile): ArrayList<HardcodedStringEntryModel> {
        val entries = arrayListOf<HardcodedStringEntryModel>()
        var hardCodedStringFinder: HardCodedStringFinder?
        for (virtualFile in files) {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            if (psiFile != null && psiFile.isWritable) {
                hardCodedStringFinder = if (psiFile.name.endsWith(".xml"))
                    LayoutXmlHardCodedStringFinder()
                else
                    JavaAndKotlinHardCodedStringFinder()
                val hardcodedTexts = hardCodedStringFinder.findHardCodedStrings(psiFile)
                entries.addAll(StringResourcesProcessor.process(hardcodedTexts, virtualFile, stringXMLFile))
            }
        }
        return entries
    }

    private fun replaceHardCodedXMLStrings(hardCodedStringEntry: HardcodedStringEntryModel, stringsToAddInStringXMLFile: StringBuilder) {
        var content = FileProcessor.readFileContent(hardCodedStringEntry.virtualFile)
        when {
            content.contains("android:text=\"${hardCodedStringEntry.value}\"") -> {
                content = content.replaceFirst("android:text=\"${hardCodedStringEntry.value}\"", "android:text=\"@string/${hardCodedStringEntry.key}\"")
                stringsToAddInStringXMLFile.appendIfNotContains("\n\t<string name=\"${hardCodedStringEntry.key}\">${hardCodedStringEntry.value}</string>")
            }
            content.contains("android:title=\"${hardCodedStringEntry.value}\"") -> {
                content = content.replaceFirst("android:title=\"${hardCodedStringEntry.value}\"", "android:title=\"@string/${hardCodedStringEntry.key}\"")
                stringsToAddInStringXMLFile.appendIfNotContains("\n\t<string name=\"${hardCodedStringEntry.key}\">${hardCodedStringEntry.value}</string>")
            }
            content.contains("android:hint=\"${hardCodedStringEntry.value}\"") -> {
                content = content.replaceFirst("android:hint=\"${hardCodedStringEntry.value}\"", "android:hint=\"@string/${hardCodedStringEntry.key}\"")
                stringsToAddInStringXMLFile.appendIfNotContains("\n\t<string name=\"${hardCodedStringEntry.key}\">${hardCodedStringEntry.value}</string>")
            }
            content.contains("android:subTitle=\"${hardCodedStringEntry.value}\"") -> {
                content = content.replaceFirst("android:subTitle=\"${hardCodedStringEntry.value}\"", "android:subTitle=\"@string/${hardCodedStringEntry.key}\"")
                stringsToAddInStringXMLFile.appendIfNotContains("\n\t<string name=\"${hardCodedStringEntry.key}\">${hardCodedStringEntry.value}</string>")
            }
        }
        if (hardCodedStringEntry.virtualFile.path.contains("/res/layout") || hardCodedStringEntry.virtualFile.path.contains("/res/menu")) {
            FileProcessor.writeFileContent(hardCodedStringEntry.virtualFile, content)
        }
    }

    private fun addContextCodeInBaseApplicationFile(baseApplicationFile: VirtualFile): Boolean {
        var fileContent = FileProcessor.readFileContent(baseApplicationFile)

        when (baseApplicationFile.fileType) {
            Utils.javaFileType -> {
                if (fileContent.contains("${ELContextFileName}.setContext(this)")) {
                    return true
                }
                val addInOnCreate = "super.onCreate();\n" +
                        "\t\t${ELContextFileName}.setContext(this);\n"
                if (fileContent.contains("super.onCreate();")) {
                    fileContent = fileContent.replaceFirst("super.onCreate();", addInOnCreate)
                    FileProcessor.writeFileContent(baseApplicationFile, fileContent)
                } else {
                    return false
                }
                return true
            }
            Utils.kotlinFileType -> {
                if (fileContent.contains("${ELContextFileName}.${baseAppContext}")) {
                    return true
                }
                if (fileContent.contains("super.onCreate()")) {

                    val addInOnCreate = "super.onCreate();\n" +
                            "       \t\t${ELContextFileName}.${baseAppContext} = this\n"
                    fileContent = fileContent.replaceFirst("super.onCreate()", addInOnCreate)
                    FileProcessor.writeFileContent(baseApplicationFile, fileContent)
                } else {
                    return false
                }
                return true
            }
            else -> return false
        }
    }

    private fun createElContextFile(project: Project, baseApplicationFile: VirtualFile) {
        val directory = PsiManager.getInstance(project).findFile(baseApplicationFile)?.containingDirectory
        val factory = PsiFileFactory.getInstance(directory?.project)
        val baseApplicationFileContent = FileProcessor.readFileContent(baseApplicationFile)

        val contextFile = Utils.getKotlinFileFromPathName(ELContextFileName, project)
        if (contextFile == null) {
            var packageName = baseApplicationFileContent.substringBefore("\n")
            packageName = packageName.split("\r").get(0).replace(";", "")

            val file: PsiFile = Utils.kotlinFileType?.let {
                if (packageName.isBlank()) {
                    packageName = "import EasyLokalPlugin"
                }
                factory.createFileFromText("$ELContextFileName.kt", it,
                        packageName +
                                "\n" +
                                "import android.content.Context\n" +
                                "\n" +
                                "object $ELContextFileName {\n" +
                                "\n" +
                                "    @JvmStatic\n" +
                                "    var $baseAppContext: Context? = null\n" +
                                "}")
            } ?: return
            directory?.add(file)
            LocalPersistenceUtils.setData(Constants.PREFERENCES.IMPORT_PACKAGE, "import ${packageName.removePrefix("package")}.${ELContextFileName};")

        }

    }

    fun StringBuilder.appendIfNotContains(str: String) {
        if (!this.contains(str)) {
            this.append(str)
        }
    }

}