package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import jp.saka1029.cspj.geometry.Array;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestEinstensPuzzle {

	static final Logger logger = Logger.getLogger(TestEinstensPuzzle.class.getName());

    // define Color [blue | green | red | white | yellow];
    enum Color { Blue, Green, Red, White, Yellow }

    // define Nationality [Dane | Englishman | German | Swede | Norwegian];
    enum Nationality { Dane, Englishman, German, Swede, Norwegian }

    // define Drink [bier | coffee | milk |tea | water];
    enum Drink { Bier, Coffee, Milk, Tea, Water }

    // define Cigarette [Blend | BlueMaster | Dunhill | PallMall | Prince];
    enum Cigarette { Blend, BlueMaster, Dunhill, PallMall, Prince }

    // define Pet [birds | cats | dogs | fish | horses];
    enum Pet { Birds, Cats, Dogs, Fish, Horses }

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
        constraint(or(and(eq(houses.get(0).color, Color.Green), eq(houses.get(1).color, Color.White)),
            and(eq(houses.get(1).color, Color.Green), eq(houses.get(2).color, Color.White)),
            and(eq(houses.get(2).color, Color.Green), eq(houses.get(3).color, Color.White)),
            and(eq(houses.get(3).color, Color.Green), eq(houses.get(4).color, Color.White))));

//        List<Variable<Color>> colors = map(h -> h.color, houses);
//        constraint(or(mapNeighbor("greenWhite", (a, b) -> a == Color.Green && b == Color.White, colors)));

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
        constraint(or(and(eq(houses.get(0).cigarette, Cigarette.Blend), eq(houses.get(1).pet, Pet.Cats)),
            and(eq(houses.get(1).cigarette, Cigarette.Blend), or(eq(houses.get(0).pet, Pet.Cats), eq(houses.get(2).pet, Pet.Cats))),
            and(eq(houses.get(2).cigarette, Cigarette.Blend), or(eq(houses.get(1).pet, Pet.Cats), eq(houses.get(3).pet, Pet.Cats))),
            and(eq(houses.get(3).cigarette, Cigarette.Blend), or(eq(houses.get(2).pet, Pet.Cats), eq(houses.get(4).pet, Pet.Cats))),
            and(eq(houses.get(4).cigarette, Cigarette.Blend), eq(houses.get(3).pet, Pet.Cats))));

        // The man who smokes Blue Masters drinks bier.
        constraint(or(map(h -> and(eq(h.cigarette, Cigarette.BlueMaster), eq(h.drink, Drink.Bier)), houses)));

        // The man who keeps horses lives next to the Dunhill smoker.
        constraint(or(and(eq(houses.get(0).pet, Pet.Horses), eq(houses.get(1).cigarette, Cigarette.Dunhill)),
            and(eq(houses.get(1).pet, Pet.Horses), or(eq(houses.get(0).cigarette, Cigarette.Dunhill), eq(houses.get(2).cigarette, Cigarette.Dunhill))),
            and(eq(houses.get(2).pet, Pet.Horses), or(eq(houses.get(1).cigarette, Cigarette.Dunhill), eq(houses.get(3).cigarette, Cigarette.Dunhill))),
            and(eq(houses.get(3).pet, Pet.Horses), or(eq(houses.get(2).cigarette, Cigarette.Dunhill), eq(houses.get(4).cigarette, Cigarette.Dunhill))),
            and(eq(houses.get(4).pet, Pet.Horses), eq(houses.get(3).cigarette, Cigarette.Dunhill))));

        // The German smokes Prince.
        constraint(or(map(h -> and(eq(h.nationality, Nationality.German), eq(h.cigarette, Cigarette.Prince)), houses)));

        // The Norwegian lives next to the blue house.
        constraint(or(and(eq(houses.get(0).nationality, Nationality.Norwegian), eq(houses.get(1).color, Color.Blue)),
            and(eq(houses.get(1).nationality, Nationality.Norwegian), or(eq(houses.get(0).color, Color.Blue), eq(houses.get(2).color, Color.Blue))),
            and(eq(houses.get(2).nationality, Nationality.Norwegian), or(eq(houses.get(1).color, Color.Blue), eq(houses.get(3).color, Color.Blue))),
            and(eq(houses.get(3).nationality, Nationality.Norwegian), or(eq(houses.get(2).color, Color.Blue), eq(houses.get(4).color, Color.Blue))),
            and(eq(houses.get(4).nationality, Nationality.Norwegian), eq(houses.get(3).color, Color.Blue))));

        // The Blend smoker has a neighbor who drinks water.
        constraint(or(and(eq(houses.get(0).cigarette, Cigarette.Blend), eq(houses.get(1).drink, Drink.Water)),
            and(eq(houses.get(1).cigarette, Cigarette.Blend), or(eq(houses.get(0).drink, Drink.Water), eq(houses.get(2).drink, Drink.Water))),
            and(eq(houses.get(2).cigarette, Cigarette.Blend), or(eq(houses.get(1).drink, Drink.Water), eq(houses.get(3).drink, Drink.Water))),
            and(eq(houses.get(3).cigarette, Cigarette.Blend), or(eq(houses.get(2).drink, Drink.Water), eq(houses.get(4).drink, Drink.Water))),
            and(eq(houses.get(4).cigarette, Cigarette.Blend), eq(houses.get(3).drink, Drink.Water))));

        Solver solver = new BasicSolver();
        solver.solve(problem, new Answer() {

            @Override
            public boolean answer(Result result) {
                for (House h : houses)
                    logger.info(h.toString(result));
                return true;
            }
            
        });
    }

    @Test
    public void test() {
        solve(new Problem());
    }

}
