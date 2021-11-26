package ch.epfl.javass;

/**
 * Classe non instanciable avec des méthodes utilitaires statiques 
 * permettant de contrôler des valeurs et lever des exceptions.
 *
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Contrôle la valeur d'un booléen.
     *
     * @param b: Le booléen à contrôler.
     *            
     * @throws IllegalArgumentException si le booléen vaut false.
     */
    public static void checkArgument(boolean b)
            throws IllegalArgumentException {

        if (!b) {

            throw new IllegalArgumentException();
        }
    }

    /**
     * Contrôle si un index et une taille représentent une plage de valeurs correctes.
     *
     * @param index: L'index.
     * @param size: La taille.
     * 
     * @throws IndexOutOfBoundsException si l'index est négatif ou plus grand ou égal à la taille.
     * 
     * @return L'indexe
     */
    public static int checkIndex(int index, int size)
            throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        } else {
            return index;
        }
    }
}
