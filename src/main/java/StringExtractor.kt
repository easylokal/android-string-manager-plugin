import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ui.PreferenceDialog
import javax.swing.JFrame


class StringExtractor : AnAction(), PreferenceDialog.CloseDialogListener {

    private val frame = JFrame()

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project? = event.getData(PlatformDataKeys.PROJECT)
        val editor: Editor? = event.getData(PlatformDataKeys.EDITOR)
        if (project == null || editor == null) {
            return
        }
        createAndShowFrames(project)
    }

    private fun createAndShowFrames(project: Project) {
        frame.contentPane.add(PreferenceDialog(project, this).jpanel)
        frame.pack()
        frame.isVisible = true
    }

    override fun closeDialog() {
        frame.dispose()
    }
}


