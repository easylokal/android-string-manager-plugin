package ui

import com.intellij.openapi.vfs.VirtualFile

data class HardcodedStringEntryModel(
        var key: String,
        var value: String,
        var isSelected: Boolean,
        var extractTemplate: String = "",
        var virtualFile: VirtualFile
)