package cmanager.gui;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Adapter for handling menu events.
 *
 * <p>This just contains some empty handlers intended for reducing the amount of code duplicates in
 * the actual GUI class. You should override the corresponding methods when using it.
 */
public class MenuAdapter implements MenuListener {

    /**
     * Invoked when the menu is canceled.
     *
     * @param menuEvent The corresponding event.
     */
    @Override
    public void menuCanceled(final MenuEvent menuEvent) {}

    /**
     * Invoked when the menu is deselected.
     *
     * @param menuEvent The corresponding event.
     */
    @Override
    public void menuDeselected(final MenuEvent menuEvent) {}

    /**
     * Invoked when the menu is selected.
     *
     * @param menuEvent The corresponding event.
     */
    @Override
    public void menuSelected(final MenuEvent menuEvent) {}
}
