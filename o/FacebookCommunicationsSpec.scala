import facebook.server.message._
import facebook.server.message.actions.{Button, ButtonPayload, ButtonWebUrl, DefaultAction}
import facebook._
import org.scalatestplus.play._
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class FacebookCommunicationsSpec extends PlaySpec with OneAppPerTest {

  "Attachments" should {

    "Transform to a nicely json object" in  {
      val attach = new Attachment("template", new AttachmentPayload("list"))

      val da = new DefaultAction("web_url", "http://somewhere.com?view?id=1010", true, "http://somewhere.com?fallback&id=1010")
      val button = new ButtonWebUrl("Buy now", "http://somewhere.com?buynow=1010", "http://somewhere.com?fallback&id=1010&buynow=1")
      val button2 = new ButtonWebUrl("Keep in basket", "http://somewhere.com?keep=1010", "http://somewhere.com?fallback&id=1010&keep=1")
      if (attach.payload.elements.isEmpty) {
        attach.payload.elements = Some(new ListBuffer[Element]())
        attach.payload.elements.get += new Element("Teste", "noimage.jpeg", "Sub teste", da , Seq(button, button2) )
      }

      attach.payload.buttons = Some(new ListBuffer[Button]())
      attach.payload.buttons.get += new ButtonPayload("Ver mais", "DEV_VIEW_MORE") // FIXME Payload string

      val builtValue = Json.toJson(attach).toString()
      val returnedValue = """{"type":"template","payload":{"template_type":"list","top_element_style":"compact","elements":[{"title":"Teste","image_url":"noimage.jpeg","subtitle":"Sub teste","default_action":{"type":"web_url","url":"http://somewhere.com?view?id=1010","messenger_extensions":true,"fallback_url":"http://somewhere.com?fallback&id=1010"},"buttons":[{"title":"Buy now","type":"web_url","url":"http://somewhere.com?buynow=1010","messenger_extensions":true,"fallback_url":"http://somewhere.com?fallback&id=1010&buynow=1"},{"title":"Keep in basket","type":"web_url","url":"http://somewhere.com?keep=1010","messenger_extensions":true,"fallback_url":"http://somewhere.com?fallback&id=1010&keep=1"}]}],"buttons":[{"title":"Ver mais","type":"postback","payload":"DEV_VIEW_MORE"}]}}"""
      assert(builtValue.equals(returnedValue))
    }

    "build an attachment of type image payload" in {
      val message = """{"object":"page","entry":[{"id":"779844352066452","time":1504558897591,"messaging":[{"sender":{"id":"1159527677474031"},"recipient":{"id":"779844352066452"},"timestamp":1504558897371,"message":{"mid":"mid.$cAAKfsLZjhfFkf_gg21eTrJlBznt7","seq":119779,"attachments":[{"type":"image","payload":{"url":"https://scontent-frx5-1.xx.fbcdn.net/v/t35.0-12/21390349_1684751244876852_1625832257_o.jpg?_nc_ad=z-m&oh=4886aaedcc47b42001001f219863113b&oe=59B04581"}}]}}]}]}"""
      val jsonAttach = Json.parse(message)

      //val req = FakeRequest("POST", "/glicosebot").withJsonBody(jsonAttach)

      val fparser = new FBodyParser(jsonAttach)
      val a :Seq[Attachment] = fparser.getAttachments()
      val jsonAttachment = Json.toJson(a)
      val expectedResult = """[{"type":"image","payload":{"url":"https://scontent-frx5-1.xx.fbcdn.net/v/t35.0-12/21390349_1684751244876852_1625832257_o.jpg?_nc_ad=z-m&oh=4886aaedcc47b42001001f219863113b&oe=59B04581"}}]"""

      assert(
        jsonAttachment.toString().equals(expectedResult)
      )
    }

  }

  "messages" should {
    "Interpret currency execution" in {
      val msg = """{"object":"page","entry":[{"id":"779844352055452","time":1504198411030,"messaging":[{"sender":{"id":"1159527688474031"},"recipient":{"id":"779844352077452"},"timestamp":1504198410786,"message":{"mid":"mid.$cAAKfsLZjhfFkanuKIleOTZBn6D-d","seq":119234,"text":"currency"}}]}]}"""
      val jsonMsg = Json.parse(msg)
      val fparser = new FBodyParser(jsonMsg)

      assert((fparser.getMessages().size == 1) && fparser.getMessages().head.text.trim().equals("currency"))
    }

    "attachment of an image" in {
      val message = """{"object":"page","entry":[{"id":"779844352066452","time":1504558897591,"messaging":[{"sender":{"id":"1159527677474031"},"recipient":{"id":"779844352066452"},"timestamp":1504558897371,"message":{"mid":"mid.$cAAKfsLZjhfFkf_gg21eTrJlBznt7","seq":119779,"attachments":[{"type":"image","payload":{"url":"https://scontent-frx5-1.xx.fbcdn.net/v/t35.0-12/21390349_1684751244876852_1625832257_o.jpg?_nc_ad=z-m&oh=4886aaedcc47b42001001f219863113b&oe=59B04581"}}]}}]}]}"""
      val jsonAttach = Json.parse(message)

      val fparser = new FBodyParser(jsonAttach)

      assert(
        fparser.getAttachments().head
          .payload
          .url
          .equals("https://scontent-frx5-1.xx.fbcdn.net/v/t35.0-12/21390349_1684751244876852_1625832257_o.jpg?_nc_ad=z-m&oh=4886aaedcc47b42001001f219863113b&oe=59B04581")
      )
    }

    "postback on a sent event" in {
      val message = """{"object":"page","entry":[{"id":"779844352066452","time":1504727238592,"messaging":[{"recipient":{"id":"779844352066452"},"timestamp":1504727238592,"sender":{"id":"1159527677474031"},"postback":{"payload":"VIEW_MORE","title":"Ver mais"}}]}]}"""
      val jsonAttach = Json.parse(message)

      val fparser = new FBodyParser(jsonAttach)
      val postbacks = fparser.getPostbacks()
      println("----> " + postbacks)
      assert(postbacks.head.payload.equals("VIEW_MORE"))
    }

    "postback is empty" in {
      val message = """{"object":"page","entry":[{"id":"779844352066452","time":1504558897591,"messaging":[{"sender":{"id":"1159527677474031"},"recipient":{"id":"779844352066452"},"timestamp":1504558897371,"message":{"mid":"mid.$cAAKfsLZjhfFkf_gg21eTrJlBznt7","seq":119779,"attachments":[{"type":"image","payload":{"url":"https://scontent-frx5-1.xx.fbcdn.net/v/t35.0-12/21390349_1684751244876852_1625832257_o.jpg?_nc_ad=z-m&oh=4886aaedcc47b42001001f219863113b&oe=59B04581"}}]}}]}]}"""
      val jsonAttach = Json.parse(message)

      val fparser = new FBodyParser(jsonAttach)
      assert(fparser.getPostbacks().size == 0)
    }

    "LIVE send welcome message to user " in {
      val shopConfig = new ShopConfig(
        1, // storeId
        System.getenv("PAGE_ACCESS_TOKEN"),
        // size from total items returned should be limited to 4 items due to rules of facebook messenger
        "https://botshop-backend-java.herokuapp.com/stores/search/findByKeywordIgnoreCase?page=<page>&size=4&id=1&keyword=<text>", // Search
        Some("https://botshop-backend-java.herokuapp.com/stores/1/buy/?id=<ID>"), // Buy
        Some("https://botshop-backend-java.herokuapp.com/products/<ID>"),// View
        Some("https://botshop-backend-java.herokuapp.com/stores/fallbackUrl?id=<ID>"),// Fallback
        Some("6f77453b-6027-4971-b6d0-6033a5f5287c")
      )

      val ent = UserProfile("1159527677474031", "Fernando", "Andre", "https://image","pt_PT", -3, "male")

      val fr = new FacebookRequest(shopConfig)
      fr.sendMessage(new JsonMessage(new FacebookRecipient(ent.id.toLong), "TESTE DE ENVIO...Bem vindo " + ent.first_name))
    }

  }
}
