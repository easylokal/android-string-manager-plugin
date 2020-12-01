package stringFinder

import utils.Constants

class LayoutXmlHardCodedStringFinder : HardCodedStringFinder() {

    override fun shouldInclude(it: String): Boolean {
        return !it.contains("@string/") && !it.matches(Constants.REGEX.DATA_BINDING_VARIABLE_REGEX)
    }

    override fun extractHardCodedString(it: String): String {

        val result = when {
            it.contains("android:title") -> {
                it.replace("android:title=\"", "")
            }
            it.contains("android:hint") -> {
                it.replace("android:hint=\"", "")
            }
            it.contains("android:subTitle") -> {
                it.replace("android:subTitle=\"", "")
            }
            else -> {
                it.replace("android:text=\"", "")
            }
        }
        return result.replace("\"", "")
    }

    override fun regex() = Regex("android:text=\".*?\"" + "|" + "android:title=\".*?\"" + "|" + "android:hint=\".*?\"" + "|" + "android:subTitle=\".*?\"")

}