Rabbi.pl
========================

Aplikacja zawarta na stronie http://rabbi.pl napisana w całości w języku scala.
jest to strona PWA stworzona podczas Hackateonu u Dominikanów w Krakowie.

uruchomienie aplikacji
-----------------------------------

aby skompilować kod:

```
sbt run app/fastOptJS
``` 

A następnie uruchomić `index.html` w przeglądarce.


testowanie aplikacji
-----------------------------------
aby uruchomić testy:

```
sbt run app/test
``` 

musisz mieć zainstalowanego node.js i node-dom

```bash
#instalacja noda
nvm install 5.0 

#instalacja dom
npm install jsdom
```

więcej na https://www.scala-js.org/doc/project/js-environments.html

release
-------------------
TODO