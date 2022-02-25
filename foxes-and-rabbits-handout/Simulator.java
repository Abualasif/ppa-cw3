import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing 2 predators, 3 Prey and a plant species.
 * 
 * @author David J. Barnes, Michael Kölling, Haroon Yasin, Rahi Al-Asif and Mohammed Kazi
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    
    // The probability that each species will be created in any given grid position.
    private static final double VULTURE_CREATION_PROBABILITY = 0.01;
    private static final double FOX_CREATION_PROBABILITY = 0.01;
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.02;
    private static final double BABOON_CREATION_PROBABILITY = 0.02;
    private static final double RHINO_CREATION_PROBABILITY = 0.4;
    private static final double PLANT_CREATION_PROBABILITY = 0.2;

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // represents the hour of day. All animals hold a reference to this.
    private Environment clock;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        // initialise the shared clock
        clock = new Environment();
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

        view.setColor(Lion.class, Color.RED);
        view.setColor(Vulture.class, Color.BLUE); 
        
        view.setColor(Rhino.class, Color.MAGENTA);
        view.setColor(Giraffe.class, Color.CYAN);
        view.setColor(Baboon.class, Color.GREEN);

        view.setColor(Plant.class, Color.BLACK);
        
        // Setup a valid starting point.
        System.out.println("Simulation created.");
        reset();
    }

    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        clock.incrementHourOfDay();
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with all species
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= VULTURE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Vulture vulture = new Vulture(true, field, location, clock);
                    animals.add(vulture);
                }
                else if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location, clock);
                    animals.add(lion);
                }
                else if (rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Giraffe giraffe = new Giraffe(true, field, location, clock);
                    animals.add(giraffe);
                }
                else if(rand.nextDouble() <= BABOON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Baboon baboon = new Baboon(true, field, location, clock);
                    animals.add(baboon);
                }
                else if(rand.nextDouble() <= RHINO_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rhino rhino = new Rhino(true, field, location, clock);
                    animals.add(rhino);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location, clock);
                    animals.add(plant);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
