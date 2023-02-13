package com.scarwe.freechess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements ChessDelegate {

    private final BoardModel board = new BoardModel();
    //private final int PORT = 8080;
    //private PrintWriter printWriter;

    // tag for console logging
    public static String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // finds the board view by its ID
        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        Button resetButton = findViewById(R.id.reset_button);
        /*Button listenButton = findViewById(R.id.listen_button);
        Button connectButton = findViewById(R.id.connect_button);*/

        resetButton.setOnClickListener(v -> {
            board.resetBoard();
            // draws to board
            boardView.invalidate();
        });

        System.out.println(board.pgnBoard());

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
        return board.pieceLoc(square);
    }

    @Override
    public void movePiece(Square from, Square to) {
        board.movePiece(from, to);
        findViewById(R.id.board_view).invalidate();
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        //executor.execute(() -> printWriter.println(fromCol +" "+fromRow +" "+toCol +" "+toRow));
    }
}