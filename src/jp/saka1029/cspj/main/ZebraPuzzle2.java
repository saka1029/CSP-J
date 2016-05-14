package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

/**
 * See
 * https://en.wikipedia.org/wiki/Zebra_Puzzle
 * 
 * @author saka1029
 *
 */
public class ZebraPuzzle2 extends SolverMain {

    Logger log = Logger.getLogger(ZebraPuzzle2.class.getName());

    enum Color {Yellow, Blue, Red, Ivory, Green}
    enum Nationality {Norwegian, Ukrainian, Englishman, Spaniard, Japanese}
    enum Drink {Water, Tea, Milk, Orangejuice, Coffee};
    enum Smoke {Kools, Chesterfield, OldGold, LuckyStrike, Parliament}
    enum Pet {Fox, Horse, Snails, Dog, Zebra}
    
    static class Person {
        final String nationality;
        final Variable<Integer> house;
        final Variable<Color> color;
        final Variable<Drink> drink;
        final Variable<Smoke> smoke;
        final Variable<Pet> pet;
        
        Person(Problem problem, String nationality) {
            this.nationality = nationality;
            house = problem.variable(nationality + ".house", Domain.range(1, 5));
            color = problem.variable(nationality + ".color", Domain.of(Color.values()));
            drink = problem.variable(nationality + ".drink", Domain.of(Drink.values()));
            smoke = problem.variable(nationality + ".smoke", Domain.of(Smoke.values()));
            pet = problem.variable(nationality + ".pet", Domain.of(Pet.values()));
        }
        
        String toString(Result r) {
            return nationality + "," + r.get(house) + "," + r.get(color)
                + "," + r.get(drink) + "," + r.get(smoke) + "," + r.get(pet);
        }
    }

    Person[] persons;
    
