import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rhino.
 * Rhinos age, move, eat plants, breed, and die.
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
 * @version 2016.02.29 (2)
 */
public class Rhino extends Animal
{
    //class variables

    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 40;
    private static final double BREEDING_PROBABILITY = 0.4;
    private static final int MAX_LITTER_SIZE = 4;
    private static final int PLANT_FOOD_VALUE = 10;

    private static final Random rand = Randomizer.getRandom();
    
    // A shared male birth rate for animals of this species
    //private static final float maleBirthRate;
    
    //instance fields
    private int age;
    private int foodLevel;

    /**
     * Create a new rhino. A rhino may be created with age
     * zero (a new born) or with a random age.
     * 
     * This animal can be created with a random gender or one
     * that depends on the male birth rate of that species
     * 
     * @param randomAge If true, the rhino will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param clock The shared clock environment for all animals
     */
    public Rhino(boolean randomAge, Field field, Location location, Environment clock)
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
    }
    
        
    /**
     * This is what the rhino does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRhinos A list to return newly born rhinos.
     */
    public void act(List<Animal> newRhinos)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            
            giveBirth(newRhinos);            
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
     * Look for plants adjacent to the current location.
     * Only the first plant found is eaten.
     * @return Where a plant was found, or null if it wasn't.
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
     * This could result in the rhino's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this rhino is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRhinos A list to return newly born rhinos.
     */
    private void giveBirth(List<Animal> newRhinos)
    {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rhino young = new Rhino(false, field, loc, getClock());
            newRhinos.add(young);
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
     * A rhino can breed if it has reached the breeding age,
     * the adjacent animal is a male and the adjacent animal
     * is of the same type
     * @return true if the rhino can breed, false otherwise.
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
            if(animal instanceof Rhino){
                Rhino rhino = (Rhino) animal;
                if(rhino.getGender() == Gender.Male){
                    return true;
                }
                }
            }
        return false;
    }
    
    /**
     * Make this rhino more hungry. This could result in the rhino's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
}