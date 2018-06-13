package rabbi.views

import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

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


