package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.IntStream;

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
public class ZebraPuzzle extends SolverMain {

    Logger log = Logger.getLogger(ZebraPuzzle.class.getName());

    enum Color {Yellow, Blue, Red, Ivory, Green}
    enum Nationality {Norwegian, Ukrainian, Englishman, Spaniard, Japanese}
    enum Drink {Water, Tea, Milk, Orangejuice, Coffee};
    enum Smoke {Kools, Chesterfield, OldGold, LuckyStrike, Parliament}
    enum Pet {Fox, Horse, Snails, Dog, Zebra}
    
    static class House {
        final Variable<Integer> number;
        final Variable<Color> color;
        final Variable<Nationality> nationality;
        final Variable<Drink> drink;
        final Variable<Smoke> smoke;
        final Variable<Pet> pet;
        
        House(Problem problem, int i) {
            number = problem.variable("house" + i, Domain.of(i));
            color = problem.variable("color" + i, Domain.of(Color.values()));
            nationality = problem.variable("nationality" + i, Domain.of(Nationality.values()));
            drink = problem.variable("drink" + i, Domain.of(Drink.values()));
            smoke = problem.variable("smoke" + i, Domain.of(Smoke.values()));
            pet = problem.variable("pet" + i, Domain.of(Pet.values()));
        }
        
        String toString(Result r) {
            return "" + r.get(number) + "," + r.get(color) + "," + r.get(nationality)
                + "," + r.get(drink) + "," + r.get(smoke) + "," + r.get(pet);
        }
    }

    House[] houses;
    
    @Override
    public void define() {
//      1. There are five houses.
        houses = IntStream.rangeClosed(1, 5)
            .mapToObj(i -> new House(problem, i))
            .toArray(House[]::new);
        
        allDifferent(map(h -> h.color, houses));
        allDifferent(map(h -> h.nationality, houses));
        allDifferent(map(h -> h.drink, houses));
        allDifferent(map(h -> h.smoke, houses));
        allDifferent(map(h -> h.pet, houses));
        
//      2. The Englishman lives in the red house.
        constraint(or(map(h -> variable(null, "EnglishmanInRed",
            (x, y) -> x == Nationality.Englishman && y == Color.Red, h.nationality, h.color),
            houses)));
        
//      3. The Spaniard owns the dog.
        constraint(or(map(h -> variable(null, "SpaniardOwnsDog",
            (x, y) -> x == Nationality.Spaniard && y == Pet.Dog, h.nationality, h.pet),
            houses)));
        
//      4. Coffee is drunk in the green house.
        constraint(or(map(h -> variable(null, "CoffeeInGreen",
            (x, y) -> x == Drink.Coffee && y == Color.Green, h.drink, h.color),
            houses)));
        
//      5. The Ukrainian drinks tea.
        constraint(or(map(h -> variable(null, "UkrainianDrinksTea",
            (x, y) -> x == Nationality.Ukrainian && y == Drink.Tea, h.nationality, h.drink),
            houses)));
        
//      6. The green house is immediately to the right of the ivory house.
        constraint(or(mapPair((h, j) -> variable(null, "GreenRightOfIvory",
            (hc, hn, jc, jn) -> 
                (hc == Color.Green && jc == Color.Ivory && hn == jn + 1
                || hc == Color.Ivory && jc == Color.Green && jn == hn + 1),
                h.color, h.number, j.color, j.number),
            houses)));
        
//      7. The Old Gold smoker owns snails.
        constraint(or(map(h -> variable(null, "OldGoldOwnsSnails",
            (x, y) -> x == Smoke.OldGold && y == Pet.Snails, h.smoke, h.pet),
            houses)));
        
//      8. Kools are smoked in the yellow house.
        constraint(or(map(h -> variable(null, "KoolsInYellow",
            (x, y) -> x == Smoke.Kools && y == Color.Yellow, h.smoke, h.color),
            houses)));

//      9. Milk is drunk in the middle house.
        constraint(or(map(h -> variable(null, "MiddleDrinkMilk",
            (x, y) -> x == Drink.Milk && y == 3, h.drink, h.number),
            houses)));

//      10. The Norwegian lives in the first house.
        constraint(or(map(h -> variable(null, "NorwegianInFirstHouse",
            (x, y) -> x == Nationality.Norwegian && y == 1, h.nationality, h.number),
            houses)));

//      11. The man who smokes Chesterfield lives in the house next to the man with the fox.
        constraint(or(mapPair((h, j) -> variable(null, "ChesterfieldNextToFox",
            (hs, hp, hn, js, jp, jn) ->
                (hs == Smoke.Chesterfield  && jp == Pet.Fox
                || hp == Pet.Fox && js == Smoke.Chesterfield) && Math.abs(hn - jn) == 1,
                h.smoke, h.pet, h.number, j.smoke, j.pet, j.number),
            houses)));

//      12. Kools are smoked in the house next to the house where the horse is kept.
        constraint(or(mapPair((h, j) -> variable(null, "KoolsNextToHorse",
            (hs, hp, hn, js, jp, jn) -> (hs == Smoke.Kools && jp == Pet.Horse
                || hp == Pet.Horse && js == Smoke.Kools) && Math.abs(hn - jn) == 1,
                h.smoke, h.pet, h.number, j.smoke, j.pet, j.number),
            houses)));

//      13. The Lucky Strike smoker drinks orange juice.
        constraint(or(map(h -> variable(null, "LuckyStrikeDrinksOrangeJuice",
            (x, y) -> x == Smoke.LuckyStrike && y == Drink.Orangejuice, h.smoke, h.drink),
            houses)));

//      14. The Japanese smokes Parliaments.
        constraint(or(map(h -> variable(null, "JapaneseSmokesParliament",
            (x, y) -> x == Nationality.Japanese && y == Smoke.Parliament, h.nationality, h.smoke),
            houses)));

//      15. The Norwegian lives next to the blue house.
        constraint(or(mapPair((h, j) -> variable(null, "NorwegianNextToBlue",
            (ha, hc, hn, ja, jc, jn) ->
                (ha == Nationality.Norwegian  && jc == Color.Blue
                || hc == Color.Blue && ja == Nationality.Norwegian)
                && Math.abs(hn - jn) == 1,
                h.nationality, h.color, h.number, j.nationality, j.color, j.number),
            houses)));
        
    }
    
    @Override
    public boolean answer(int n, Result result) throws IOException {
        for (House e : houses)
            log.info(e.toString(result));
        return true;
    }

    public static void main(String[] args) throws IOException {
		new ZebraPuzzle().parse(args).solve();
    }


}
