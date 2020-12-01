package utils

import com.intellij.openapi.vfs.VirtualFile
import ui.HardcodedStringEntryModel
import utils.FileProcessor.readFileContent

/*  This class will be able to process all types of file i.e. kotlin, java , xml files and replace all hard-coded strings
*/

object StringResourcesProcessor {

    fun process(hardcodedStrings: List<String>, virtualFile: VirtualFile, stringXMLFile: VirtualFile): List<HardcodedStringEntryModel> {
        val keysToAddInStringXML = mutableListOf<String>()
        val prefix = LocalPersistenceUtils.getData(Constants.PREFERENCES.PREFIX) ?: "str_"
        val entries = mutableListOf<HardcodedStringEntryModel>()
        if (virtualFile.name.endsWith(".xml")) {
            for (stringResourceValue in hardcodedStrings) {
                val stringXMLContent = readFileContent(stringXMLFile)
                val stringResourceKey = getKeyForStringResource(stringXMLContent, prefix + stringResourceValue, 0, keysToAddInStringXML)
                keysToAddInStringXML.add(stringResourceKey)
                entries.add(HardcodedStringEntryModel(stringResourceKey, stringResourceValue, true, "", virtualFile))
            }
        } else if (virtualFile.path.contains("/main/java")) {
            val extractTemplate = Constants.javaExtractTemplate
            for (stringResourceValue in hardcodedStrings) {
                val stringXMLContent = readFileContent(stringXMLFile)
                val stringResourceKey = getKeyForStringResource(stringXMLContent, prefix + stringResourceValue, 0, keysToAddInStringXML)
                keysToAddInStringXML.add(stringResourceKey)
                entries.add(HardcodedStringEntryModel(stringResourceKey, stringResourceValue, true, extractTemplate, virtualFile))
            }
        }
        return entries
    }

    private fun getKeyForStringResource(stringsXMLFileContent: String, originalText: String, repeatCount: Int, stringsToAddInStringXMLFile: MutableList<String>): String {
        val transformedText = Constants.REGEX.KEY_GENERATOR_REGEX.replace(originalText, "").replace(" ", "_")

        if (stringsXMLFileContent.contains(transformedText) || stringsToAddInStringXMLFile.contains(transformedText)) {
            return getKeyForStringResource(stringsXMLFileContent, transformedText.plus("_" + repeatCount + 1), repeatCount + 1, stringsToAddInStringXMLFile)
        }
        return transformedText
    }


}