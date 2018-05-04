Rabbi.pl
========================

Aplikacja zawarta na stronie Rabbi.pl.

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