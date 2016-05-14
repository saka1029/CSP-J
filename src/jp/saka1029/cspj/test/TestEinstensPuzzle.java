package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import jp.saka1029.cspj.geometry.Array;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestEinstensPuzzle {

    static final Logger logger = Logger.getLogger(TestEinstensPuzzle.class.getName());

    // define Color [blue | green | red | white | yellow];
    enum Color {
        Blue, Green, Red, White, Yellow
    }

    // define Nationality [Dane | Englishman | German | Swede | Norwegian];
    enum Nationality {
        Dane, Englishman, German, Swede, Norwegian
    }

    // define Drink [bier | coffee | milk |tea | water];
    enum Drink {
        Bier, Coffee, Milk, Tea, Water
    }

    // define Cigarette [Blend | BlueMaster | Dunhill | PallMall | Prince];
    enum Cigarette {
        Blend, BlueMaster, Dunhill, PallMall, Prince
    }

    // define Pet [birds | cats | dogs | fish | horses];
    enum Pet {
        Birds, Cats, Dogs, Fish, Horses
    }

    class House {
        final int name;
        final Variable<Color> color;
        final Variable<Nationality> nationality;
        final Variable<Drink> drink;
        final Variable<Cigarette> cigarette;
        final Variable<Pet> pet;

        House(Problem problem, int name) {
            this.name = name;
            color = problem.variable("color" + name, Domain.of(Color.values()));
            nationality = problem.variable("nationality" + name, Domain.of(Nationality.values()));
            drink = problem.variable("drink" + name, Domain.of(Drink.values()));
            cigarette = problem.variable("cigarette" + name, Domain.of(Cigarette.values()));
            pet = problem.variable("pet" + name, Domain.of(Pet.values()));
        }

        public String toString(Result r) {
            return String.format("hous %d is %s, %s, %s, %s, %s",
                name, r.get(color), r.get(nationality), r.get(drink), r.get(cigarette), r.get(pet));
        }

        @Override
        public String toString() {
            return Arrays.asList(name, color, nationality, drink, cigarette, pet).toString();
        }

    }

    Array<House> houses;

    void solve(Problem problem) {
        houses = Array.of(5, i -> new House(problem, i));
        allDifferent(map(h -> h.color, houses));
        allDifferent(map(h -> h.nationality, houses));
        allDifferent(map(h -> h.drink, houses));
        allDifferent(map(h -> h.cigarette, houses));
        allDifferent(map(h -> h.pet, houses));

        // The Englishman lives in the red house.
        constraint(or(map(h -> and(eq(h.nationality, Nationality.Englishman), eq(h.color, Color.Red)), houses)));

        // The Swede keeps dogs.
        constraint(or(map(h -> and(eq(h.nationality, Nationality.Swede), eq(h.pet, Pet.Dogs)), houses)));

        // The Dane drinks tea.
        constraint(or(map(h -> and(eq(h.nationality, Nationality.Dane), eq(h.drink, Drink.Tea)), houses)));

        // The green house is just to the left of the white one.
        constraint(or(mapNeighbor((a, b) -> and(eq(a.color, Color.Green), eq(b.color, Color.White)), houses)));

        // The owner of the green house drinks coffee.
        constraint(or(map(h -> and(eq(h.color, Color.Green), eq(h.drink, Drink.Coffee)), houses)));

        // The Pall Mall smoker keeps birds.
        constraint(or(map(h -> and(eq(h.cigarette, Cigarette.PallMall), eq(h.pet, Pet.Birds)), houses)));

        // The owner of the yellow house smokes Dunhills.
        constraint(or(map(h -> and(eq(h.color, Color.Yellow), eq(h.cigarette, Cigarette.Dunhill)), houses)));

        // The man in the center house drinks milk.
        constraint(eq(houses.get(2).drink, Drink.Milk));

        // The Norwegian lives in the first house.
        constraint(eq(houses.get(0).nationality, Nationality.Norwegian));

        // The Blend smoker has a neighbor who keeps cats.
        constraint(or(mapNeighbor((a, b) -> or(and(eq(a.cigarette, Cigarette.Blend), eq(b.pet, Pet.Cats)),
            and(eq(a.pet, Pet.Cats), eq(b.cigarette, Cigarette.Blend))), houses)));
//        constraint(or(map2((a, b) -> and(eq(a.cigarette, Cigarette.Blend), eq(b.pet, Pet.Cats)), houses)));

        // The man who smokes Blue Masters drinks bier.
        constraint(or(map(h -> and(eq(h.cigarette, Cigarette.BlueMaster), eq(h.drink, Drink.Bier)), houses)));

        // The man who keeps horses lives next to the Dunhill smoker.
        constraint(or(mapNeighbor((a, b) -> or(and(eq(a.pet, Pet.Horses), eq(b.cigarette, Cigarette.Dunhill)),
            and(eq(a.cigarette, Cigarette.Dunhill), eq(b.pet, Pet.Horses))), houses)));

        // The German smokes Prince.
        constraint(or(map(h -> and(eq(h.nationality, Nationality.German), eq(h.cigarette, Cigarette.Prince)), houses)));

        // The Norwegian lives next to the blue house.
        constraint(or(mapNeighbor((a, b) -> or(and(eq(a.nationality, Nationality.Norwegian), eq(b.color, Color.Blue)),
            and(eq(a.color, Color.Blue), eq(b.nationality, Nationality.Norwegian))), houses)));

        // The Blend smoker has a neighbor who drinks water.
        constraint(or(mapNeighbor((a, b) -> or(and(eq(a.cigarette, Cigarette.Blend), eq(b.drink, Drink.Water)),
            and(eq(a.drink, Drink.Water), eq(b.cigarette, Cigarette.Blend))), houses)));

        Solver solver = new BasicSolver();
        int[] count = {0};
        solver.solve(problem, result -> {
            for (House h : houses)
                logger.info(h.toString(result));
            ++count[0];
            return true;
        });
        assertEquals(1, count[0]);
    }

    @Test
    public void test() {
        solve(new Problem());
    }

}
