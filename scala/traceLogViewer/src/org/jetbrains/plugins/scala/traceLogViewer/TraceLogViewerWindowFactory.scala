package org.jetbrains.plugins.scala.traceLogViewer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.{DumbAware, Project}
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.content.{ContentManagerEvent, ContentManagerListener}

class TraceLogViewerWindowFactory extends ToolWindowFactory with DumbAware {
  override def init(toolWindow: ToolWindow): Unit = {
    toolWindow.setStripeTitle("Scala Trace Log Viewer")
  }

  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {
    toolWindow.setToHideOnEmptyContent(true)
    val contentManager = toolWindow.getContentManager
    contentManager.addContent(TraceLogSelectionView.create())
    contentManager.addContentManagerListener(new ContentManagerListener {
      override def contentRemoved(event: ContentManagerEvent): Unit = {
        if (event.getContent.getTabName == TraceLogSelectionView.displayName) {
          contentManager.addContent(TraceLogSelectionView.create(), 0)
        }
      }
    })
  }

  override def shouldBeAvailable(project: Project): Boolean =
    ApplicationManager.getApplication.isInternal
}

object TraceLogViewerWindowFactory {
}