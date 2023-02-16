package com.scarwe.freechess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends Activity implements ChessDelegate {

    private final BoardGame board = new BoardGame();
    //private final int PORT = 8080;
    //private PrintWriter printWriter;

    // tag for console logging
    public static String tag = "MainActivity";
    MediaPlayer player;

    public MainActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });

        setContentView(R.layout.activity_main);

        // finds the board view by its ID
        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        Button resetButton = findViewById(R.id.reset_button);
        /*Button listenButton = findViewById(R.id.listen_button);
        Button connectButton = findViewById(R.id.connect_button);*/

        resetButton.setOnClickListener(v -> {
            board.resetBoard();
            board.whiteCastled = false;
            board.blackCastled = false;
            boardView.invalidate();
        });

        /*listenButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> ((Runnable) () -> {
                try {
                    Log.d(tag, "socket server listening...");
                    ServerSocket serverSocket = new ServerSocket(PORT);
                    Socket socket = serverSocket.accept();
                    receiveMove(socket);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).run());
        });

        connectButton.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> ((Runnable) () -> {
                try {
                    Log.d(tag, "socket client connecting to addr:port");
                    Socket socket = new Socket("172.11.11.47", PORT);
                    receiveMove(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).run());
        });*/

    }

    /*private void receiveMove(Socket socket) throws IOException {
        Scanner scanner = new Scanner(socket.getInputStream());
        printWriter = new PrintWriter(socket.getOutputStream(), true);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] numberStrs = line.split(",");
            int[] move = new int[numberStrs.length];
            for (int i = 0; i < numberStrs.length; i++){
                move[i] = Integer.parseInt(numberStrs[i]);
            }
            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable () {
                        @Override
                        public void run() {
                            movePiece(move[0], move[1], move[2], move[3]);
                        }
                    });
                }
            }.start();
        }
    }*/

    // required due to interface
    @Override
    public ChessPiece pieceLoc(Square square) {
        return BoardGame.pieceLoc(square);
    }

    @Override
    public void movePiece(Square from, Square to) throws CloneNotSupportedException, IOException {
        boolean checkmated = true;
        boolean stalemated = true;
        ChessPlayer white = BoardGame.whitePlayer;
        ChessPlayer black = BoardGame.blackPlayer;
        BoardView boardView = findViewById(R.id.board_view);
        if (white.turn) {
            if (BoardGame.whitePlayer.movePiece(from, to, false)) {
                if (BoardGame.gameMove == 0) {
                    BoardGame.gameMove++;
                }
                BoardGame.setCurrentPlayer(BoardGame.blackPlayer);
                BoardGame.whitePlayer.setTurn(false);
                BoardGame.blackPlayer.setTurn(true);
                black.findLegalMoves();
                black.isKingChecked();
                boardView.invalidate();
                if (player == null ) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                if (black.checked) {
                    System.out.println("black checked");
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                            break;
                        }
                    }
                    if (checkmated) {
                        System.out.println("black checkmated");
                    }
                } else {
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                            break;
                        }
                    }
                    if (stalemated) {
                        System.out.println("black stalemated");
                    }
                }
            }
        } else if (BoardGame.blackPlayer.turn) {
            if (BoardGame.blackPlayer.movePiece(from, to, false)) {
                BoardGame.setCurrentPlayer(BoardGame.whitePlayer);
                BoardGame.whitePlayer.setTurn(true);
                BoardGame.blackPlayer.setTurn(false);
                white.findLegalMoves();
                white.isKingChecked();
                boardView.invalidate();
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                if (white.checked) {
                    System.out.println("white checked");
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                            break;
                        }
                    }
                    if (checkmated) {
                        System.out.println("white checkmated");
                    }
                } else {
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                            break;
                        }
                    }
                    if (stalemated) {
                        System.out.println("white stalemated");
                    }
                }
            }
        }

        findViewById(R.id.board_view).invalidate();
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        //executor.execute(() -> printWriter.println(fromCol +" "+fromRow +" "+toCol +" "+toRow));
    }
}