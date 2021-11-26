package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Classe non instanciable avec des méthodes statiques 
 * permettant de travailler sur des vecteurs de 64 bits 
 * stockés dans des valeurs de type long.
 *
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 */
public final class Bits64 {

    private Bits64() {
    }

    /**
     * Construit un masque
     *
     * @param start: Le bit de départ du masque (en partant du bit le plus faible).
     * @param size: La longueur du masque(le nombre de bits du masque).
     *            
     * @throws IllegalArgumentException si start et size ne désignent pas une valeur
     *         comprise entre 0 et 64, inclus.
     *
     * @return Un long dont les bits d'index allant de start (inclus)
     *         à start + size (exclus) valent 1, les autres valant 0.
     */
    public static long mask(int start, int size)
            throws IllegalArgumentException {

        Preconditions.checkArgument(
                start >= 0 && size >= 0 && start + size <= Long.SIZE);

        if (size != Long.SIZE) {
            return ((1L << size) - 1) << start;
        } else {
            return (~0L);// Masque de 1
        }
    }

    /**
     * Extrait un certain nombre de bits d'un long.
     *
     * @param bits: Le long dont on veut extraire les bits
     * @param start: Le bit de départ depuis lequel on veut 
     *               extraire les bits (en partant du bit le plus faible).
     * @param size: La longueur de l'extraction de bit(le nombre de bits extraits).
     *            
     * @throws IllegalArgumentException si start et size ne désignent pas une valeur
     *         comprise entre 0 et 64, inclus.
     *
     * @return Un long dont les size bits de poids faible sont égaux à ceux de
     *         bits allant de l'index start (inclus) à l'index start + size (exclus).
     */
    public static long extract(long bits, int start, int size)
            throws IllegalArgumentException {

        return (bits & mask(start, size)) >>> start;

    }

    /**
     * Empaquete 2 long.
     *
     * @param v1: Le premier long à empaqueter.
     * @param s1: La longueur du premier long (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v2: Le deuxième long à empaqueter.
     * @param s2: La longueur du deuxième long (le nombre de bits que l'on veut empaqueter de cet entier).
     *            
     * @throws IllegalArgumentException si l'une des tailles (s1, s2) n'est pas comprise 
     *         entre 1 (inclus) et 64 (exclus), si l'une des valeurs occupe plus de bits
     *         que sa taille, ou si la somme des tailles est supérieure à 64.
     *
     * @return  Un entier empaquetant les valeurs v1 et v2, v1 occupant les s1 bits de 
     *          poids faible et v2 occupant les s2 bits suivants, tous les autres bits valant 0.
     */
    public static long pack(long v1, int s1, long v2, int s2)
            throws IllegalArgumentException {

        Preconditions.checkArgument(checkArgument(v1, s1)
                && checkArgument(v2, s2) && s1 + s2 <= Long.SIZE);
        return (extract(v1, 0, s1)) | (extract(v2, 0, s2) << s1);
    }

    /**
     * Contrôle que les valeurs passés en arguments respectent un certain format.
     *
     * @param v: La valeur.
     * @param s: La taille.
     *
     * @return Un boolean valant true si la taille est comprise entre 1 (inclus) et 64 (exclus) et
     *         que la valeur n'occupe pas plus de bits que le nombre spécifié par sa taille.
     */
    private static boolean checkArgument(long v, int s) {
        return (s < Long.SIZE && s > 0 && (v >>> s) == 0);
    }

}
