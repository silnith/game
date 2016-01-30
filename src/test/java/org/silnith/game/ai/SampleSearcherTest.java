package org.silnith.game.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.silnith.game.Game;
import org.silnith.game.GameState;
import org.silnith.game.move.Move;


public class SampleSearcherTest {
    
    private static class Board {
        
        public int board[][] = new int[3][3];
        
        public Board mark(final int x, final int y) {
            final Board newBoard = new Board();
            for (int i = 0; i < 3; i++ ) {
                for (int j = 0; j < 3; j++ ) {
                    newBoard.board[i][j] = board[i][j];
                }
            }
            newBoard.board[x][y] = 1;
            return newBoard;
        }
        
    }
    
    private static class DealMove implements Move<Board> {
        
        @Override
        public Board apply(final Board board) {
            return new Board();
        }
        
    }
    
    private static class XMove implements Move<Board> {
        
        public final int x;
        
        public final int y;
        
        public XMove(final Board board, final int x, final int y) {
            super();
            this.x = x;
            this.y = y;
        }
        
        @Override
        public Board apply(final Board board) {
            return board.mark(x, y);
        }
        
    }
    
    private static class TicTacToe implements Game<Move<Board>, Board> {
        
        @Override
        public boolean isWin(final Board board) {
            for (int i = 0; i < 3; i++ ) {
                if (board.board[i][0] == 1 && board.board[i][1] == 1 && board.board[i][2] == 1) {
                    return true;
                }
                if (board.board[0][i] == 1 && board.board[1][i] == 1 && board.board[2][i] == 1) {
                    return true;
                }
            }
            if (board.board[0][0] == 1 && board.board[1][1] == 1 && board.board[2][2] == 1) {
                return true;
            }
            if (board.board[0][2] == 1 && board.board[1][1] == 1 && board.board[2][0] == 1) {
                return true;
            }
            return false;
        }
        
        @Override
        public Collection<Move<Board>> findAllMoves(final GameState<Move<Board>, Board> state) {
            final ArrayList<Move<Board>> moves = new ArrayList<>();
            final Board board = state.getBoards().get(0);
            for (int i = 0; i < 3; i++ ) {
                for (int j = 0; j < 3; j++ ) {
                    if (board.board[i][j] == 0) {
                        moves.add(new XMove(board, i, j));
                    }
                }
            }
            return moves;
        }
        
        @Override
        public GameState<Move<Board>, Board> pruneGameState(final GameState<Move<Board>, Board> state) {
            return state;
        }
        
    }
    
    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final Move<Board> initialMove = new DealMove();
        final Board initialBoard = initialMove.apply(null);
        final Searcher<Move<Board>, Board> searcher =
                new Searcher<>(new TicTacToe(), new GameState<>(initialMove, initialBoard));
                
        final Runnable worker = searcher.getNewWorker();
        worker.run();
        
//        ExecutorService service = Executors.newFixedThreadPool(4);
//        final Deque<Future<Collection<GameState<Board>>>> futures = new ConcurrentLinkedDeque<>();
//        futures.add(service.submit(searcher.getCallable(searcher
//                .getInitialState())));
//        while (!futures.isEmpty()) {
//            final Future<Collection<GameState<Board>>> removeLast = futures
//                    .removeLast();
//            final Collection<GameState<Board>> collection = removeLast.get();
//            for (final GameState<Board> state : collection) {
//                if (searcher.getGame().isWin(state.getBoards().get(0))) {
//                    searcher.getSolutions().add(state);
//                } else {
//                    futures.add(service.submit(searcher.getCallable(state)));
//                }
//            }
//        }
//        service.shutdown();
        
        System.out.println("Nodes searched: " + searcher.getNodesSearched());
        System.out.println("Max depth searched: " + searcher.getMaxDepthSearched());
        System.out.println(searcher.getSolutions().size());
    }
    
}
