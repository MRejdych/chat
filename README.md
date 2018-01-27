# Projekt - Czat internetowy  

## Instrukcja użytkownika:
### Wymagania systemowe
W celu uruchomienia aplikacji system musi posiadać zainstalowane  [Java Development Kit 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) lub nowsze.

### Uruchomienie aplikacji
Pierwszym krokiem jest uruchomienie serwera aplikacji.  
W tym celu należy uruchomić z poziomu IDE metodę main() klasy ChatServer.java lub uruchomić server 
z poziomu terminala wywołując komendę "./gradlew runServer --console plain" z poziomu katalogu głównego projektu.  

Po uruchomieniu serwera należy wybrać jeden z dostępnych adresów ip oraz podać port, pod którym serwer ma oczekiwać połączeń.  

<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/runServer.png?raw=true"" />
</p>  

Następnie należy uruchomić aplikację-klienta uruchamiając z poziomu IDE metodę main() klasy ChatClient.java lub 
uruchomić ją z poziomu terminala wywołując komendę "./gradlew runClient --console plain" z poziomu katalogu głównego projektu. 

Po uruchomieniu aplikacji-klienta należy podać w interfejsie graficznym adres ip oraz port, na którym
 nasłuchuje serwer aplikacji.  
   
<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/runClient.png?raw=true"" />
</p>  

Następnie należy się zalogować jako dodany wcześniej użytkownik lub dodać nowego użytkownika.    

<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/login.png?raw=true"" />
</p>  

Nazwa użytkownika powinna składać się z małych lub dużych liter oraz cyfr i mieć długość od 3 do 15 znaków.  
  
<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/registration.png?raw=true"" />
</p>  
    
Po zalogowaniu się do aplikacji zobaczymy okno, w którym widnieje lista zarejestrowanych użytkowników.  
Aby rozpocząć konwersację z aktualnie zalogowanym użytkownikiem wystarczy dwukrotnie kliknąć jego nazwę.  
  

<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/conversation.png?raw=true"" />
</p>    

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

[Gradle Build Tool](https://gradle.org/) - narzędzie wykorzystane w celu automatyzacji procesu budowania i uruchamiania aplikacji.  

## List wymagań odnośnie aplikacji:
### Wymagania funkcjonalne:
- Możliwość dodawania unikalnych użytkowników.
- Baza dodanych użytkowników nie jest tracona podczas ponownego uruchomienia systemu.
- Użytkownik może wysłać wiadomość do innego użytkownika aplikacji.
- Użytkownik  po zalogowaniu się może zobaczyć listę pozostałych użytkowników.
- Możliwość pracy z wieloma klientami czatu jednocześnie.
- Klienci czatu mogą łączyć się z serwerem uruchomionym na innej maszynie.
- W dowolonym momencie liczba klientów może wzrosnąć lub zmaleć, co nie powinno zaburzyć poprawnego funkcjonowania systemu.
### Wymagania niefunkcjonalne:
- Aplikacja klienca musi posiadać łatwy w obsłudze interfejs graficzny.
- Możliwość uruchomienia aplikacji na systemie Linux oraz Windows.


## Główni aktorzy:
- Użytkownik końcowy wchodzący w interakcję poprzez interfejs graficzny z aplikacją pełniącą rolę klienta.
- Administrator serwera wchodzący z nim w interakcję poprzez wiersz poleceń.   

## Przypadki użycia:
### Diagram przypadków użycia
<p align="center">
    <img src="https://github.com/MRejdych/chat/blob/master/imgs/use-case.png?raw=true"" />
</p>  


## Architektura systemu:  

System został zaimplementowany w oparciu o architekturę klient-serwer.  
Serwer nasłuchuje na wybranym porcie żądania połączenia ze strony klienta.  
Po nadejściu takiego żądania Serwer oraz Klient tworzą obiekty typu Socket komunikujące się ze sobą poprzez 
przypisany im port wybrany z puli dostępnych portów.  
Po połączeniu się z klientem serwer nasłuchuje żądania połączenia od kolejnego klienta na pierwotnie wybranym porcie.
  
<p align="center">
  <img src="https://github.com/MRejdych/chat/blob/master/imgs/client-server.png?raw=true"/>
</p>  


## Bibliografia

- Java. Podstawy. Wydanie X, Cay. S. Horstmann, ISBN: 978-83-283-2480-0 
- Java. Techniki Zaawansowane. Wydanie X, Cay. S. Horstmann, ISBN: 978-83-283-3479-3  








