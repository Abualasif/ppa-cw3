import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.lang.System;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing 2 predators, 3 Prey and a plant species.
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
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
    private double VULTURE_CREATION_PROBABILITY;
    private double LION_CREATION_PROBABILITY;

    private double GIRAFFE_CREATION_PROBABILITY;
    private double BABOON_CREATION_PROBABILITY;
    private double RHINO_CREATION_PROBABILITY;
    
    private double PLANT_CREATION_PROBABILITY;

    private static final Double[] defaultSpawnParams = {0.1,0.1,0.15,0.15,0.15,0.18};

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    private int stepCount;
    private int inputSteps;
    // A graphical view of the simulation.
    private SimulatorView view;
    // represents the clock (which is tied to weather)...
    // ... All animals hold a reference to this.
    private Environment clock;

    private boolean showView;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, defaultSpawnParams, true);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width, Double[] spawnParams, boolean showGUI)
    {

        showView = showGUI;

        VULTURE_CREATION_PROBABILITY = spawnParams[0];
        LION_CREATION_PROBABILITY = spawnParams[1];

        GIRAFFE_CREATION_PROBABILITY = spawnParams[2];
        BABOON_CREATION_PROBABILITY = spawnParams[3];
        RHINO_CREATION_PROBABILITY = spawnParams[4];

        PLANT_CREATION_PROBABILITY = spawnParams[5];

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
        view = new SimulatorView(depth, width, showGUI);

        view.setColor(Lion.class, Color.RED, "RED");
        view.setColor(Vulture.class, Color.BLUE, "BLUE"); 
        
        view.setColor(Rhino.class, Color.MAGENTA, "MAGENTA");
        view.setColor(Giraffe.class, Color.CYAN, "CYAN");
        view.setColor(Baboon.class, Color.GREEN, "GREEN");

        view.setColor(Plant.class, Color.BLACK, "BLACK");
        
        reset();
    }

    /**
     * Run the simulation from its current state for a number of steps
     * @param steps the number of steps to run the simulation
     */
    public void runLongSimulation(int steps)
    {
        inputSteps = steps;
        simulate(steps);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        int step = 1;
        while(step <= numSteps && view.isViable(field)) {
            view.updateInternalField(field);
            while(view.getIfPaused() && view.isViable(field)){
                view.updateInternalField(field);
                if(view.getSingleStep()){
                    simulateOneStep();
                    view.negateSingleStep();
                }
            stepCount++;
            }
            simulateOneStep();
            //delay(120);   // uncomment this to run more slowly
            step++;
        }

    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * species
     */
    public void simulateOneStep()
    {
        step++;
        clock.incrementHourOfDay();
        
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals "act"
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add all newly born species to the main lists.
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
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
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
    
    public boolean isSimValuesViable(){
        return (view.isViable(field) && (stepCount == inputSteps));
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
