# CinemaChain -- Projekt OOP

## Opis projektu

Projekt realizowany w ramach programowania obiektowego.\
System symuluje działanie sieci kin (CinemaChain) z wykorzystaniem zasad
OOP (enkapsulacja, dziedziczenie, polimorfizm, abstrakcja).

## Struktura projektu

-   `src/` -- kod źródłowy aplikacji\
-   `docs/` -- dokumentacja (jeśli występuje)\
-   `tests/` -- testy jednostkowe (jeśli występują)

## Wymagania

-   JDK (zalecana wersja zgodna z projektem)
-   Dowolne IDE obsługujące Javę (np. IntelliJ IDEA / Eclipse)

## Uruchomienie

1.  Sklonuj repozytorium.
2.  Otwórz projekt w IDE.
3.  Zbuduj projekt.
4.  Uruchom klasę z metodą `main`.

------------------------------------------------------------------------

## Diagram UML

> **[![](https://img.plantuml.biz/plantuml/dsvg/ZLTDR-Cs4BqRy7yWV2cmZONqIhJ69ZQmMtJJ1dMmTuQQs4GeqI3f16cR_zxXfoXAHZQ701hEZEQzDpEYFnlBj3rtShampw8_CSqwmYKpviPQBHxF5dxdgj-BVu1SBnT7qGBXGdC9yQ43UrIjMIwMYv_8R-wFt3F-p0vWmkDoSGhFPCMPPL8TLkJVvO8GwCyT-GAz1lVatqdkr8k0S4p8prPO2PJiiP3-46tjMJChL7ydUgpDK29w6uyEBXTQlWbZV_M9D_44t_ut64p3NMYEUNV_mYlNwmrPNJVDfrKeYL0IqYqNxmvRnl22PKE9O0_CRfc5Wz9l0n1dpJYqUgrGOE0ZwB9u7bFGIKB__fJtAvCov-rPLzDZC3XHu19j9XcseeUEvHoYhKkR9cLjYqoOoggLwjAhN5c9RCCrG8z-kOvaA8ln13dMIxZ8EY-GUx2hNrP4hTT97Hz7YSVfV2ASoij7Kk9Bh1LyL0exFNLX80Mx0_IBRwpCZ7hjGVy1RpCCRrMR6oqS9l085G6qDnPF5MToXqyFeeE8V5n9PghkfxrbzcnArPmL7Uyb6meqsPEIEtQAbLmL6L9LUe1dRj_-oi3Ag8A2cR07AodPnTcRz5HMEDVMkQcYORYYwOYQKAzCQZtpNPmeGJqai3uR7uwOR5VR_PvoJCuGYySdd6t7ZvA28x3xyh6WAr98IpxJiPBjW-3FO94Y9_2Odd1KTt2I93Lo4cvyMZLdRC2cR3hirKAZIQCm2MRKiA6S-xFcHsRAZUdCD1uNt22QMt7O0HSTanFzWdlEmZtwSTSdXUaWTi5h8BGS74gsPsDL1tgszNay785A4PUNqvFGnluvtL0OFcV6pI1aPSFgeinnWCVAag6AIZOlGXEfecJ8KsEBlHYXuOe7_SGuJ9ioeDx14pjBEphD33oYkku4qemNs_Aga3IDhFN3i_TzeGyJrT7ZHPooWXVzF16pXJNDVviQIINjaOb-hAlVqLjdlooj3vlHaFlWQiM5Z0cqNpP9JhzoyZgEMjRXqRmPZSqFpds9fceHVpbeuYS9lsMXz0-2ugse2HSHIWAQZu8YWYPtAyssZsAhHXw1ZsAcgznMAsFMILef-kTygnjXhT-G6gmPlWnqi6A1SnvkYSwISTaz516_zaV_QRda6gUo6RSp9QTnBryAB-p5PWyNaVmXmV2IsofkZegPY82fkEVC-Cp9xzUkdyHPfEFKWzVib8-TXzleSl4P-XP_BlmF)](https://editor.plantuml.com/uml/ZLTDR-Cs4BqRy7yWV2cmZONqIhJ69ZQmMtJJ1dMmTuQQs4GeqI3f16cR_zxXfoXAHZQ701hEZEQzDpEYFnlBj3rtShampw8_CSqwmYKpviPQBHxF5dxdgj-BVu1SBnT7qGBXGdC9yQ43UrIjMIwMYv_8R-wFt3F-p0vWmkDoSGhFPCMPPL8TLkJVvO8GwCyT-GAz1lVatqdkr8k0S4p8prPO2PJiiP3-46tjMJChL7ydUgpDK29w6uyEBXTQlWbZV_M9D_44t_ut64p3NMYEUNV_mYlNwmrPNJVDfrKeYL0IqYqNxmvRnl22PKE9O0_CRfc5Wz9l0n1dpJYqUgrGOE0ZwB9u7bFGIKB__fJtAvCov-rPLzDZC3XHu19j9XcseeUEvHoYhKkR9cLjYqoOoggLwjAhN5c9RCCrG8z-kOvaA8ln13dMIxZ8EY-GUx2hNrP4hTT97Hz7YSVfV2ASoij7Kk9Bh1LyL0exFNLX80Mx0_IBRwpCZ7hjGVy1RpCCRrMR6oqS9l085G6qDnPF5MToXqyFeeE8V5n9PghkfxrbzcnArPmL7Uyb6meqsPEIEtQAbLmL6L9LUe1dRj_-oi3Ag8A2cR07AodPnTcRz5HMEDVMkQcYORYYwOYQKAzCQZtpNPmeGJqai3uR7uwOR5VR_PvoJCuGYySdd6t7ZvA28x3xyh6WAr98IpxJiPBjW-3FO94Y9_2Odd1KTt2I93Lo4cvyMZLdRC2cR3hirKAZIQCm2MRKiA6S-xFcHsRAZUdCD1uNt22QMt7O0HSTanFzWdlEmZtwSTSdXUaWTi5h8BGS74gsPsDL1tgszNay785A4PUNqvFGnluvtL0OFcV6pI1aPSFgeinnWCVAag6AIZOlGXEfecJ8KsEBlHYXuOe7_SGuJ9ioeDx14pjBEphD33oYkku4qemNs_Aga3IDhFN3i_TzeGyJrT7ZHPooWXVzF16pXJNDVviQIINjaOb-hAlVqLjdlooj3vlHaFlWQiM5Z0cqNpP9JhzoyZgEMjRXqRmPZSqFpds9fceHVpbeuYS9lsMXz0-2ugse2HSHIWAQZu8YWYPtAyssZsAhHXw1ZsAcgznMAsFMILef-kTygnjXhT-G6gmPlWnqi6A1SnvkYSwISTaz516_zaV_QRda6gUo6RSp9QTnBryAB-p5PWyNaVmXmV2IsofkZegPY82fkEVC-Cp9xzUkdyHPfEFKWzVib8-TXzleSl4P-XP_BlmF)**\
> Skrócony UML

> **![](https://img.plantuml.biz/plantuml/dsvg/lLfPRnit47utuFz0-ALIBGd9wy8r8ajo8jIo14bDK1H5GE_I4g4zX3tiE6d-UuVdabpgAkpcmU2ICyFX7DyCgNmeIfoNLHAVdnKRccvnZXCKnhWe-cMPquUg9ECmInVqEq7lpi_MD28ef7aOa_EpyxDVq6-x_h75RKv3cguEq6rnkC4hWYwsSOz4LG-lrhtlLOwVGQ4jpSgy1q98WiCrfcbFohn0k53oqO_pCuHeMfBy4OS4pSJqB8jf-28M4ReCSHnMCIu9MoQjWk0oG0lusq5FTBi8Sq9Iu0hGGvR51ASTLAvXRgWdsW6weQiX2Mc2OoRq9pC14jP2G_A8gxZqxjrzm8NOjWZG16-lsBO3K6MLvIyTG-grf1SxBweSpfolYBinK9Wgxw6xzAdLEbeTPGFl3hRTl2HjgSKFYBAIXZzH9DKHUe15VVgzjSEyhkhr_hb6ZXe_ZuZnA4jOM8Qun77skg5kY-OHBySYi4XQ9UWZINEY0cqn67-z7OlnpNZwSTwVVVfJV0wcaz5GZlk3vNXwByVtuqc__hpjJyPtacmuxq_wOlXfl5XEv-D1_wuH-9FiYH9qTPMJCCkZwskTS2KjOuY61S08mm0-5y5XIvgb4ve2ia3uGTRAfHKx2CpSqQAyugUIsN095fkJ0iI4Z5NpI2t4_jtkDRfur-lzUY4j4Y0Xy2GlFe59oLlua0lc7kGZmtzcxYdJVHxFf3-N_VjXVpuKNxFvT3AL2t_CHlElUi9o29FeyqUUFJlUWFqVI6uw8PIQ19PUsaFb88i8IzjQZBNr9np76lHId3Zkxx8SLYvc6oWFCxwMIT-kjxo7QNjBCS_u2xRGa8QZY7qQY05KJncDb258aZsHtIJMaGRScVfGD7AEb6TPeex4Zg4Wl53ufC-h9gvtsmSqXv0jdCCA5IpxT913QXXi7FdCvJLZ8USjqrks0iw-sCd7h9PSPLmHSsOaxIl3JDoHsbArDnrRCPVo65d3du27cRq7SxdYQHLrQT2JlWZOAr46oGb2uIYpF5j10vMGsoo_9z_ABuHiTX_APBWXvJEijXxpB1cdOLmLz0bSSfT15Hn26YvfGg30PQDl_ZN_9ZAIEI8mXBJ09a2XaTyyTI_UNw2isrL8ola2nBEMqSXSSAWuPu1u8fo4jb2cDw_T7jgYEUBAiKXXaTHGJaZ-n2kH3ocpvvJalvENHi1Z3e0we_cYyYOO7imPzKl7Cz8F9zX5XzfRM4OBvxRHNqv5Ma2_LXNA8BVpqKYCvgF5QFvv90lIOdetz9OVraF5EDMeMMYHik6K85PlfK0nvvuXqStBL3l1uZ1jBgcv7ujT78QltKtwvStBGZhLPh8ywDbjMG9e36Kp8mF0E7ZB29L15iU4TougO3febwIs1X-kOCiDn0uo18kfl_vsaB0grraErxKPRyWBqWgheimIaayRmbOLj5NDQLfyP0jVA4YgocMs8QdHP1_K8C7vPf75aJR2ph9Gkzz7MwyQXKHwJXH_rpnjRsBMvPwhVGi8QyH4KyeHHwpZUP7YRR7EIeRoUoFxqenLayk83pTKxRooKnNYCM6TRo0QO3bLLpFLhnIVwLPxJqykxPkIIbFs5i0YtWDXiBmrRqU1UrdIYIvp832HG2r3M2nfk24bf6ipwyIICbXTUys3Tb1zHXQgvl7OjtCoSEQEGxN_dsrs9ti7xJEe0mLWHBYkwro6i96Z8wBL5k4djmhTG_KYlTGNKB23YIURe5HeSzqG0QsGqlK4zOyItQ-1YgDKopIrWAtzARrdbqUQob0LaTeg-OU3KOBYrH0DREMnaH_MFBpxZR1J30zu3c0rah1vtX-tJJYvv8d0vYpWk5Jvq5nXDb6PmJlX9gw8FjXydsfxmSL0tGDeQxIzgXDHtMzjVdFLwMXbnzWpsyG0-G5SRgN1cMqaNsbwucaYG8cogqt6dm9widrUIoFR918XUlPRLM0ZHvFDF9letpKEdzA7ufYzdxn13ye4Y_wJZSpEt4v6_xCIet1IZzcZyVnfbKz1hcjdQ6IcUsCtRdX6MHFfAoTupr1dgDH2xE6zAWe4Kk-N3hjnUMy-i3YNPBvegwU0fzKsTMIplBVZJOQXw6cNaL1pli7BffGjdZRbXnaD0slEz_m2bfJ5m49prR8_qhpWJnmE6qZtJOE2D7PVNrJnQiZg85TC1raI3ZxAt2gHliULUh6nSwEJliExvUWrLzk6b15JtYT2BDXdSvcdsXEjjiyNx8r6qxJiBFOzlHn7gwkWm6LMG7UyrBYrJwA33Z6d1d1awadK250TPwTb3mltDqaTzY7oXesifE7KWthQbpT2f7CT5vmgTtZLKcd5IvbQKQdgiijf_IlMaPr-bn-XcOwNnabQ3HeJDDMQBtRhnQO8DvJ4Xetsmh8BfrC6Q53Q9c0nBrzlXS43ASiNy4SHhi0PP5z3RJDKwSaigY6iqzZqFLksRd2k1UoSPrqqPx-1dhsCMHTj_2Zaj9nWBD6AcXSndwKEKO6Wes7c2CBwmh6ZjB_DjMPF7_4V1TSN9R_eyhNlJE6UsnAqRAaCfJcFqVnbKIK9pdd_RmVaSK8LeXluvu2wwBZbCtQ090WUOF9symUPP23qT7q9zDmXNgVqM31wNFKPv5bHT5LXYMcwETJ7MvPnzL4Hqgjl6K4z2w9z5FiFrNW9OgUoQDWzvi3E7Wvn3kE72myDq-YztqMyCMUg-2zUzWurbVT_5DZ4zM-fvsSVI1fLIVml)**\
> Pełny UML

------------------------------------------------------------------------

## Treść zadania

------------------------------------------------------------------------

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