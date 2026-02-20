Celem zadania jest zaprojektowanie i implementacja systemu wspierającego funkcjonowanie sieci multipleksów (a więc mówimy o obsłudze wielu lokalizacji, a w każdej z nich jest wiele sal kinowych).



Proszę o:

wgranie linka do repozytorium na githubie z kodem źródłowym.
w README proszę umieścić diagram klas dla systemu (utworzony dowolną metodą)


Oto kilka istotnych założeń dot. zakresu:

ograniczamy się tylko do modelu biznesowego, a więc NIE interesują nas takie elementy jak interfejs graficzny, baza danych etc.
chcemy mieć możliwość postawienia 1 systemu dla 2 lub więcej kin
chcemy mieć możliwość rezerwacji miejsc przed seansem
chcemy mieć możliwość kupienia biletów z wyprzedzeniem
chcemy mieć możliwość sprawdzenia repertuaru na najbliższy tydzień
chcemy mieć możliwość obsługi seansów VIP i 3D
chcemy mieć możliwość sprawdzenia swoich biletów
chcemy mieć możliwość zakupu biletów bez konta
chcemy zobaczyć przykład wywołania w ramach funkcji main() - zobacz przykład na końcu!
Dla uproszczenia NIE ZAJMUJEMY SIĘ (nie modelujemy na diagramie klas):

płatnościami online
kasjerem
administratorem
logowaniem (poza kontem klienta nie potrzebujemy nic co jest związane z logowaniem)
Dla lepszego zwizualizowania celu powinni Państwo dostarczyc demonstracyjną klasę Main gdzie zobaczymy cos w stylu:

public class Main {
public static void main(String[] args) {
Cinema cinema1 = new Cinema("Super Tarasy", "ul. Akademicka 5");
//... configuration and test data should be inserted here...
// below you will find sample function executions
cinema1.printProgramme();
Screening screening = cinema1.getScreenings()[0];
screening.reservePlaces("H34", "H35", "H36"); // seats number given
screening.reservePlaces(seat1, seat2, seat3); // other option
screening.reservePlaces(customer, "H34", "H35", "H36"); // reservation for registered customer
movie1 = cinema1.findMovie("James Bon");
// ... etc ...
}