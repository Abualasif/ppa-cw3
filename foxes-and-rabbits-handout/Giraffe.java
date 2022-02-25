import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Haroon Yasin, Rahi Al-Asif and Mohammed Kazi
 * @version 2016.02.29 (2)
 */
public class Giraffe extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    private static final int PLANT_FOOD_VALUE = 10;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A shared male birth rate for animals of this species
    //private static final float maleBirthRate;

    // Individual characteristics (instance fields).
    
    // The rabbit's age.
    private int age;
    private int foodLevel;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * This animal can be created with a random gender or one
     * that depends on the male birth rate of that species
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Giraffe(boolean randomAge, Field field, Location location, Environment clock)
    {
        super(field, location, clock);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
        this.setGender();
    }
    
        
    /**
     * This is what the giraffe does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGiraffes A list to return newly born rabbits.
     */
    public void act(List<Animal> newGiraffes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            
            giveBirth(newGiraffes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Look for food (Plants) adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) { 
                    plant.setDead();
                    foodLevel = PLANT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Increase the age.
     * This could result in the giraffe's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this giraffe is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGiraffes A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newGiraffes)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Giraffe young = new Giraffe(false, field, loc, getClock());
            newGiraffes.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * checks the gender and type of adjacent animals
     * @return true if adjacent animal is of the same type and is gender male
     */
    private boolean isCompatibleAnimal()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Giraffe){
                Giraffe giraffe = (Giraffe) animal;
                if(giraffe.getGender() == Gender.Male){
                    return true;
                }
                }
            }
        return false;
    }

    /**
     * A giraffe can breed if it has reached the breeding age.
     * @return true if the giraffe can breed, false otherwise.
     */
    private boolean canBreed()
    {
        if(gender == Gender.Male){
            return false;
        }
        else{
            boolean bool = (age > BREEDING_AGE) && isCompatibleAnimal();
            return bool;
        }
    }
    
    /**
     * Make this giraffe more hungry. This could result in the giraffe's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
}
