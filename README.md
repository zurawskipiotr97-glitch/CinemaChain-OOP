
![UML.png](https://img.plantuml.biz/plantuml/dsvg/XLP1Roid4BxpAv0lbT8aQg_8iXBRKbirIQFOwXsp4nk5XHMmYTo-_FTdO7ZBiXkzKyBCjpFp3JCVldEUMz-tYfna0rH8AnGGzoPrnorlgL3SkNllhJpq7luIHk_aVq1_8wJZueqVWIw4rD1oSU9ItuBkMxUW_vFm3_q3j8NBuPEaymwutt0FHsFFbNddkUzTD7v-4Rkpyd8yXkXIUx2lN01zJiPdewJ0cB5akeLNtYjV-oczeGVk8DZ1CVh8ksLPrZLTo-CMX6ovMaMqFrc0xQwtbohi4LW1YFvtsUs41T2NDFEehocrfW4L2AMQDz6r2IuiLJQCxhozX8x7z2sMdUpN-5KgjZNlCj1waCul7yCXsKNll6d1rf-Y6PbAyGRUhSyRqy0Ok8-k18kbF_4MScc1NabWldPhJ9ih-vChxAxBhSkkNJOaloxoH0Dl6WlEpUH6BlFPpOTE77AUaj6hjCu_rPaKdp52owMQ9W-4CF6aOWSyPusZjXe-YpT4bqiBmjXcjOeXVgLUUZN429QcjznBen-bxdsuOAbzT1p3YkNeSTzMqSwFy0BEMod2Pn4_fFtHuvWFfuQbQgBXn9LYaLJgfVjNTemUZ570DPhsSI_6rYWcUnaQy604Lrlk8HoZiojNaOqt5nk5Eb1DwQ0FwReiEB3lqApF_tnei7_3kSB7loKszivzXNyPCEC4ztvz3icd-p1YDycqzuhHBLvPecPKi__vUWqt1DnFvyAQZz5KwBuzW1s6GQ28iP7I3b4BejFW1dipok2GVPumR6tAPe7xqAJwidC-f8L39Y9hZ3rQVOO3MIvCef1NDv3C-rs7VuwQw-RMAFePue8fpk2CEFz2Nq2b8Jd9pf7ngx7yTdix-vwGEICrDpTqSRKOltQiU7K8GPsyGcHK3vOKilODkyeAiIIaV2io5Zzb-PsO0w7ciBJqiv11IjdmSCm2yOBP-EaW90UO0WST9f4r0jB0iwILXAGQiP7uCuAXrX4oj2G0VgyeJTpOtYXgK-VuxjXOfGeu5bE8KIeN3mKIAOssZSqBKPcWRjLKCmW9oR7mUhpoxXE20H0sEpyrPcX-vHac8QqX8TNI8QuQ1787kkbRzHq0)


Celem zadania jest zaprojektowanie i implementacja systemu wspierającego funkcjonowanie sieci multipleksów (a więc mówimy o obsłudze wielu lokalizacji, a w każdej z nich jest wiele sal kinowych).

![img.png](docs/images/img.png)

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

![img_1.png](docs/images/img_1.png)

Dla uproszczenia NIE ZAJMUJEMY SIĘ (nie modelujemy na diagramie klas):

płatnościami online
kasjerem
administratorem
logowaniem (poza kontem klienta nie potrzebujemy nic co jest związane z logowaniem)
Dla lepszego zwizualizowania celu powinni Państwo dostarczyc demonstracyjną klasę pl.edu.agh.zurawskipiotr.cinemachain.Main gdzie zobaczymy cos w stylu:

public class pl.edu.agh.zurawskipiotr.cinemachain.Main {
public static void main(String[] args) {
pl.edu.agh.zurawskipiotr.cinemachain.model.Cinema cinema1 = new pl.edu.agh.zurawskipiotr.cinemachain.model.Cinema("Super Tarasy", "ul. Akademicka 5");
//... configuration and test data should be inserted here...
// below you will find sample function executions
cinema1.printProgramme();
pl.edu.agh.zurawskipiotr.cinemachain.model.Screening screening = cinema1.getScreenings()[0];
screening.reservePlaces("H34", "H35", "H36"); // seats number given
screening.reservePlaces(seat1, seat2, seat3); // other option
screening.reservePlaces(customer, "H34", "H35", "H36"); // reservation for registered customer
movie1 = cinema1.findMovie("James Bon");
// ... etc ...
}