package net.icegem.stuffapp.exceptions;

/**
 * Created by mikael.korpela on 12.5.2015.
 */
public class ItemAlreadyExistsException extends Exception
{
    public ItemAlreadyExistsException(String description)
    {
        super("ItemAlreadyExistsException: " + description );
    }
}
