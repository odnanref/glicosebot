package service

import java.time.{ZoneOffset, ZonedDateTime}

import com.google.inject.Inject
import facebook.server.message.{Attachment, Sender, UserProfile}
import facebook.{FacebookRecipient, FacebookRequest, JsonMessage, ShopConfig}
import models.daos.GlicoseDAO
import models.entities.Glicose
import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import play.api.mvc._
import java.io._

import facebook.server.message.payload.AttachmentFilePayload
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

class GlicoseService @Inject()(glicoseDAO: GlicoseDAO, fconfig: ShopConfig)
                              (implicit ex : ExecutionContext) {

  val fr = new FacebookRequest(fconfig)

  def deleteLast(sender: Sender): Unit = {
    glicoseDAO.deleteLastByUserId(sender.id.toLong)
  }

  def removeUser(id: Long): Unit = {
    glicoseDAO.deleteByUserId(id).onComplete{
      case Success(x) => {
        fr.sendMessage(
          new JsonMessage(
            new FacebookRecipient(id),
            "Os seus dados foram removidos."
          )
        )
      }
      case Failure(x) => {
        Logger.error("Failure happen Error removing user : " + id + " " + x.getMessage )
        fr.sendMessage(
          new JsonMessage(
            new FacebookRecipient(id),
            "Falha a tentar remover os seus dados."
          )
        )
      }
    }
  }

  def getReportByDay(rep : Seq[Glicose]) : Map[LocalDate, Seq[String]] = {
    rep.groupBy(_.datein.toLocalDate).map { case (k, v) => k -> v.map(_.inputed) }
  }

  def buildReport(rep: Seq[Glicose], userProfile: UserProfile): Unit = {
    val reportByDay = this.getReportByDay(rep)
    // generated date
    val localDate = LocalDate.now()
    val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
    val formattedString = formatter.print(localDate)

    val page = views.html.report("Gerado em " + formattedString, userProfile, reportByDay)

    val property = "java.io.tmpdir"
    val tempDir = System.getProperty(property) + "/glicosereports"

    val filepath = tempDir + File.separator + "glicose_" + userProfile.id + ".html"

    val file = new File(filepath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(page.toString())
    bw.close()

    if(file.exists()) {
      val run :String = if (new File("/usr/local/wkhtmltox/bin/wkhtmltopdf").exists()) {
        "/usr/local/wkhtmltox/bin/wkhtmltopdf"
      } else {
        "/usr/bin/wkhtmltopdf"
      }

      import sys.process._
        Seq(run,
          "--load-error-handling","ignore", "--header-left",
          "Registo de glicose para " + userProfile.first_name + " " + userProfile.last_name,
          "--header-line" , "--footer-right",
          "Pág. [page] de [toPage]",
          "--footer-left", "https://www.facebook.com/glicosebot/",
          filepath, filepath.replace(".html", ".pdf")).!

      Logger.info("Generated PDF for user=" + userProfile.id + " " + filepath.replace(".html", ".pdf"))
      // Send it
      sendReportAttachment("glicose_" + userProfile.id + ".pdf", userProfile)
    }
  }

  def sendReportAttachment(file: String, userProfile: UserProfile) : Unit = {
    val fbr = new FacebookRequest(this.fconfig)
    val attachment = new Attachment("file", new AttachmentFilePayload("https://" + System.getenv("DOMAIN") + "/attachments/" + file ))
    // conscience: OH dear! Are you going to leave this url statically like this in the code?
    // me: Yes!
    val jmsg = new JsonMessage(new FacebookRecipient(userProfile.id.toLong), "O seu relatório.", Some(attachment))
    fbr.sendMessage(jmsg)
  }

  def sendReport(user: UserProfile): Unit = {
   glicoseDAO.getReportByUserProfileId(user.id.toLong).onComplete{
     case Success(rep) => buildReport(rep, user)
     case Failure(x) => {
       Logger.error("Failure getting report from database for " + user.id + " " + x.getMessage)
       fr.sendMessage(new JsonMessage(new FacebookRecipient(user.id.toLong), "Falha a gerar relatório iremos analizar o que ocorreu."))

       // TODO email error or something
     }
   }
  }

  def saveLastMeal(lastmeal: String, userProfileId: Long): Unit = {
    glicoseDAO.updateLastMeal(lastmeal, userProfileId)
  }

  def saveGlicose(x: String, userProfile: UserProfile) : Unit = {

    val calctime = new DateTime().withZone(DateTimeZone.forOffsetHours(userProfile.timezone))
    val tmp = Glicose(0, x, calctime, userProfile.id.toLong)

    Logger.debug("Saving to database " + tmp )

    glicoseDAO.insert(tmp).onComplete {
      case Success(x) => {
        fr.sendMessage(
          new JsonMessage(
            new FacebookRecipient(userProfile.id.toLong),
            "O seu valor foi salvo com o ID=" + x + " valor =" + tmp.inputed + " Data=" + tmp.datein
          )
        )
      }
      case Failure(x) => {
        Logger.error("Error saving " + tmp.inputed + " in " + tmp.datein + " user " + tmp.userProfileId + "\n" + x.getMessage )
        fr.sendMessage(new JsonMessage(new FacebookRecipient(userProfile.id.toLong), "Falha a gravar o valor.\n Tente novamente por favor.\nVamos analizar o que ocorreu."))

        // TODO email error or something
      }
    }
  }
}
