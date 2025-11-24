package tictactoesimple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class TicTacToeSimple {
    private static char currentPlayer = 'X';
    private static JButton[] buttons = new JButton[9];
    private static JLabel statusLabel;
    private static JLabel scoreLabel;
    private static boolean gameActive = true;
    private static String player1Name = "Player 1";
    private static String player2Name = "Player 2";
    private static int player1Wins = 0;
    private static int player2Wins = 0;
    private static int draws = 0;
    private static JTextField player1Field;
    private static JTextField player2Field;
    private static ArrayList<ScoreRecord> bestScores = new ArrayList<>();
    private static final String SCORE_FILE = "tictactoe_scores.txt";
    
    public static void main(String[] args) {
        loadScores();
        
        JFrame frame = new JFrame("Tic-Tac-Toe with Score History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 650);
        frame.setLayout(new BorderLayout(5, 5));
        
        JPanel namePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        namePanel.setBorder(BorderFactory.createTitledBorder("Player Names"));
        namePanel.add(new JLabel("Player 1 (X):", SwingConstants.RIGHT));
        player1Field = new JTextField("Player 1");
        namePanel.add(player1Field);
        namePanel.add(new JLabel("Player 2 (O):", SwingConstants.RIGHT));
        player2Field = new JTextField("Player 2");
        namePanel.add(player2Field);
        frame.add(namePanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        scoreLabel = new JLabel("CURRENT SCORES: " + player1Name + " - 0 | " + player2Name + " - 0 | Draws: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(200, 230, 255));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        mainPanel.add(scorePanel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        gridPanel.setPreferredSize(new Dimension(300, 300));
        gridPanel.setBorder(BorderFactory.createTitledBorder("Game Board"));
        
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].setFocusPainted(false);
            
            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (gameActive && buttons[index].getText().equals("")) {
                        player1Name = player1Field.getText().isEmpty() ? "Player 1" : player1Field.getText();
                        player2Name = player2Field.getText().isEmpty() ? "Player 2" : player2Field.getText();
                        
                        buttons[index].setText(String.valueOf(currentPlayer));
                        buttons[index].setEnabled(false);
                        
                        if (checkWin()) {
                            String winnerName = (currentPlayer == 'X') ? player1Name : player2Name;
                            statusLabel.setText(winnerName + " WINS!");
                            statusLabel.setBackground(Color.GREEN);
                            
                            if (currentPlayer == 'X') player1Wins++;
                            else player2Wins++;
                            
                            updateScore();
                            checkForBestScore(winnerName, (currentPlayer == 'X') ? player1Wins : player2Wins);
                            gameActive = false;
                            
                        } else if (checkDraw()) {
                            statusLabel.setText("GAME DRAW!");
                            statusLabel.setBackground(Color.ORANGE);
                            draws++;
                            updateScore();
                            gameActive = false;
                            
                        } else {
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                            String currentPlayerName = (currentPlayer == 'X') ? player1Name : player2Name;
                            statusLabel.setText(currentPlayerName + "'s Turn (" + currentPlayer + ")");
                            statusLabel.setBackground(Color.YELLOW);
                        }
                    }
                }
            });
            gridPanel.add(buttons[i]);
        }
        
        JPanel gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridContainer.add(gridPanel);
        mainPanel.add(gridContainer, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        statusLabel = new JLabel(player1Name + "'s Turn (X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.YELLOW);
        statusLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("New Game");
        resetButton.addActionListener(e -> resetGame());
        JButton updateNamesButton = new JButton("Update Names");
        updateNamesButton.addActionListener(e -> updatePlayerNames());
        JButton bestScoresButton = new JButton("View Best Scores");
        bestScoresButton.addActionListener(e -> showBestScores());
        controlPanel.add(resetButton);
        controlPanel.add(updateNamesButton);
        controlPanel.add(bestScoresButton);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }
    
    private static boolean checkWin() {
        String player = String.valueOf(currentPlayer);
        return (buttons[0].getText().equals(player) && buttons[1].getText().equals(player) && buttons[2].getText().equals(player)) ||
               (buttons[3].getText().equals(player) && buttons[4].getText().equals(player) && buttons[5].getText().equals(player)) ||
               (buttons[6].getText().equals(player) && buttons[7].getText().equals(player) && buttons[8].getText().equals(player)) ||
               (buttons[0].getText().equals(player) && buttons[3].getText().equals(player) && buttons[6].getText().equals(player)) ||
               (buttons[1].getText().equals(player) && buttons[4].getText().equals(player) && buttons[7].getText().equals(player)) ||
               (buttons[2].getText().equals(player) && buttons[5].getText().equals(player) && buttons[8].getText().equals(player)) ||
               (buttons[0].getText().equals(player) && buttons[4].getText().equals(player) && buttons[8].getText().equals(player)) ||
               (buttons[2].getText().equals(player) && buttons[4].getText().equals(player) && buttons[6].getText().equals(player));
    }
    
    private static boolean checkDraw() {
        for (JButton button : buttons) if (button.getText().equals("")) return false;
        return true;
    }
    
    private static void updateScore() {
        String scoreText = String.format("CURRENT SCORES: %s - %d | %s - %d | Draws: %d", player1Name, player1Wins, player2Name, player2Wins, draws);
        scoreLabel.setText(scoreText);
    }
    
    private static void updatePlayerNames() {
        player1Name = player1Field.getText().isEmpty() ? "Player 1" : player1Field.getText();
        player2Name = player2Field.getText().isEmpty() ? "Player 2" : player2Field.getText();
        updateScore();
        String currentPlayerName = (currentPlayer == 'X') ? player1Name : player2Name;
        statusLabel.setText(currentPlayerName + "'s Turn (" + currentPlayer + ")");
        statusLabel.setBackground(Color.YELLOW);
    }
    
    private static void resetGame() {
        currentPlayer = 'X';
        gameActive = true;
        updatePlayerNames();
        String currentPlayerName = (currentPlayer == 'X') ? player1Name : player2Name;
        statusLabel.setText(currentPlayerName + "'s Turn (" + currentPlayer + ")");
        statusLabel.setBackground(Color.YELLOW);
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
        }
    }
    
    private static void checkForBestScore(String playerName, int wins) {
        if (wins >= 3) {
            boolean found = false;
            for (ScoreRecord record : bestScores) {
                if (record.playerName.equals(playerName)) {
                    if (wins > record.score) {
                        record.score = wins;
                        record.date = new Date();
                    }
                    found = true;
                    break;
                }
            }
            if (!found) bestScores.add(new ScoreRecord(playerName, wins, new Date()));
            Collections.sort(bestScores, (a, b) -> b.score - a.score);
            if (bestScores.size() > 5) bestScores = new ArrayList<>(bestScores.subList(0, 5));
            saveScores();
        }
    }
    
    private static void loadScores() {
        bestScores.clear();
        try {
            if (!Files.exists(Paths.get(SCORE_FILE))) return;
            java.util.List<String> lines = Files.readAllLines(Paths.get(SCORE_FILE));
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    long dateMillis = Long.parseLong(parts[2]);
                    Date date = new Date(dateMillis);
                    bestScores.add(new ScoreRecord(name, score, date));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }
    
    private static void saveScores() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(SCORE_FILE));
            for (ScoreRecord record : bestScores) {
                writer.println(record.playerName + "|" + record.score + "|" + record.date.getTime());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving scores: " + e.getMessage());
        }
    }
    
    private static void showBestScores() {
        if (bestScores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No best scores yet!\nWin at least 3 games to appear here.", "Best Scores", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='text-align: center;'><h2>🏆 BEST SCORES 🏆</h2>");
        for (int i = 0; i < bestScores.size(); i++) {
            ScoreRecord record = bestScores.get(i);
            String medal = "";
            if (i == 0) medal = "🥇 ";
            else if (i == 1) medal = "🥈 ";
            else if (i == 2) medal = "🥉 ";
            sb.append("<b>").append(medal).append(record.playerName).append("</b><br>");
            sb.append(record.score).append(" wins • ").append(record.getFormattedDate()).append("<br><br>");
        }
        sb.append("</div></html>");
        
        Object[] options = {"Close", "Reset History"};
        int choice = JOptionPane.showOptionDialog(null, sb.toString(), "Hall of Fame", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
        if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset ALL score history?\nThis cannot be undone!", "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                bestScores.clear();
                saveScores();
                JOptionPane.showMessageDialog(null, "Score history has been reset!", "History Reset", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    static class ScoreRecord {
        String playerName;
        int score;
        Date date;
        ScoreRecord(String playerName, int score, Date date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }
        String getFormattedDate() {
            return String.format("%tF", date);
        }
    }
}