import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a vulture.
 * Vulture age, move, eat prey (Rhinos/Baboons), and die.
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
 * @version 2016.02.29 (2)
 */
public class Vulture extends Animal
{
    // class variables
    private static final int BREEDING_AGE = 15;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 2;
    
    // The food values of each prey. In effect, these is the
    // number of steps a vulture can go before it has to eat again.
    private static final int GIRAFFE_FOOD_VALUE = 9;
    private static final int RHINO_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A shared male birth rate for animals of this species
    //private static final float maleBirthRate;

    //instance fields
    private int age;
    private int foodLevel;

    /**
     * Create a vulture. A vulture can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * This animal can be created with a random gender or one
     * that depends on the male birth rate of that species
     * 
     * @param randomAge If true, the vulture will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Vulture(boolean randomAge, Field field, Location location, Environment clock)
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
    }
    
    /**
     * This is what the vultures does most of the time: it hunts for
     * prey. In the process, it might: breed, die of hunger/old age or 
     * be unable to hunt due to weather (FOG)
     * @param field The field currently occupied.
     * @param newVultures A list to return newly born vultures.
     */
    public void act(List<Animal> newVultures)
    {
        incrementAge();
        incrementHunger();
        Location newLocation = null;
        if(isAlive()) {
            if (shouldSleep()) {
                // maintain hunger level
                decrementHunger();
                return;
            }
            else if (isAffectedByWeather()) {
                // vulture can't hunt prey in fog
                decrementHunger();
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            else {
                giveBirth(newVultures);            
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
     * Increase the age. This could result in the vulture's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this vulture more hungry. This could result in the vulture's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Give this vulture one food unit.
     * This is used to maintain hunger when Lion is sleeping
     */
    private void decrementHunger()
    {
        foodLevel++;
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
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
            
            if (animal instanceof Baboon) {
                Baboon baboon = (Baboon) animal;
                if(baboon.isAlive()) { 
                    baboon.setDead();
                    foodLevel = GIRAFFE_FOOD_VALUE;
                    return where;
                }    
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this vulture is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newVultures A list to return newly born vultures.
     */
    private void giveBirth(List<Animal> newVultures)
    {
        // New vultures are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Vulture young = new Vulture(false, field, loc, getClock());
            newVultures.add(young);
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
     * A vulture can breed if it has reached the breeding age.
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
            if(animal instanceof Vulture){
                Vulture vulture = (Vulture) animal;
                if(vulture.getGender() == Gender.Male){
                    return true;
                }
                
                }
            }
            return false;
    }
    
    /**
     * Check if a vulture is affected by weather conditions
     * Vultures are affected (i.e unable to move/eat) by FOG ONLY
     */
    public boolean isAffectedByWeather() 
    {
        Environment.Weather weather = getClock().getCurrentWeather();

        boolean isAffectedByWeather = (weather == Environment.Weather.FOG);
        return isAffectedByWeather;
    }
    
    /**
     * Checks whether a vulture would sleep by checking if the 
     * hour of day falls withing designated sleeping hours
     * @return if a vulture should sleep
     */
    private boolean shouldSleep() 
    {
        int hourOfDay = getClock().getHourOfDay();
        
        // Lions should sleep between 18:00 and 23:00
        return hourOfDay >= 18 && hourOfDay <= 23;
    }
}