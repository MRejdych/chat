# Projekt - Czat internetowy
## Wykorzystane technologie:
Projekt został zaimplementowany z użyciem języka Java, w szczególności wykorzystane zostały Java Sockets 
w celu stworzenia architektury klient-serwer.

[Java Tutorial - All about sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html)  

## Instrukcja użytkownika:
### Wymagania systemowe
W celu uruchomienia aplikacji system musi posiadać zainstalowany [Java Runtime Environment 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) lub nowszy.


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