package proto;

/**
 * All classes of the proto package must implement the Preference interface
 * even if component-modifications are not compulsory on them -
 * as the case of the FontFactory which is abstract, relieving itself of this duty.
 */
interface Preference {

    /**
     * Sets Dashboard specific styles on the target component.
     * Constructors of all implementors must delegate to this method
     * to initialize them as Dashboard specific components.
     */
    void setPreferences();

}
