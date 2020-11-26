package org.jetbrains.plugins.scala.compilationCharts

import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.compilationCharts.ui.CompilationChartsComponentHolder
import org.jetbrains.plugins.scala.compiler.{CompilerEvent, CompilerEventListener}

private class UpdateCompilationProgressListener(project: Project)
  extends CompilerEventListener {

  override def eventReceived(event: CompilerEvent): Unit = {
    val timestamp = System.nanoTime()
    val state = CompilationProgressStateManager.get(project)
    val newStateOption = for {
      compilationUnitId <- event.compilationUnitId
      newState <- Option(event).collect {
        case _: CompilerEvent.CompilationStarted =>
          state.put(compilationUnitId, CompilationProgressInfo(
            startTime = timestamp,
            finishTime = None,
            updateTime = timestamp,
            progress = 0.0
          ))
        case CompilerEvent.ProgressEmitted(_, _, progress) =>
          state.updateLast(compilationUnitId)(_.copy(
            updateTime = timestamp,
            progress = progress
          ))
        case _: CompilerEvent.CompilationFinished =>
          state.updateLast(compilationUnitId)(_.copy(
            finishTime = Some(timestamp),
            updateTime = timestamp,
            progress = 1.0
          ))
      }
    } yield newState
    newStateOption.foreach { newState =>
      CompilationProgressStateManager.update(project, newState)
      val component = CompilationChartsComponentHolder.createOrGet(project)
      component.updateData()
      component.repaint()
    }
  }
}
