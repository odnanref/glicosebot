import java.io.File

import scala.concurrent.Future
import javax.inject._
import play.Logger
import play.api.inject.ApplicationLifecycle

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject() (lifecycle: ApplicationLifecycle) {

  val property = "java.io.tmpdir"
  val reportpath = System.getProperty(property) + "/glicosereports"

  val ap_reps = new File(reportpath)
  if (!ap_reps.exists() || !ap_reps.isDirectory) {
    val cdir = ap_reps.mkdir()
    if (!cdir) {
      Logger.warn("Failed creating " + reportpath)
      throw new Exception("Failed creating attachment directory " + reportpath)
    }
  }

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }

}