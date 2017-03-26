package com.demonwav.despector

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.PlatformIcons
import org.jetbrains.java.decompiler.IdeaDecompiler
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JTabbedPane
import javax.swing.SwingConstants

class DecompiledTextEditorProvider : FileEditorProvider, DumbAware {

    override fun getEditorTypeId() = "DECOMPILED-TEXT-EDITOR-TYPE-ID"
    override fun accept(project: Project, file: VirtualFile) = file.fileType == JavaClassFileType.INSTANCE

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val factory = EditorFactory.getInstance()
        val first = factory.createEditor(factory.createDocument(file.decompile(true)), project, JavaFileType.INSTANCE, true)
        val second = factory.createEditor(factory.createDocument(file.decompile(false)), project, JavaFileType.INSTANCE, true)

        return TabbedFileEditor(first, second)
    }

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}

private fun VirtualFile.decompile(isDespector: Boolean): CharSequence {
    return if (isDespector) {
        DespectorFileDecompiler().getText(this)
    } else {
        IdeaDecompiler().getText(this)
    }
}

class TabbedFileEditor(firstEditor: Editor, secondEditor: Editor) : FileEditor {

    val pane: JBTabbedPane = JBTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT)

    init {
        pane.insertTab("Despector", PlatformIcons.CLASS_ICON, firstEditor.component, "Decompiled by Despector", 0)
        pane.insertTab("FernFlower", PlatformIcons.CLASS_ICON, secondEditor.component, "Decompile by FernFlower", 1)

        pane.selectedComponent = firstEditor.component
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getName(): String {
        return "Decompiled Class"
    }

    override fun setState(state: FileEditorState) {}

    override fun getComponent(): JComponent {
        return pane
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return pane
    }

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return null
    }

    override fun selectNotify() {}

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun deselectNotify() {}

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? {
        return null
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun dispose() {}
}
