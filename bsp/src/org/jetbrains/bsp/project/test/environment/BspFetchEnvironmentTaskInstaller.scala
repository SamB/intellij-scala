package org.jetbrains.bsp.project.test.environment

import com.intellij.execution.{RunManagerEx, RunManagerListener, RunnerAndConfigurationSettings}
import com.intellij.openapi.project.Project

class BspFetchEnvironmentTaskInstaller(project: Project) extends RunManagerListener {
  override def runConfigurationAdded(settings: RunnerAndConfigurationSettings): Unit = {
    val runManager = RunManagerEx.getInstanceEx(project)
    val runConfiguration = settings.getConfiguration
    if (BspTesting.isBspRunnerSupportedConfiguration(runConfiguration)) {
      val beforeRunTasks = runManager.getBeforeRunTasks(runConfiguration)
      val task = new BspFetchTestEnvironmentTask
      task.setEnabled(true)
      beforeRunTasks.add(task)
      val tasks = runManager.getBeforeRunTasks(BspFetchTestEnvironmentTask.runTaskKey)
      if (tasks.isEmpty) {
        runManager.setBeforeRunTasks(runConfiguration, beforeRunTasks)
      }
    }
  }
}
