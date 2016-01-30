package org.silnith.game.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.silnith.game.Game;
import org.silnith.game.GameState;
import org.silnith.game.move.Move;


/**
 * A game search engine. Given a game engine and an initial game state, this
 * provides functionality to search the game tree.
 *
 * @param <B> the board type for the game to search
 */
public class Searcher<M extends Move<B>, B> {
    
    private final Game<M, B> game;
    
    private final GameState<M, B> initialState;
    
    private final Deque<GameState<M, B>> pendingNodes;
    
    private final AtomicLong pendingNodesCount;
    
    private final Collection<GameState<M, B>> solutions;
    
    private final AtomicLong nodesSearched;
    
    private final AtomicInteger maxDepthSearched;
    
    public Searcher(final Game<M, B> game, final GameState<M, B> initialState) {
        super();
        this.game = game;
        this.initialState = initialState;
        this.pendingNodes = new ConcurrentLinkedDeque<>();
        this.pendingNodesCount = new AtomicLong();
        this.solutions = Collections.synchronizedCollection(new HashSet<GameState<M, B>>());
        this.nodesSearched = new AtomicLong();
        this.maxDepthSearched = new AtomicInteger();
        
        Searcher.this.addNodeToQueue(this.initialState);
    }
    
    public Game<M, B> getGame() {
        return game;
    }
    
    public GameState<M, B> getInitialState() {
        return initialState;
    }
    
    public Collection<GameState<M, B>> getSolutions() {
        return solutions;
    }
    
    public long getNodesSearched() {
        return nodesSearched.get();
    }
    
    public int getMaxDepthSearched() {
        return maxDepthSearched.get();
    }
    
    public long getPendingNodesCount() {
        return pendingNodesCount.get();
    }
    
    public boolean isDone() {
        return getPendingNodesCount() <= 0;
    }
    
    /**
     * Search the given game state and return a collection of possible game
     * states that can succeed it.
     * 
     * @param state the game state to search
     * @return a collection of game states that can follow the input state
     */
    protected Collection<GameState<M, B>> search(final GameState<M, B> state) {
        final Collection<GameState<M, B>> moves = new ArrayList<>();
        
        final Collection<M> possibleMoves = game.findAllMoves(state);
        for (final M possibleMove : possibleMoves) {
            final GameState<M, B> possibleState = new GameState<>(state, possibleMove);
            
            final GameState<M, B> prunedState = game.pruneGameState(possibleState);
            
            if (prunedState != null) {
                moves.add(prunedState);
            }
        }
        
        nodesSearched.incrementAndGet();
        setMaxDepthSearched(state.getBoards().size());
        return moves;
    }
    
    private void setMaxDepthSearched(final int depth) {
        boolean succeeded;
        do {
            final int current = maxDepthSearched.get();
            if (current >= depth) {
                return;
            }
            succeeded = maxDepthSearched.compareAndSet(current, depth);
        } while ( !succeeded);
    }
    
    protected void addNodeToQueue(final GameState<M, B> node) {
        pendingNodesCount.incrementAndGet();
        pendingNodes.add(node);
    }
    
    protected GameState<M, B> getNodeFromQueue() throws InterruptedException {
        GameState<M, B> node = pendingNodes.pollLast();
        while (node == null) {
            Thread.sleep(3000);
            node = pendingNodes.pollLast();
        }
        return node;
    }
    
    public Runnable getNewWorker() {
        return new Worker();
    }
    
    private class Worker implements Runnable {
        
        @Override
        public void run() {
            try {
                do {
                    final GameState<M, B> state = getNodeFromQueue();
                    if (game.isWin(state.getBoards().get(0))) {
                        solutions.add(state);
                        continue;
                    }
                    final Collection<GameState<M, B>> states = search(state);
                    for (final GameState<M, B> gameState : states) {
                        addNodeToQueue(gameState);
                    }
                    pendingNodesCount.decrementAndGet();
                } while ( !isDone());
            } catch (final InterruptedException e) {
                ;
            }
        }
        
    }
    
}