    @Override
    public void define() {
//      1. There are five houses.
        Person norwegian = new Person(problem, "Norwegian");
        Person ukurainian = new Person(problem, "Ukurainian");
        Person englishman = new Person(problem, "Englishman");
        Person spaniard = new Person(problem, "Spaniard");
        Person japanese = new Person(problem, "Japanese");

        persons = new Person[]{norwegian, ukurainian, englishman, spaniard, japanese};
        
        allDifferent(map(h -> h.color, persons));
        allDifferent(map(h -> h.house, persons));
        allDifferent(map(h -> h.drink, persons));
        allDifferent(map(h -> h.smoke, persons));
        allDifferent(map(h -> h.pet, persons));
        
//      2. The Englishman lives in the red house.
        constraint("EnglishmanRed", c -> c == Color.Red, englishman.color);
        
//      3. The Spaniard owns the dog.
        constraint("SpaniardDog", c -> c == Pet.Dog, spaniard.pet);
        
//      4. Coffee is drunk in the green house.
        constraint(or(map(h -> variable(null, "CoffeeGreen",
            (x, y) -> x == Drink.Coffee && y == Color.Green, h.drink, h.color),
            persons)));
        
//      5. The Ukrainian drinks tea.
        constraint("UkurainianTea", c -> c == Drink.Tea, ukurainian.drink);
        
//      6. The green house is immediately to the right of the ivory house.
        constraint(or(mapPair((h, j) -> variable(null, "GreenRightIvory",
            (hc, hn, jc, jn) -> 
                (hc == Color.Green && jc == Color.Ivory && hn == jn + 1
                || hc == Color.Ivory && jc == Color.Green && jn == hn + 1),
                h.color, h.house, j.color, j.house),
            persons)));
        
//      7. The Old Gold smoker owns snails.
        constraint(or(map(h -> variable(null, "OldGoldSnails",
            (x, y) -> x == Smoke.OldGold && y == Pet.Snails, h.smoke, h.pet),
            persons)));
        
//      8. Kools are smoked in the yellow house.
        constraint(or(map(h -> variable(null, "KoolsYellow",
            (x, y) -> x == Smoke.Kools && y == Color.Yellow, h.smoke, h.color),
            persons)));

//      9. Milk is drunk in the middle house.
        constraint(or(map(h -> variable(null, "MilkMiddle",
            (x, y) -> x == Drink.Milk && y == 3, h.drink, h.house),
            persons)));

//      10. The Norwegian lives in the first house.
        constraint("NorwegianFirstHouse", n -> n == 1, norwegian.house);

//      11. The man who smokes Chesterfield lives in the house next to the man with the fox.
        constraint(or(mapPair((h, j) -> variable(null, "ChesterfieldFox",
            (hs, hp, hn, js, jp, jn) ->
                (hs == Smoke.Chesterfield  && jp == Pet.Fox
                || hp == Pet.Fox && js == Smoke.Chesterfield) && Math.abs(hn - jn) == 1,
                h.smoke, h.pet, h.house, j.smoke, j.pet, j.house),
            persons)));

//      12. Kools are smoked in the house next to the house where the horse is kept.
        constraint(or(mapPair((h, j) -> variable(null, "KoolsNextHorse",
            (hs, hp, hn, js, jp, jn) -> (hs == Smoke.Kools && jp == Pet.Horse
                || hp == Pet.Horse && js == Smoke.Kools) && Math.abs(hn - jn) == 1,
                h.smoke, h.pet, h.house, j.smoke, j.pet, j.house),
            persons)));

//      13. The Lucky Strike smoker drinks orange juice.
        constraint(or(map(h -> variable(null, "LuckyStrikeOrangeJuice",
            (x, y) -> x == Smoke.LuckyStrike && y == Drink.Orangejuice, h.smoke, h.drink),
            persons)));

//      14. The Japanese smokes Parliaments.
        constraint("JapaneseParliament", s -> s == Smoke.Parliament, japanese.smoke);

//      15. The Norwegian lives next to the blue house.
//        constraint("NorwegianNotBlue", s -> s != Color.Blue, norwegian.color);
//        constraint(ne(norwegian.color, Color.Blue));

        constraint(or(map(e -> variable(null, "NorwegianNextBlue",
            (nh, c, h) -> c == Color.Blue && Math.abs(nh - h) == 1,
            norwegian.house, e.color, e.house),
            persons)));
 
//        constraint(or(
//            variable(null, "NorwegianNextBlue", (nh, c, h) -> c == Color.Blue && Math.abs(nh - h) == 1, norwegian.house, ukurainian.color, ukurainian.house),
//            variable(null, "NorwegianNextBlue", (nh, c, h) -> c == Color.Blue && Math.abs(nh - h) == 1, norwegian.house, englishman.color, englishman.house),
//            variable(null, "NorwegianNextBlue", (nh, c, h) -> c == Color.Blue && Math.abs(nh - h) == 1, norwegian.house, spaniard.color, spaniard.house),
//            variable(null, "NorwegianNextBlue", (nh, c, h) -> c == Color.Blue && Math.abs(nh - h) == 1, norwegian.house, japanese.color, japanese.house)
//        ));

//        constraint(or(mapPair((h, j) -> variable(null, "NorwegianNextBlue",
//            (ha, hc, hn, ja, jc, jn) ->
//                (ha == Nationality.Norwegian  && jc == Color.Blue
//                || hc == Color.Blue && ja == Nationality.Norwegian)
//                && Math.abs(hn - jn) == 1,
//                h.nationality, h.color, h.house, j.nationality, j.color, j.house),
//            persons)));
//        for (Constraint c : problem.constraints)
//            System.out.println(c);
    }
    
    @Override
    public boolean answer(int n, Result result) throws IOException {
        log.info("*** answer " + n);
        for (Person e : persons)
            log.info(e.toString(result));
        return true;
    }

    public static void main(String[] args) throws IOException {
		new ZebraPuzzle2().parse(args).solve();
    }


}
