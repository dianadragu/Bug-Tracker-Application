I Structura
Am structurat impelementarea acestei teme in mai multe pachete:
- commands -> contine clasele de comenzi si gestiune a comenzilor / error handler
- database -> contine baza de date a aplicatiei si enumul pentru perioadele aplicatiei
- entities -> contine :
    - users -> pachet cu clasele utilizatorilor care mostenesc interfata Comuna
    - tickets -> pachet cu clasele tichetelor si un pachet observers care gestioneaza
      sistemul de notificari care urmaresc schimbari ale proprietatilor tichetelor
    - observer -> un pachet care contine interfata Observer si clasa Subiectului
    - milestone -> pachet pentru clasa milestone, enumul cu statusul si pachetul cu observatori
      pentru milestone
    - fileio -> pachet pentru clasele in care fac maparea datelor de intrare din fiserele JSON
      in obiecte
    - reports -> pachet in care am implementat startegiile si vizitatorii pentru logica de
      construire a rapoartelor. Contine si pachetul performance, in care am separat
      clasele care implementeaza Strategy Design Pattern pentru comanda de generare
      a raportului de performanta
    - main -> pachetul initial din schelet in care am pastrat clasa originala App si clasa
      AppRunner care se ocupa cu executia propriu-zisa a aplicatiei prin rularea commenzilor
      si aplicarea actualizarilor.
    - search -> pachet in care am mutat logica comenzii de search pe baza filtrelor (clase din
      pachetul filters)
    - utils -> pachet pentru clase ajutatoare pentru generarea nodurilor de output, calcule matematice
      lucrul cu obiecte de tip LocalDate.

II Precizari implementare
1. In rezolvare am utilizat mai multe design pattern-uri, dupa cum urmează:
- Singleton
- Visitor
- Factory
- Command
- Observer
- Strategy
2. De asemenea, pe langa Jackson care era precizat in enunt, am utilizat biblioteca Lombok pentru:
- generarea getterilor si setterilor
- generarea constructorilor
- desgin pattern-ul SuperBuilder pentru a permite si mostenirea

III Etapele implementarii
1. Prima faza a fost construirea scheletului aplicatiei. M-am inspirat din prima tema, de aceea am
   ales sa fac clase separate in care sa deserializez inputul.
   - InputLoader face popularea efectiva a campurilor prin doua liste UserInput si CommandInput.
   - CommandInput e structura generala a inputului unei comenzi, cu toate campurile posibile pentru
   un input
    - UserInput clasa abstracta mostenita de clasele echivalente inputului fiecarui tip de user. Am
      folosit adnotarile JsonTypeInfo si JsonSubTypes pentru a gestiona automat polimorfismul, deci practic
      utilizatorii diferiti sunt instantiati direct in functie de campul "role".
    - ReportedTicketParamsInput are aceeasi structura.
      Ulterior am construit AppRunner, pentru ca am vrut sa separ clasa App care scrie rezultatele si
      instantiaza inputLoader pentru a citi datele, de executia aplicatiei. Astfel scopul principal al clasei
      AppRunner este sa proceseze comenzile, sa incarce userii in baza de date in momentul in care este instantiata.
      Avand in vedere ca exista o lista de useri comuna, ea se va incarca o singura data, la pornirea aplicatiei. De
      asemenea, AppRunner este cea care apeleaza metoda pentru a actualiza datele din baza de date inainte de rularea
      urmatoarei comenzi. Am inclus si metoda de clearDatabase in constructor, pentru a ma asigura ca database nu
      contine valori ale unor rulari trecute.
      Deoarce aplicatia cere accesul permanent la anumite date, am implementat clasa AppDatabase ca un Singleton
      pentru a returna aceeasi instanta a obiectului de oriunde a-si initializa obiectul, astfel toate entitatile/comenzile
      din aplicatie sa foloseasca aceleasi data, general valabile: lista de utilizatori, lista cu toate milestoneurile,
      lista cu toate ticketele, perioada in care se afla aplicatia, etc..
2. A doua etapa a constat in crearea entitatilor Ticket si User. Ambele entitati sunt formate dintr-o clasa abstracta
   parinte si clase care extind parintele.
    - User -> contine o lista de milestone-uri, intrucat atat managerii cat si developerii sa tina evidenta milestone-urilor
      pe care le-au creat/la care sunt repartizati.
    - Ticket -> contine pe langa parametrii posibili din input, o lista separata unde retin comentariile si o lista pentru
      obiectele care detin date despre istoric(ticketHistory). In clasa Ticket am definit metode pentru schimbul
      prioritatii, al statutului, si al manipularii comentariilor, precum si al expertizelor cerute de la devs.
      Pentru a popula listele de tichete si useri din database, am folosit aceeasi strategie, pentru ca eu aveam mai mult
      nevoie de un factory, dar cum fiecare tip de tichet/user are parametrii diferiti a trebuit sa imbin si un visitor. In acest punct
      al temei nu stapaneam la fel de bine design pattern-urile asa ca am ales sa fac o singura clasa de tip loader cu 3 metode, fiecare
      cu parametrul de input diferit, in care pur si simplu returnam obiectul cu tipul respectiv, folosind design pattern-ul Super Builder
      din Lombok, pe baza campurilor pe care le luam din obiectul dat ca parametru. Fiecare tip de Input, atat pt tichet, cat si pentru user
      contine metoda clasica accept() cu clasa visitor ca parametru, pentru a putea fi implementata de fiecare copil in parte, adica partea
      de visitor.
      - Milestone -> retine toate informatiile despre un milestone, inclusiv id-urile tichetelor si lista de developeri repartizati.
