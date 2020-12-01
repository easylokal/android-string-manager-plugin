package stringFinder

import com.intellij.psi.PsiFile
import utils.FileProcessor
import utils.FileProcessor.readFileContent

abstract class HardCodedStringFinder {

    fun findHardCodedStrings(psiFile: PsiFile): List<String> {

        FileProcessor.saveAllFile()

        val content = readFileContent(psiFile.virtualFile)
        val result = regex().findAll(content)

        return result
                .map { extractHardCodedString(it.value) }
                .filter { shouldInclude(it) }
                .toList()

    }

    abstract fun shouldInclude(it: String): Boolean

    protected abstract fun extractHardCodedString(it: String): String

    protected abstract fun regex(): Regex

}