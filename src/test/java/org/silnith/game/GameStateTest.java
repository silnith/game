package org.silnith.game;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.silnith.game.move.Move;
import org.silnith.util.LinkedNode;
import org.silnith.util.Pair;

@RunWith(MockitoJUnitRunner.class)
public class GameStateTest {

	@Mock
	private LinkedNode<Move<Object>> moves;

	@Mock
	private LinkedNode<Object> boards;

	@Mock
	private GameState<Move<Object>, Object> gameState;

	@Mock
	private Move<Object> move;

	@Mock
	private Object board;

	@Mock
	private Object previousBoard;

	@Test
	public void testGameStateLinkedNodeOfMoveOfTLinkedNodeOfT() {
		final Pair<LinkedNode<Move<Object>>, LinkedNode<Object>> expected = new Pair<>(moves, boards);
		
		final GameState<Move<Object>, Object> actual = new GameState<>(moves, boards);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testGameStateMoveOfTT() {
		final Pair<LinkedNode<Move<Object>>, LinkedNode<Object>> expected = new Pair<>(new LinkedNode<>(move), new LinkedNode<>(board));
		
		final GameState<Move<Object>, Object> actual = new GameState<>(move, board);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testGameStateGameStateOfTMoveOfTT() {
		final LinkedNode<Move<Object>> expectedMoves = new LinkedNode<>(move, moves);
		final LinkedNode<Object> expectedBoards = new LinkedNode<>(board, boards);
		final Pair<LinkedNode<Move<Object>>, LinkedNode<Object>> expected = new Pair<>(expectedMoves, expectedBoards);
		
		when(gameState.getMoves()).thenReturn(moves);
		when(gameState.getBoards()).thenReturn(boards);
		when(move.apply(any())).thenReturn(board);
		when(boards.get(anyInt())).thenReturn(previousBoard);
		
		final GameState<Move<Object>, Object> actual = new GameState<>(gameState, move);
		
		assertEquals(expected, actual);
		
		verify(move).apply(same(previousBoard));
		verify(boards).get(eq(0));
	}

	@Test
	public void testGameStateGameStateOfTMoveOfT() {
		final LinkedNode<Move<Object>> expectedMoves = new LinkedNode<>(move, moves);
		final LinkedNode<Object> expectedBoards = new LinkedNode<>(board, boards);
		final Pair<LinkedNode<Move<Object>>, LinkedNode<Object>> expected = new Pair<>(expectedMoves, expectedBoards);
		
		when(gameState.getMoves()).thenReturn(moves);
		when(gameState.getBoards()).thenReturn(boards);
		
		final GameState<Move<Object>, Object> actual = new GameState<>(gameState, move, board);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testGetMoves() {
		final GameState<Move<Object>, Object> state = new GameState<>(moves, boards);
		
		assertSame(moves, state.getMoves());
	}

	@Test
	public void testGetBoards() {
		final GameState<Move<Object>, Object> state = new GameState<>(moves, boards);
		
		assertSame(boards, state.getBoards());
	}

}
