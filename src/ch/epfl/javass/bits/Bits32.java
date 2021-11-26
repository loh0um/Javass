package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Classe non instanciable avec des méthodes statiques 
 * permettant de travailler sur des vecteurs de 32 bits 
 * stockés dans des valeurs de type int.
 *
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 */
public final class Bits32 {

    private Bits32() {
    }

    /**
     * Construit un masque
     *
     * @param start: Le bit de départ du masque (en partant du bit le plus faible).
     * @param size: La longueur du masque(le nombre de bits du masque).
     *            
     * @throws IllegalArgumentException si start et size ne désignent pas une valeur
     *         comprise entre 0 et 32, inclus.
     *
     * @return Un entier dont les bits d'index allant de start (inclus)
     *         à start + size (exclus) valent 1, les autres valant 0.
     */
    public static int mask(int start, int size)
            throws IllegalArgumentException {

        Preconditions.checkArgument(
                start >= 0 && size >= 0 && start + size <= Integer.SIZE);

        if (size != Integer.SIZE) {
            return ((1 << size) - 1) << start;
        } else {
            return (~0);// Masque de 1
        }
    }

    /**
     * Extrait un certain nombre de bits d'un entier.
     *
     * @param bits: L'entier dont on veut extraire les bits
     * @param start: Le bit de départ depuis lequel on veut 
     *               extraire les bits (en partant du bit le plus faible).
     * @param size: La longueur de l'extraction de bit(le nombre de bits extraits).
     *            
     * @throws IllegalArgumentException si start et size ne désignent pas une valeur
     *         comprise entre 0 et 32, inclus.
     *
     * @return Un entier dont les size bits de poids faible sont égaux à ceux de
     *         bits allant de l'index start (inclus) à l'index start + size (exclus).
     */
    public static int extract(int bits, int start, int size)
            throws IllegalArgumentException {

        return (bits & mask(start, size)) >>> start;

    }

    /**
     * Empaquete 2 entiers.
     *
     * @param v1: Le premier entier à empaqueter.
     * @param s1: La longueur du premier entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v2: Le deuxième entier à empaqueter.
     * @param s2: La longueur du deuxième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     *            
     * @throws IllegalArgumentException si l'une des tailles (s1, s2) n'est pas comprise 
     *         entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits
     *         que sa taille, ou si la somme des tailles est supérieure à 32.
     *
     * @return  Un entier empaquetant les valeurs v1 et v2, v1 occupant les s1 bits de 
     *          poids faible et v2 occupant les s2 bits suivants, tous les autres bits valant 0.
     */
    public static int pack(int v1, int s1, int v2, int s2)
            throws IllegalArgumentException {

        Preconditions.checkArgument(checkArgument(v1, s1)
                && checkArgument(v2, s2) && s1 + s2 <= Integer.SIZE);
        return (extract(v1, 0, s1)) | (extract(v2, 0, s2) << s1);
    }

    /**
     * Empaquete 3 entiers (surcharge de pack).
     *
     * @param v1: Le premier entier à empaqueter.
     * @param s1: La longueur du premier entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v2: Le deuxième entier à empaqueter.
     * @param s2: La longueur du deuxième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v3: Le troisième entier à empaqueter.
     * @param s3: La longueur du troisième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     *            
     * @throws IllegalArgumentException si l'une des tailles (s1, s2, s3) n'est pas comprise 
     *         entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits
     *         que sa taille, ou si la somme des tailles est supérieure à 32.
     *
     * @return  Un entier empaquetant les valeurs v1, v2 et v3, v1 occupant les s1 bits de 
     *          poids faible, v2 occupant les s2 bits suivants et v3 les s3 bits suivants,
     *          tous les autres bits valant 0.
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3)
            throws IllegalArgumentException {
        return pack(pack(v1, s1, v2, s2), s1 + s2, v3, s3);
    }

    /**
     * Empaquete 7 entiers (surcharge de pack).
     *
     * @param v1: Le premier entier à empaqueter.
     * @param s1: La longueur du premier entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v2: Le deuxième entier à empaqueter.
     * @param s2: La longueur du deuxième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v3: Le troisième entier à empaqueter.
     * @param s3: La longueur du troisième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v4: Le quatrième entier à empaqueter.
     * @param s4: La longueur du quatrième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v5: Le cinquième entier à empaqueter.
     * @param s5: La longueur du cinquième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v6: Le sixième entier à empaqueter.
     * @param s6: La longueur du sixième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     * @param v7: Le septième entier à empaqueter.
     * @param s7: La longueur du septième entier (le nombre de bits que l'on veut empaqueter de cet entier).
     *            
     * @throws IllegalArgumentException si l'une des tailles (s1, s2, s3, s4, s5, s6, s7) n'est pas
     *         comprise entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits
     *         que sa taille, ou si la somme des tailles est supérieure à 32.
     *
     * @return  Un entier empaquetant les valeurs v1, v2, v3, v4, v5, v6 et v7, v1 occupant les s1
     *          bits de poids faible, v2 occupant les s2 bits suivants, v3 les s3 bits suivants, 
     *          v4 les s4 bits suivants, v5 les s5 bits suivants, v6 les s6 bits suivants et v7 les
     *          s7 bits suivants, tous les autres bits valant 0.
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7)
            throws IllegalArgumentException {
        return pack(pack(pack(v1, s1, v2, s2, v3, s3), s1 + s2 + s3, v4, s4, v5,
                s5), s1 + s2 + s3 + s4 + s5, v6, s6, v7, s7);
    }

    /**
     * Contrôle que les valeurs passés en arguments respectent un certain format.
     *
     * @param v: La valeur.
     * @param s: La taille.
     *
     * @return Un boolean valant true si la taille est comprise entre 1 (inclus) et 32 (exclus) et
     *         que la valeur n'occupe pas plus de bits que le nombre spécifié par sa taille.
     */
    private static boolean checkArgument(int v, int s) {
        return (s < Integer.SIZE && s > 0 && (v >>> s) == 0);
    }
}
