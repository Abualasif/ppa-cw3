import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Lion extends Animal
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a fox can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int GIRAFFE_FOOD_VALUE = 10;
    private static final int RHINO_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A shared male birth rate for animals of this species
    //private static final float maleBirthRate;

    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;
    // Enum for gender, maybe put in animal class
    

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * This animal can be created with a random gender or one
     * that depends on the male birth rate of that species
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location, Clock clock)
    {
        super(field, location, clock);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RHINO_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RHINO_FOOD_VALUE;
        }
        this.setGender();
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newFoxes)
    {
        incrementAge();
        incrementHunger();
        Location newLocation = null;
        if(isAlive()) {
            if (shouldSleep()) {
                // don't move and maintain hunger level
                decrementHunger();
            } 
            else if (isAffectedByWeather()) {
                // unable to hunt prey in fog/heavy rain
                decrementHunger();
                newLocation = getField().freeAdjacentLocation(getLocation());
            } 
            else {
                giveBirth(newFoxes);            
                // Move towards a source of food if found.
                newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
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
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Give this lion one food unit.
     * This is used to maintain hunger when Lion is sleeping
     */
    private void decrementHunger()
    {
        foodLevel++;
    }
    
    /**
     * Look for rabbits adjacent to the current location.
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
            if(animal instanceof Rhino) {
                Rhino rhino = (Rhino) animal;
                if(rhino.isAlive()) { 
                    rhino.setDead();
                    foodLevel = RHINO_FOOD_VALUE;
                    return where;
                }
            }
            
            if (animal instanceof Giraffe) {
                Giraffe giraffe = (Giraffe) animal;
                if(giraffe.isAlive()) { 
                    giraffe.setDead();
                    foodLevel = GIRAFFE_FOOD_VALUE;
                    return where;
                }    
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc, getClock());
            newFoxes.add(young);
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
     * A fox can breed if it has reached the breeding age.
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
            if(animal instanceof Lion){
                Lion lion = (Lion) animal;
                if(lion.getGender() == Gender.Male){
                    return true;
                }
                }
            }
        return false;
    }
    
    /**
     * Check if a lion is affected by weather conditions
     * Lions are affected by fog and rain
     * A lion that is affected can only move to a random adjacent location
     * and unable to look for prey
     */
    public boolean isAffectedByWeather() 
    {
        Clock.Weather weather = getClock().getCurrentWeather();

        boolean isAffectedByWeather = (weather == Clock.Weather.FOG || weather == Clock.Weather.RAIN);
        return isAffectedByWeather;
    }
    
    /**
     * Checks whether a lion should sleep by checking if the 
     * hour of day falls withing designated sleeping hours
     * @return if a lion should sleep
     */
    private boolean shouldSleep() 
    {
        int hourOfDay = getClock().getHourOfDay();
        
        // Lions should sleep between 18:00 and 23:00
        return hourOfDay >= 18 && hourOfDay <= 23;
    }
}