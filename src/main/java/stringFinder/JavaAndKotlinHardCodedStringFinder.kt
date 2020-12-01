package stringFinder

class JavaAndKotlinHardCodedStringFinder : HardCodedStringFinder() {

    override fun shouldInclude(it: String): Boolean {
        return it.isNotBlank()
    }

    override fun regex() = Regex("\".*?\"")

    override fun extractHardCodedString(it: String) = it.replace("\"", "")

}