3.  Etapa de construire a comenzilor, aduce utilizarea pattern-urilor Command, Factory, si iar Visitor.
    Am creat o interfata Command, cu o metoda execute() care sa fie implementata de fiecare clasa care implementeaza Command, deci
    pentru fiecare comanda am facut clase separate. Initial nu am citit bine enuntul, si am crezut ca pot implementa ca la carte Command
    pattern, asa ca am facut si o clasa Invoker prin care ma gandeam ca pot face comenziile de Undo, dar acestea sunt comenzi in sine,
    cu logici separate, dar cum aplicatia poate fi extinsa, am pastrat Invoker-ul pentru ca el sa fie cel care executa comanda primita ca
    parametru, si nu AppRunner(el doar creeaza Invoker-ul). Din nefericire, nu am reusit sa gasesc vreo modalitate sa scap de switch, nici
    in acest proiect, de aceea am realizat o clasa separata Factory care sa returneze comanda potrivita in functie de numele ei. AppRunner
    apeleaza metoda createCommand din factory pentru a obtine clasa echivalenta comenzii si prin intermediul invoker-ului o apeleaza.
    Visitor Pattern in acest context apare in errorHandler, clasa care verifica erorile generale pentru fiecare comanda. Totusi, a intervenit
    problema de verificare a rolurilor utilizatorilor acceptate de fiecare commanda, pentru ca acestea erau diferite. Astfel, am gasit solutia
    ca aceasta clasa ErrorHandler sa joace pe post de vizitator, iar fiecare comanda sa implementeze metoda accept (cu error handler ca parametru),
    definita in command, in care isi construiesc singure o lista cu utilizatorii pe care ii acepeta si apeleaza metoda de verificare din errorHandler
    pe baza acestei liste.
    Fiecare comanda urmeaza aceeasi structura, in metoda execute: instantiaza un error handler, apeleaza metooda, verifica erorile, realizeaza
    scopul si intorc un objectNode pe care AppRunner il adauga lista de objectNodes.
- Clasele de output din utils au rolul de a separa construirea nodurilor efectiva de logica clasei si sunt refolosibile de mai multe comenzi,
  cum ar fi CmdCommonOutput, care e o clasa ajutatoare cu o metoda statica toJson care intoarce un objectNode care contine deja inf genrale despre
  comanda, specifice fiecarui output.
  ->> O logica speciala am implementat-o pentru:

    - generarea notificarilor si a actiunilor automate
      In acest context am folosit un ObserverPattern, deoarce aveam nevoie de un observator si pentru milestone, dar si pentru tichete. De aceaa,
      pentru a nu scrie cod de doua ori, am folosit genericitate. in pachetul entities.observer am creat o clasa abstracta Subject<T> care mentine o lista
      de observatori generici Observer<T> si implementeaza o metoda notifyObsersevers, si interfata Observer<T> cu o metoda update. Atat Milestone cat si Tichet,
      in pachetele lor au cate o clasa Notification care retine datele necesare pentru fiecare observator, si mai multe clase observatori concreti care implementeaza
      metoda update(), pentru un observator de tipul clasei de notificare aferente.

    - pentru metoda search pe baza de filtre
      Aici am folosit Strategy Pattern, dar si genericitate: am creat o interfata strategy cu o metoda passFilter care primeste un parametru de tip T, si mai multe
      clase, fiecare reprezentand o strategie de filtrare. Deoarece exista strategii diferite in functie de tipul obiectului, dev sau ticket, am creat o clasa FIlterFactory cu 2
      metode care intorc o lista de strategii pe baza tipului respectiv si a filtrelor primite din fisierul de la input. De asemenea, am introduc si o clasa SearchContext, care
      aplica strategiile de filtrare pe lista de entitati data ca parametru.

- pentru generarea Rapoartelor
  Aici, epntru rapoartele de risk, customerImpact, si resolution efficiency am folosit un Visitor Pattern, iar pentru rapoartele de performanta am folosit un strategy pattern
  intrucat formula de calcul era diferita pentru fiecare nivelul de expertiza al developerului. In cazul primelor tipuri de rapoarte, nu am putut implement Strategy, desi am vrut,
  pentru ca necesita parametrii de tipuri difieriti(tichete diferite).

IV UTILIZARE LLM-uri
Am utilizat LLM-uri DOAR in scop de verificare a ideilor, feedback asupra structurarii codului pentru a respecta principiile OOP si debugging in cazul calculelor matematice(score de
performanta).

V FEEDBACK
Mi-a placut foarte mult tema asta, desi nu am reusit sa o modelez exact cum as fi vrut eu.
A fost foarte interesant, ca desi eram aproape, uneori, de un raspuns, aparea ceva o "chichita"
si trebuia sa ma gandesc la alta metoda, si tot asa...



     