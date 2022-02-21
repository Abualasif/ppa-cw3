import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */

public abstract class Animal
{

    // Enum to represent the animals gender
    enum Gender{
        Male,
        Female;
    }

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // The animal's gender
    protected Gender gender;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Randomly generate the animals gender with no seed
     * checks a random float between 0 and 1 to 0.5 to 
     * make the gender of the animal male or female
     */
    protected void setGender(){
        this(0.5);
    }

    /**
     * Overloaded Method
     * Randomly generate the animals gender with a seed
     * value (maleBirthRate), checks this to a random 
     * float between 0 and 1 to make the animals gender
     * male or female
     * @param maleBirthRate the rate of birth for males in
     * that animal
     */
    protected void setGender(float maleBirthRate){
        Random rand = Randomizer.getRandom();
        if(rand.nextFloat() < maleBirthRate){
            gender = Gender.Male;
        }
        else{
            gender = Gender.Female;
        }
    }

    /**
     * Returns the gender of a given animal
     */
    protected Gender getGender(){
        return gender;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
