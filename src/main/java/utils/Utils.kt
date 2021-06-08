package utils

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

object Utils {

    val javaFileType = Language.findLanguageByID("JAVA")?.associatedFileType
    val kotlinFileType = Language.findLanguageByID("kotlin")?.associatedFileType

    fun getAllLayoutXMLFiles(project: Project) = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.allScope(project)).filter { it.path.contains("/src/main/res/layout") || it.path.contains("/src/main/res/menu") }
    fun getAllSrcJavaFiles(project: Project) = javaFileType?.let { FileTypeIndex.getFiles(it, GlobalSearchScope.allScope(project)).filter { it.path.contains(Constants.PATH.SOURCE_CODE_PATH) } }
    fun getAllSrcKotlinFiles(project: Project) = kotlinFileType?.let { FileTypeIndex.getFiles(it, GlobalSearchScope.allScope(project)).filter { it.path.contains(Constants.PATH.SOURCE_CODE_PATH) } }


    fun getJavaFile(fileName: String, project: Project): VirtualFile? {
        return getAllSrcJavaFiles(project)?.firstOrNull { it.name == fileName || it.name == "$fileName.java" }
    }

    fun getKotlinFile(fileName: String, project: Project): VirtualFile? {
        return getAllSrcKotlinFiles(project)?.firstOrNull { it.name == fileName || it.name == "$fileName.kt" }
    }

    fun getKotlinFileFromPathName(fileName: String, project: Project): VirtualFile? {
        return getAllSrcKotlinFiles(project)?.firstOrNull { it.path.contains(fileName) || it.name == "$fileName.kt" }
    }

    fun getStringXMLFiles(project: Project): VirtualFile {
        return FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.allScope(project)).first { it.path.contains(Constants.PATH.STRINGS_XML_PATH)  }
    }
}