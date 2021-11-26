package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import ch.epfl.javass.jass.Card.Color;

/**
 * Classe représentant un joueur simulé par l'ordinateur jouant avec
 * l'algorithme MCTS(Monte Carlo Tree Search).
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class MctsPlayer implements Player {
    /**
     * Constante ayant un impact sur la recherche de l'algorithme (à plus C est
     * grand, à plus l'algorithme explore de nouvelles parties de l'arbre, à
     * plus C est petit, à plus il reste dans la partie de l'arbre la plus
     * performante).
     */
    private static final int C = 40;

    private static final int FIRSTCHILD = 0;
    private static final int FIRSTELEMENT = 0;

    private final PlayerId id;
    private final SplittableRandom rng;
    private final int iterations;

    /**
     * Constructeur
     * 
     * @param: ownId: Identité du joueur.     
     * @param: rngSeed:Seed déterminant l'aléatoire.  
     * @param: iterations:Le nombre d'itérations de l'algorithme (le nombre de parties
     *                    qu'il va simuler).      
     * 
     * @throw: IllegalArgumentException: Si le nombre d'itérations est inférieur
     *         à 9.
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations)
            throws IllegalArgumentException {
        if (iterations < Jass.HAND_SIZE)
            throw new IllegalArgumentException(
                    "Iterations should be superior or egal to 9");

        this.id = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    /**
     * Cherche la carte la plus optimale à jouer d'après l'algorithme MCTS.
     * 
     * @param: state: L'état du tour de jeu.
     * @param: hand: La main du joueur MCTS.
     * 
     * @return: La carte que le joueur MCTS veut jouer.
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        assert !PackedTrick.isFull(state.packedTrick());

        long packedHand = hand.packed();
        Node root = new Node(state, possibleCards(state, packedHand));

        for (int i = 0; i < iterations; ++i) {

            List<Node> path = addNode(packedHand, root);

            // Recupere le state du nouveau noeud cree
            TurnState nodeState = path.get(path.size() - 1).state;

            nodeState=finishTurn(nodeState, packedHand);
            
            propagateScores(nodeState, path);
        }

        // PackedCardSet avec un seul element correspondant au meilleur
        // fils de la racine (donc la carte a jouer)
        long bestPackedCardSet = PackedCardSet.difference(
                root.state.packedUnplayedCards(),
                root.children[root.indexBestChild(0)].state
                        .packedUnplayedCards());

        // Seul element du set
        int bestPackedCard = PackedCardSet.get(bestPackedCardSet, FIRSTELEMENT);

        return Card.ofPacked(bestPackedCard);
    }
    
    /**
     * Demande au MCTS quel atout il veut choisir ou si il veut chibrer
     * 
     * @param hand: La main du MCTS
     * @param canPass:Si le joueur a le droit de chibrer
     * 
     * @return: L'atout choisi ou null si le joueur decide de passer (il ne 
     *          peut pas passer si son partenaire a deja passe)
     */
    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) { 
        
        final double EXPECTATION_FOR_TRUMP = 100.46;
        
        long packedHand = hand.packed();
        
        Map<Color, Double> trumpScores=new HashMap<>();
        
        final int ITERATION_PER_COLOR=Math.max(iterations/Color.COUNT,Jass.HAND_SIZE);
        
        for(Color c:Color.ALL) {
            
            //Ici le score n'importe pas
            TurnState initialState= TurnState.initial(c, Score.INITIAL, id);
            
            Node root = new Node(initialState, possibleCards(initialState, packedHand));
            
            //Utilise le meme algorithme que cardToPlay pour determiner quelle carte jouer
            for (int i=0;i<ITERATION_PER_COLOR;++i) {
                
                List<Node> path = addNode(packedHand, root);

                // Recupere le state du nouveau noeud cree
                TurnState nodeState = path.get(path.size() - 1).state;

                nodeState=finishTurn(nodeState, packedHand);
                
                propagateScores(nodeState, path);
            }
            Node bestChild=root.children[root.indexBestChild(0)];
            double expectationPoints=(bestChild.totalNodePoint)/(double)bestChild.turnPlayedFromNode;
            trumpScores.put(c, expectationPoints);
        }
        
        //Determines the bestTrump
        double bestTrumpScore=0;
        Color bestTrump=Color.SPADE;
        for(Color c:Color.ALL) {
           
          if(trumpScores.get(c)>bestTrumpScore) {
              bestTrumpScore=trumpScores.get(c);
              bestTrump=c;
          }
        }
        
        /* Si le joueur peut chiber, regarde si son espérance est supérieure à l'espérance moyenne
         * (obtenue avec des parties simulées, voir TurnPointExpectation dans test). Si oui, il choisit
         * cette couleur, sinon, il chibe.*/

        return canPass && bestTrumpScore < EXPECTATION_FOR_TRUMP? null : bestTrump;
    }
    
    /**
     * Propage les scores dans les noeuds superieurs du path
     * 
     * @param bottomNodeState: Le noeud du bas
     * @param path: Le chemin du noeud parcouru depuis la racine (root)
     */
    private void propagateScores(TurnState bottomNodeState, List<Node>path) {
        
        int turnScoreTeam1 = PackedScore.turnPoints(bottomNodeState.packedScore(),
                TeamId.TEAM_1);
        int turnScoreTeam2 = PackedScore.turnPoints(bottomNodeState.packedScore(),
                TeamId.TEAM_2);

        // Fait remonter les scores
        Node previousNode = null;

        for (Node node : path) {
            if (previousNode != null) {
                node.totalNodePoint += previousNode.state.nextPlayer()
                        .team() == TeamId.TEAM_1 ? turnScoreTeam1
                                : turnScoreTeam2;
            }
            node.turnPlayedFromNode++;
            previousNode = node;
        }
    }

    /**
     * Finit aleatoirement le tour
     * 
     * @param state: L'etat actuel du tour.
     * @param packedHand: La main empaquete du MCTS player
     * 
     * @return: L'etat du tour final
     */
    private TurnState finishTurn(TurnState state,long packedHand) {
        
        while (!PackedCardSet.isEmpty(state.packedUnplayedCards())) {

            //S'occupe de determiner qui doit jouer, le MCTS ou un autre joueur
            long packedPlayableCards = possibleCards(state, packedHand);

            // Choisie un carte jouable aleatoirement
            int packedCard = PackedCardSet.get(packedPlayableCards,
                    rng.nextInt(PackedCardSet.size(packedPlayableCards)));

            state = state.withNewCardPlayedAndTrickCollected(
                    Card.ofPacked(packedCard));

        } 
        return state;
    }
    /**
     * Retourne les cartes qu'il est possible de jouer en fonction du joueur qui
     * devrait jouer le tour en sachant que le joueur MCTS possède la main hand.
     * 
     * @param: state: L'état du tour de jeu.
     * @param: packedHand: La main sous forme empaquetée.
     * 
     * @return: L'ensemble de cartes jouables sous forme empaquetée.
     */
    private long possibleCards(TurnState state, long packedHand) {
        assert (!PackedTrick.isFull(state.packedTrick()));

        long packedUnplayedCards = state.packedUnplayedCards();

        return state.nextPlayer() == id
                ? PackedTrick.playableCards(state.packedTrick(),
                        PackedCardSet.intersection(packedHand,
                                packedUnplayedCards))
                : PackedTrick.playableCards(state.packedTrick(), PackedCardSet
                        .difference(packedUnplayedCards, packedHand));
    }

    /**
     * Ajoute (si possible) un noeud au bon endroit de l'arbre.
     * 
     * @param: hand: La main.
     * 
     * @param: root: La racine de l'arbre. 
     * 
     * @return: Une liste représentant le chemin depuis la racine jusqu'au
     *          nouveau noeud.
     */
    private List<Node> addNode(long packedHand, Node root) {

        Node actualNode = root;
        List<Node> path = new LinkedList<>();

        // Cherche le noeud auquel on va ajouter un noeud
        while (actualNode.isFull() && !PackedCardSet
                .isEmpty(actualNode.state.packedUnplayedCards())) {
            path.add(actualNode);
            actualNode = actualNode.children[actualNode.indexBestChild(C)];
        }

        path.add(actualNode);

        // Si toutes les cartes ont ete jouees, on ne rajoute pas d'enfant
        if (!PackedCardSet.isEmpty(actualNode.state.packedUnplayedCards())) {

            int packedCard = PackedCardSet.get(actualNode.addableChildren,
                    FIRSTCHILD);

            // Peut renvoyer un trick Invalid, ce qui ne pose pas de probleme
            // par la suite
            TurnState newState = actualNode.state
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(packedCard));

            int childIndex = actualNode.children.length
                    - PackedCardSet.size(actualNode.addableChildren);

            if (!newState.isTerminal()) {
                long newAddableChildren = possibleCards(newState, packedHand);

                actualNode.children[childIndex] = new Node(newState,
                        newAddableChildren);
            }
            // Si l'état est terminal, le noeud n'a plus d'enfants ajoutables
            else {
                actualNode.children[childIndex] = new Node(newState,
                        PackedCardSet.EMPTY);
            }

            path.add(actualNode.children[childIndex]);

            actualNode.addableChildren = PackedCardSet
                    .remove(actualNode.addableChildren, packedCard);
        }

        return path;
    }
    
    

    /**
     * Classe imbriquée représentant un noeud de l'arbre.
     * 
     * @author Antoine Masanet (288366)
     * @author Loïc Houmard (297181)
     *
     */
    private final static class Node {
        private final TurnState state;
        private final Node[] children;
        private long addableChildren;

        private int totalNodePoint;// S(n)
        private int turnPlayedFromNode;// N(n)

        /**
         * Constructeur
         * 
         * @param: state: L'état du tour du noeud.   
         * @param: addableChildren:L'ensemble de cartes qui peuvent être ajoutées comme
         *                         enfant de ce noeud.
         *             
         */
        private Node(TurnState state, long addableChildren) {
            this.children = new Node[PackedCardSet.size(addableChildren)];
            this.state = state;
            this.addableChildren = addableChildren;
            totalNodePoint = 0;
            turnPlayedFromNode = 0;
        }

        /**
         * Calcule la valeur V(n) du noeud.
         * 
         * @param: parent:Le noeud parent de celui-ci. 
         * @param: c: Une valeur déterminant l'importance du nombres de parties
         *            simulées par rapport à la performance.
         *             
         * @return: Un double représentant V(n).
         */
        private double computeVn(Node parent, int c) {
            assert (parent.turnPlayedFromNode >= 1);

            double invertOfN = 1.0 / turnPlayedFromNode;

            return turnPlayedFromNode > 0
                    ? (invertOfN * totalNodePoint) + c * Math.sqrt(
                            2 * Math.log(parent.turnPlayedFromNode) * invertOfN)
                    : Double.POSITIVE_INFINITY;
        }

        /**
         * Retourne l'index du noeud fils le plus prometteur.
         * 
         * @param: c: Une valeur déterminant l'importance du nombres de parties
         *             simulées par rapport à la performance.           
         * 
         * @return: L'index du noeud fils le plus prometteur.
         */
        private int indexBestChild(int c) {
            int i = 0;
            double best = Double.MIN_VALUE;
            int indexBest = 0;

            for (Node child : children) {
                double Vn = child.computeVn(this, c);

                if (Vn > best) {
                    best = Vn;
                    indexBest = i;
                }
                ++i;
            }

            return indexBest;
        }

        /**
         * Regarde si le noeud est "plein", c'est à dire que tous ses fils sont
         * déjà existant.
         * 
         * @return: True ssi le noeud est plein, c'est à dire qu'il n'a plus
         *          d'enfants à ajouter.
         */
        private boolean isFull() {
            return PackedCardSet.size(addableChildren) == 0;

        }
        
        /**
         * Permet d'afficher les points, le nombre de parties jouées et la
         * moyenne d'un noeud.
         * 
         * @return: Un String représentant les statistiques d'un noeud.
         */
        @SuppressWarnings("unused")
        private String affichage() {
            String s = "TotalNode points vaut: " + totalNodePoint;
            s += "\nTotal Iteration vaut: " + turnPlayedFromNode;
            s += "\n Average nodePoints: "
                    + ((double) totalNodePoint) / turnPlayedFromNode;
            return s;
        }
    }
}
