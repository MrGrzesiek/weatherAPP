# Ćwiczenie 2: Aplikacja pogodowa

Celem ćwiczenia jest zapoznanie się z następującymi zagadnieniami:
- Interfejs użytkownika bazujący na fragmentach.
- Komunikacja pomiędzy fragmentami i aktywnością.
- Dostosowanie interfejsu użytkownika do różnych ekranów.
- Usługi sieciowe dostępne w systemie Android.
- Parsowanie plików XML/JSON.
- Przechowywanie danych w pamięci urządzenia (np. w prywatnym katalogu aplikacji).
- Przechowywanie danych w bazie danych.

## Założenia:
1. Niniejsze ćwiczenie zakłada opracowanie aplikacji pozwalającej m.in. na pobieranie i wyświetlanie informacji o warunkach pogodowych (bieżących i prognozowanych) dla lokalizacji wybranych przez użytkownika.
2. Dane pogodowe można pobrać korzystając z API udostępnianego przez serwis [OpenWeatherMap](https://openweathermap.org). Dane można pobierać w postaci plików JSON lub XML. Szczegółowe informacje dla programistów chcących korzystać z plików udostępnianych w ramach portalu można znaleźć pod adresem: [OpenWeatherMap API](https://openweathermap.org/api)
3. Aplikacja powinna umożliwiać użytkownikowi zdefiniowanie listy ulubionych lokalizacji, dla których będą pobierane dane pogodowe.
4. W momencie uruchomienia aplikacja powinna sprawdzać czy możliwe jest połączenie z internetem. Jeżeli tak, pobierane są aktualne informacje na temat pogody i zapisywane w pamięci telefonu (w prywatnym katalogu aplikacji).
5. Jeżeli żadne połączenie internetowe nie jest aktywne w chwili uruchomienia aplikacji, informacje na temat pogody powinny zostać wczytane z pliku, który został zapisany podczas ostatniego połączenia. Ponadto użytkownik powinien być poinformowany o tym, że dane mogą być nieaktualne, a do aktualizacji wymagane jest połączenie internetowe.

**Uwaga**: Warto zastanowić się czy konieczne jest pobieranie informacji z Internetu przy każdym uruchomieniu aplikacji. Wśród informacji dostarczanych w pliku XML znajduje się czas ostatniej aktualizacji danych. Można więc zdefiniować czas, przez który dane mogą być trzymane w pamięci podręcznej, bez konieczności odświeżania. Można również z góry założyć pewien czas, przez który dane nie będę odświeżane.

6. Dane pogodowe powinny być prezentowane w przejrzystej formie. W tym celu należy przygotować interfejs aplikacji bazujący na fragmentach, tj.:
   - Fragment 1 – podstawowe dane, tj.: nazwa miejscowości, współrzędne geograficzne, czas, temperatura, ciśnienie, opis i reprezentacja graficzna warunków pogodowych.
   - Fragment 2 – dane dodatkowe np.: informacje o sile i kierunku wiatru, wilgotności, widoczności.
   - Fragment 3 – prognoza pogody na nadchodzące dni.

**Uwaga**: Układ fragmentów na ekranie powinien być zależny od jego orientacji i rozdzielczości. W przypadku orientacji pionowej dla urządzenia typu telefon należy zastosować przewijanie fragmentów np. za pomocą klasy ViewPager.

7. W aplikacji powinno być dostępne menu, które umożliwi:
   - Odświeżenie informacji z internetu na żądanie użytkownika.
   - Ustawienie/zmianę lokalizacji, dla których pobierane są dane.
   - Wybór jednostek miary stosowanych w aplikacji.
