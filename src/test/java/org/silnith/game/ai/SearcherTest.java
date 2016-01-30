package org.silnith.game.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.silnith.game.Game;
import org.silnith.game.GameState;
import org.silnith.game.move.Move;


@RunWith(MockitoJUnitRunner.class)
public class SearcherTest {
    
    private static class ReturnFirstArgumentAnswer<T> implements Answer<T> {
        
        @SuppressWarnings("unchecked")
        @Override
        public T answer(final InvocationOnMock invocation) throws Throwable {
            return (T) invocation.getArguments()[0];
        }
        
    }
    
    @Mock
    private Move<Object> initialMove;
    
    @Mock
    private Object initialBoard;
    
    private GameState<Move<Object>, Object> initialState;
    
    @Mock
    private Game<Move<Object>, Object> game;
    
    private Searcher<Move<Object>, Object> searcher;
    
    @Mock
    private Move<Object> move1;
    
    @Mock
    private Move<Object> move2;
    
    @Mock
    private Move<Object> move3;
    
    @Mock
    private Object board1;
    
    @Mock
    private Object board2;
    
    @Mock
    private Object board3;
    
    @Before
    public void setUp() {
        initialState = new GameState<>(initialMove, initialBoard);
        searcher = new Searcher<>(game, initialState);
    }
    
    @Test
    public void testSearch() {
        final Collection<Move<Object>> moves = new ArrayList<>();
        moves.add(move1);
        moves.add(move2);
        moves.add(move3);
        when(game.findAllMoves(any(GameState.class))).thenReturn(moves);
        when(move1.apply(any(Object.class))).thenReturn(board1);
        when(move2.apply(any(Object.class))).thenReturn(board2);
        when(move3.apply(any(Object.class))).thenReturn(board3);
        when(game.pruneGameState(any(GameState.class))).thenAnswer(new ReturnFirstArgumentAnswer<>());
        
        final Collection<GameState<Move<Object>, Object>> newStates = searcher.search(initialState);
        
        assertEquals(3, newStates.size());
        
        final GameState<Move<Object>, Object> state1 = new GameState<Move<Object>, Object>(initialState, move1, board1);
        final GameState<Move<Object>, Object> state2 = new GameState<Move<Object>, Object>(initialState, move2, board2);
        final GameState<Move<Object>, Object> state3 = new GameState<Move<Object>, Object>(initialState, move3, board3);
        
        final Iterator<GameState<Move<Object>, Object>> iter = newStates.iterator();
        final GameState<Move<Object>, Object> firstState = iter.next();
        final GameState<Move<Object>, Object> secondState = iter.next();
        final GameState<Move<Object>, Object> thirdState = iter.next();
        assertEquals(state1, firstState);
        assertEquals(state2, secondState);
        assertEquals(state3, thirdState);
        
        verify(move1).apply(same(initialBoard));
        verify(move2).apply(same(initialBoard));
        verify(move3).apply(same(initialBoard));
        verify(game).pruneGameState(eq(state1));
        verify(game).pruneGameState(eq(state2));
        verify(game).pruneGameState(eq(state3));
    }
    
    @Test
    public void testSearchPruned() {
        final Collection<Move<Object>> moves = new ArrayList<>();
        moves.add(move1);
        moves.add(move2);
        moves.add(move3);
        when(game.findAllMoves(any(GameState.class))).thenReturn(moves);
        when(move1.apply(any(Object.class))).thenReturn(board1);
        when(move2.apply(any(Object.class))).thenReturn(board2);
        when(move3.apply(any(Object.class))).thenReturn(board3);
        when(game.pruneGameState(any(GameState.class))).thenReturn(null);
        
        final Collection<GameState<Move<Object>, Object>> newStates = searcher.search(initialState);
        
        assertTrue(newStates.isEmpty());
        
        final GameState<Move<Object>, Object> state1 = new GameState<Move<Object>, Object>(initialState, move1, board1);
        final GameState<Move<Object>, Object> state2 = new GameState<Move<Object>, Object>(initialState, move2, board2);
        final GameState<Move<Object>, Object> state3 = new GameState<Move<Object>, Object>(initialState, move3, board3);
        
        verify(move1).apply(same(initialBoard));
        verify(move2).apply(same(initialBoard));
        verify(move3).apply(same(initialBoard));
        verify(game).pruneGameState(eq(state1));
        verify(game).pruneGameState(eq(state2));
        verify(game).pruneGameState(eq(state3));
    }
    
}
