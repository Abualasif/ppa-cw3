import java.util.List;
import java.util.Random;
import java.util.Iterator;


/**
 * A simple model of a baboon.
 * Baboons age, move, breed, eat plants and die.
 * 
 * @author David J. Barnes, Michael Kölling, Haroon Yasin, Rahi Al-Asif and Mohammed Kazi
 * @version 2016.02.29 (2)
 */
public class Baboon extends Animal
{
    // class variables

    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 40;
    private static final double BREEDING_PROBABILITY = 0.07;
    private static final int MAX_LITTER_SIZE = 4;
    private static final int PLANT_FOOD_VALUE = 10;
    private static final Random rand = Randomizer.getRandom();
    
    // A shared male birth rate for animals of this species
    //private static final float maleBirthRate;

    //instance fields
    private int age;
    private int foodLevel;

    /**
     * Create a new baboon. A baboon may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the baboon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Baboon(boolean randomAge, Field field, Location location, Environment clock)
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
     * This is what the baboon does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newBaboons A list to return newly born baboons.
     */
    public void act(List<Animal> newBaboons)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBaboons);            
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
     * This could result in the baboon's death
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this baboon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBaboons A list to return newly born baboons.
     */
    private void giveBirth(List<Animal> newBaboons)
    {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Baboon young = new Baboon(false, field, loc, getClock());
            newBaboons.add(young);
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
     * A baboon can breed if it has reached the breeding age.
     * @return true if the baboon can breed, false otherwise.
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
     * Checks whether a female can breed with animals around it
     * @return true if adjacent baboon (if found) is male 
     */
    private boolean isCompatibleAnimal()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Baboon){
                Baboon baboon = (Baboon) animal;
                if(baboon.getGender() == Gender.Male){
                    return true;
                }
                }
            }
        return false;
    }
    
    /**
     * Make this baboon more hungry. This could result in the baboon's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

}