# Aplikacja do prezentowania danych statystycznych na temat ilości pasażerów na wybranych lotniskach

Aplikacja służy do wyświetlania informacji statystycznych na temat ilości pasażerów na wybranych lotniskach. Dane są zgrupowane kwartałami na okres 30 ostatnich kwartałów.

Aplikacja została podzielona na 2 części:
- serwer
- klient

## Serwer

Serwer służy do odczytania pliku Excel, w którym zawarte są dane do obliczeń. Wystawia on REST API do komunikacji oraz zwracania poszczególnych danych dla aplikacji klienta.

Serwer został napisany w języku JAVA w wersji 17.

### Uruchomienie serwera

Do uruchomienia serwera wymagana jest JAVA w wersji 17. Po zainstalowaniu środowiska deweloperskiego JAVA JDK, można uruchomić serwer za pomocą poniższej komendy:

```bash
./gradlew bootRun
```

Po wykonaniu powyższej komendy, serwer zostanie uruchomiony na porcie `8080`.

### REST API

Poniżej znajduje się lista endpointów REST API, których można używać z serwerem aplikacji:

#### Wgranie pliku Excel do aplikacji

Metoda: `POST` </br>
Endpoint: `/api/v1/statistics` </br>
Payload: `file`, który jest typu `multipart/form-data`

#### Pobranie danych surowych do analizy

Metoda: `GET` </br>
Endpoint: `/api/v1/statistics/raw` </br>
Odpowiedź: `JSON` o strukturze przedstawionej poniżej

```json
[
  {
    "timestamp": "III kwartał 2022",
    "dataPoints": [
      { "id": "Chopina w Warszawie", "value": 4898107.0 },
      { "id": "Kraków Balice", "value": 2236123.0 },
      { "id": "Gdańsk im. L. Wałęsy", "value": 1588483.0 },
      { "id": "Katowice - Pyrzowice", "value": 1886490.0 },
      { "id": "Wrocław - Strachowice", "value": 1066189.0 }
    ]
  }
]
```

#### Pobieranie obliczonych danych statystycznych

Metoda: `GET` </br>
Endpoint: `/api/v1/statistics`
Odpowiedź: `JSON` o strukturze przedstawionej poniżej

```json
[
  {
    "id": "Chopina w Warszawie",
    "values": [
      { "name": "std", "value": 1660219.5277269054 },
      { "name": "avg", "value": 3738282.1 },
      { "name": "min", "value": 64657.0 },
      { "name": "firstQuantile", "value": 3107229.0 },
      { "name": "dominant", "value": 2331906.0 },
      { "name": "median", "value": 3917355.0 },
      { "name": "max", "value": 6430206.0 },
      { "name": "skewness", "value": -0.6354316294314826 },
      { "name": "diff", "value": 6365549.0 },
      { "name": "kurtosis", "value": -0.2503921470236872 },
      { "name": "secondQuantile", "value": 3917355.0 },
      { "name": "thirdQuantile", "value": 4891498.0 }
    ]
  }
]
```


## Klient


Serwer został napisany we frameworku Vue.js oraz biblioteki Vuetify w wersji 3.

### Uruchomienie klienta

Do zainstalowania pakietów klienta wymagany jest zainstalowany Node.js w wersji > 20. Po zainstalowaniu Node.js, należy przejść do katalogu `frontend` i wykonać poniższe komendy:

```bash
npm install
```


Po zainstalowaniu pakietów, można uruchomić aplikację klienta za pomocą poniższej komendy:

```bash
npm run dev
```

Po wykonaniu powyższej komendy, aplikacja klienta zostanie uruchomiona na porcie `3000`.