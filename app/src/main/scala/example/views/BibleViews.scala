package example.views

import example.AudioPlayer
import example.model.{Bible, BibleFile, BibleTestament, Book}
import org.scalajs.dom.html.Div
import example.utils.Implicits._
import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery

object View {
  val stid = Bible.ot.name.hashCode
  val ntid = Bible.nt.name.hashCode
}

class InfoView {
  val view: Div = {
    div(
      div(cls := "infoParagraph",
        div(
          cls := "shortIcon",
          backgroundColor := "#419DCC",
          p("B", cls := "shortBook")
        ),
        div(cls := "infoContent",
          p("""
            |Witaj na stronie zawierającej Biblię w formie plików audio.
            |Dzięki temu możesz wsłuchiwać się w słowa Biblii w dowolnym miejscu, dysponując komputerem lub odtwarzaczem MP3.
            |Nagrania zawierają Biblię w przekładzie znanym jako Biblia Tysiąclecia -
            |będącym oficjalnym tłumaczeniem liturgicznym Kościoła Katolickiego w Polsce.
            """.stripMargin
          )
        )
      ),
      div(cls := "infoParagraph",
        div(
          cls := "shortIcon",
          backgroundColor := "#419DCC",
          p("M", cls := "shortBook")
        ),
        div(cls := "infoContent",
          p("""
              |Pliki zostały znalezione w internecie, a nagrane zostały przez Bibliotekę Centralną Polskiego Związku Niewidomych w 1982 roku.
              |Nagranie udostępniamy za zgodą Biblioteki. Serdecznie dziękujemy pani Dyrektor Biblioteki Teresie Dederko za życzliwość i za umożliwienie nam tego zadania.
            """.stripMargin)
        )
      ),
      div(cls := "infoParagraph",
        div(
          cls := "shortIcon",
          backgroundColor := "#419DCC",
          p("P", cls := "shortBook")
        ),
        div(cls := "infoContent",
          p("""Poszczególne księgi publikowana są w dwóch wersjach - cala księga jako jeden (czasem bardzo długi) plik oraz księga podzielona na kilka plików, o długości 30-40 min
              |(dla osób, które nie mogą pozwolić sobie na komfort przesłuchania za jednym razem całej księgi). Zapraszamy do słuchania - ewentualne uwagi proszę przekazywać na adres kuba@choinski.pl.
            """.stripMargin)
        )
      ),
      div(cls := "infoParagraph",
        div(
          cls := "shortIcon",
          backgroundColor := "#419DCC",
          p("3", cls := "shortBook")
        ),
        div(cls := "infoContent",
          p("""Jeśli chcesz mieć na swoim komputerze/odtwarzaczu całą Biblię, możesz pobrać jeden plik (archiwum zip) zawierający całość nagrania. Uwaga - plik ma 1,2 GB i jego pobranie może potrwać zależnie od szybkości łącza) - plik dostępny jest tutaj -
            """.stripMargin, a(href := "http://biblia-mp3.pl/biblia-cala.zip", "Cała Biblia w postaci archiwum ZIP")
          )
        )
      )
    ).render
  }
}

class BibleViews {
  val view: Div = {
    div(
      cls := "tabContainer",
      ul(cls := "nav nav-tabs",
        li(cls := "testTab", a(data.toggle := "tab", href := "#"+View.stid, "Stary Testament")),
        li(cls := "testTab active", a(data.toggle := "tab", href := "#"+View.ntid, "Nowy Testament")),
        li(cls := "infoTab", a(data.toggle := "tab",
          padding := "1.18em 0",
          href := "#app-info",
          span(cls := "fa fa-info-circle fa-2x")
          )
        )
      )
    ).render
  }
}

class BibleTestamentView(b:BibleTestament, colors:Seq[String]) {
  val colorMapping =
    b.books.groupByOrdered(_.group).map(_._1).zip(colors).toMap
    .withDefaultValue("gray")

  val booksViews = b.books.map(s => new BookView(s, colorMapping(s.group)))

  val view: Div = {
    div(
      id := b.name.hashCode,
      cls := "tab-pane",
      booksViews.map(_.view)
    ).render
  }
}

case class BookView(b:Book, color:String) {
  val fileViews = b.files.map(s => new BibleFileView(s))

  val view: JsDom.TypedTag[Div] = {
    val cid = "chapter-" + b.short.hashCode
    val chapters = div(
      id:=cid,
      cls:="collapse",
      fileViews.map(_.view)
    ).render

    val header = div(
      data.toggle := "collapse",
      data.target := "#" + cid,
      cls := "bookHeader collapsed",
      div(cls := "shortIcon", backgroundColor := color,
        p(b.short, cls := "shortBook")
      ),
      div(cls := "bookName", p(b.name, cls := "bookNameText")),
      div(clear := "both")
    ).render

    div(
      header,
      chapters,
      p("", clear := "both", margin := "0 0")
    )
  }
}


class BibleFileView(val b:BibleFile) {
  val icon = i(cls := "fa fa-play").render

  def setPlaying(a:Option[Boolean]) = {
    val playing = a.getOrElse(false)
    view.classList.toggle("selected", a.isDefined)
    icon.classList.toggle("fa-play", !playing)
    icon.classList.toggle("fa-pause", playing)
  }

  val view: Div = {
    div(cls :="chapter",
      button(cls := "btnPlay", icon),
      p(b.versionPartName, cls := "book"),
      div(cls := "durationBox",
        i(cls := "fa fa-clock-o", aria.hidden := "true"),
        p(b.name, cls := "duration")
      ),
      onclick := { () => AudioPlayer.toggle(b)() },
      p("", clear := "both", margin := "0 0")
    ).render
  }
}
