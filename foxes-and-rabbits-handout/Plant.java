import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A model of a Plant
 * Plants age, get eaten and give rise to new plants,
 * but they do not move in the field.
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
 * @version 2016.02.29 (2)
 */
public class Plant extends Animal
{

    private static final int BREEDING_AGE = 1;
    private static final int MAX_AGE = 60;
    private static final double BREEDING_PROBABILITY = 0.5;
    private static final int MAX_LITTER_SIZE = 2;
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    private int age;

	/**
     * Create a new plant. A plant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param clock The shared clock environment for all animals
     */
    public Plant(boolean randomAge, Field field, Location location, Environment clock)
    {
        super(field, location, clock);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

	/**
     * This is what the plant does most of the time - it will 
     * breed.
     * The plant my also die out if it is too old.
     * @param newPlants A list to return newly born plants.
     */
    public void act(List<Animal> newPlants)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newPlants);            
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
            
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in plant dying out.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    private void giveBirth(List<Animal> newPlants)
    {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(false, field, loc, getClock());
            newPlants.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of new plants,
     * if it can breed.
     * @return The number of new plants (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    private boolean canBreed()
    {
        boolean bool = (age > BREEDING_AGE) && isCompatibleAnimal();
        return bool;
    }

    /**
     * checks if there is a plant next to it that it can breed with
     * @return true if any adjacent location contains a plant
     */

    private boolean isCompatibleAnimal()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Plant){
                    return true;
                }
            }
        return false;
    }

}