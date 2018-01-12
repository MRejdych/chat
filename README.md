# Projekt - Czat internetowy  

## Instrukcja użytkownika:
### Wymagania systemowe
W celu uruchomienia aplikacji system musi posiadać zainstalowane  [Java Runtime Environment 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) lub nowsze.

###Uruchomienie aplikacji
Pierwszym krokiem jest uruchomienie serwera aplikacji.  
W tym celu należy uruchomić z poziomu IDE metodę main() klasy ChatServer.java lub uruchomić server 
z poziomu terminala wywołując komendę "./gradlew runServer --console plain" z poziomu katalogu głównego projektu.  

Po uruchomieniu serwera należy wybrać jeden z dostępnych adresów ip oraz podać port, pod którym serwer ma oczekiwać połączeń.  


![RunServer](https://github.com/MRejdych/chat/blob/master/imgs/runServer.png)  


Następnie należy uruchomić aplikację-klienta uruchamiając z poziomu IDE metodę main() klasy ChatClient.java lub 
uruchomić ją z poziomu terminala wywołując komendę "./gradlew runClient --console plain" z poziomu katalogu głównego projektu. 

Po uruchomieniu aplikacji-klienta należy podać w interfejsie graficznym adres ip oraz port, na którym
 nasłuchuje serwer aplikacji.  
 
![RunClient](https://github.com/MRejdych/chat/blob/master/imgs/runClient.png)  

Następnie należy się zalogować jako dodany wcześniej użytkownik lub dodać nowego użytkownika.    


![Login](https://github.com/MRejdych/chat/blob/master/imgs/login.png)  

Nazwa użytkownika powinna składać się z małych lub dużych liter oraz cyfr i mieć długość od 3 do 15 znaków.  
  
![Registration](https://github.com/MRejdych/chat/blob/master/imgs/registration.png)    
  
Po zalogowaniu się do aplikacji zobaczymy okno, w którym widnieje lista zarejestrowanych użytkowników.  
Aby rozpocząć konwersację z aktualnie zalogowanym użytkownikiem wystarczy dwukrotnie kliknąć jego nazwę.  
  
![Conversation](https://github.com/MRejdych/chat/blob/master/imgs/conversation.png)

## Cel projektu:
Projekt ma na celu zademonstrowanie przykładowego zastosowania języka programowania Java w implementowaniu systemów rozproszonych.  


## Wykorzystane technologie:
Projekt został zaimplementowany z użyciem języka Java, w szczególności wykorzystane zostały [Java Sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html) 
w celu stworzenia architektury klient-serwer oraz [Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/components/index.html) 
w celu stworzenia interfejsu użytkownika.  

[Apache commons-lang3](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/package-summary.html) - biblioteka zawierająca narzędzia w postaci statycznych metod 
realizujących powszechne zadania wykonywane w Javie w celu uproszczenia procesu tworzenia oprogramowania.  

[JSON](http://json.org/) - lekki format wymiany danych komputerowych wykorzystany w aplikacji pełniącej 
rolę serwera w celu przechowywania danych o użytkownikach.  

[Gradle Build Tool](https://gradle.org/) - narzędzie wykorzystane w celu automatyzacji procesu budowania aplikacji.  



