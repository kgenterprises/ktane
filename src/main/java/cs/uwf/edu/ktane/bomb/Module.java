package cs.uwf.edu.ktane.bomb;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author daniel.jermyn
 * An abstract class that will act as the superclass for all modules
 */
public abstract class Module
{
	/**
	 * The name of the module
	 */
	@Getter
	@Setter
	private String name;
	
	/**
	 * Parameterized constructor
	 * @param theName the name of the module
	 */
	public Module(String theName)
	{
		setName(theName);
	}
	
	/**
	 * Default constructor
	 */
	public Module()
	{
		setName("No Name");
	}
	
	/**
	 * Method that must be inherited by all subclasses
	 */
	public abstract void solve();

}
