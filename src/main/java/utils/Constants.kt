package utils

object Constants {
    var baseAppContext = "context"
    var ELContextFileName = "ELContext"

    var javaExtractTemplate = "getString(\$id)"

    object PREFERENCES {
        const val EL_PREFERENCES = "EL_PREFERENCES"
        const val PREFIX = "PREFIX"
        const val IMPORT_PACKAGE = "IMPORT_PACKAGE"
        const val APPLICATION_CLASS_NAME = "APPLICATION_CLASS_NAME"
    }

    object LABELS {
        const val EMPTY_PREFIX_FIELD = "Please enter a valid prefix"
        const val INVALID_APPLICATION_NAME_FIELD = "Please enter a valid application class name"
        const val LOADING_STRINGS = "Loading Strings...This may take some time."
    }

    object PATH {
        const val SOURCE_CODE_PATH = "app/src/main/java"
        const val STRINGS_XML_PATH = "app/src/main/res/values/strings.xml"
    }

    object REGEX {
        val KEY_GENERATOR_REGEX = Regex("[^A-Za-z0-9-_ ]")
        val DATA_BINDING_VARIABLE_REGEX = Regex("@\\{.*}")
    }
}

