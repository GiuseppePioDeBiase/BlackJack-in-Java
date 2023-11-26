import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BlackJack {

    private static Giocatore giocatore1 = new Giocatore("Giocatore 1");
    private static Giocatore giocatore2 = new Giocatore("Giocatore 2");
    private static Mazzo mazzo = new Mazzo();
    private static JTextArea resultArea;
    private static boolean isSecondCardRevealed = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Blackjack");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton dealButton = new JButton("Deal");
        JButton hitButton = new JButton("Hit");
        JButton standButton = new JButton("Stand");

        resultArea = new JTextArea(10, 30);

        panel.add(dealButton);
        panel.add(hitButton);
        panel.add(standButton);
        panel.add(resultArea);
        
        dealButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                giocatore1 = new Giocatore("Giocatore 1");
                giocatore2 = new Giocatore("Giocatore 2");
                mazzo = new Mazzo();
                mazzo.mischia();

                giocatore1.prendiCarta(mazzo.pescaCarta());
                giocatore2.prendiCarta(mazzo.pescaCarta());
                giocatore1.prendiCarta(mazzo.pescaCarta());
                Carta secondaCarta = mazzo.pescaCarta();
                giocatore2.prendiCarta(secondaCarta);

                resultArea.setText(giocatore1.toString() + "\nPunteggio: " + giocatore1.calcolaPunteggio() +
                        "\n\n" + "Giocatore 2: " + giocatore2.getPrimaCarta() + ", **");

                // Abilita il pulsante "Hit" e "Stand" all'inizio di ogni nuova partita
                hitButton.setEnabled(true);
                standButton.setEnabled(true);

                // Rendi visibile la seconda carta del giocatore 2 solo quando è il suo turno
                isSecondCardRevealed = false;
            }
        });



        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (giocatore1.calcolaPunteggio() <= 21) {
                    giocatore1.prendiCarta(mazzo.pescaCarta());
                    resultArea.setText(giocatore1.toString() + "\nPunteggio: " + giocatore1.calcolaPunteggio() +
                            "\n\n" + "Giocatore 2: " + giocatore2.getPrimaCarta() + ", " + (isSecondCardRevealed ? giocatore2.getSecondaCarta() : "**"));
                }

                if (giocatore1.calcolaPunteggio() > 21) {
                    resultArea.append("\n\nGiocatore 1 ha perso!");
                    hitButton.setEnabled(false); // Disabilita il pulsante "Hit"
                    standButton.setEnabled(false); // Disabilita il pulsante "Stand"
                }
            }
        });

        standButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (giocatore1.calcolaPunteggio() <= 21) {
                    while (giocatore2.calcolaPunteggio() < giocatore1.calcolaPunteggio() && giocatore2.calcolaPunteggio() <= 21) {
                        giocatore2.prendiCarta(mazzo.pescaCarta());
                    }

                    resultArea.setText(giocatore1.toString() + "\nPunteggio: " + giocatore1.calcolaPunteggio() +
                            "\n\n" + giocatore2.toString() + "\nPunteggio: " + giocatore2.calcolaPunteggio());
                }

                if (giocatore2.calcolaPunteggio() > 21 || giocatore1.calcolaPunteggio() > giocatore2.calcolaPunteggio()) {
                    resultArea.append("\n\nGiocatore 1 ha vinto!");
                } else if (giocatore1.calcolaPunteggio() < giocatore2.calcolaPunteggio()) {
                    resultArea.append("\n\nGiocatore 2 ha vinto!");
                } else {
                    resultArea.append("\n\nÈ un pareggio!");
                }

                // Disabilita il pulsante "Hit" e "Stand" dopo che il giocatore ha finito il turno
                hitButton.setEnabled(false);
                standButton.setEnabled(false);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    static class Carta {
        private String faccia;
        private String palo;
        private int valore;

        public Carta(String faccia, String palo, int valore) {
            this.faccia = faccia;
            this.palo = palo;
            this.valore = valore;
        }

        public int getValore() {
            return valore;
        }

        @Override
        public String toString() {
            return faccia + " di " + palo;
        }
    }

    static class Mazzo {
        private List<Carta> carte;

        public Mazzo() {
            carte = new ArrayList<>();
            String[] facce = {
                    "Asso", "Due", "Tre", "Quattro", "Cinque", "Sei", "Sette",
                    "Otto", "Nove", "Dieci", "Jack", "Queen", "King"
            };
            String[] palo = {
                    "Cuori", "Fiori", "Picche", "Quadri"
            };
            int[] valori = {
                    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10
            };
            for (int i = 0; i < facce.length; i++) {
                for (int j = 0; j < palo.length; j++) {
                    carte.add(new Carta(facce[i], palo[j], valori[i]));
                }
            }
        }

        public void mischia() {
            Collections.shuffle(carte);
        }

        public Carta pescaCarta() {
            if (carte.isEmpty()) {
                throw new IllegalStateException("Il mazzo è vuoto");
            }
            return carte.remove(0);
        }
    }

    static class Giocatore {
        private String nome;
        private List<Carta> mano;

        public Giocatore(String nome) {
            this.nome = nome;
            mano = new ArrayList<>();
        }

        public void prendiCarta(Carta carta) {
            mano.add(carta);
        }

        public int calcolaPunteggio() {
            int punteggio = 0;
            int assi = 0;

            for (Carta carta : mano) {
                int valore = carta.getValore();
                if (valore == 1) {
                    assi++;
                }
                punteggio += valore;
            }

            while (assi > 0 && punteggio <= 11) {
                punteggio += 10; // Cambia il valore di un asso da 1 a 11
                assi--;
            }

            return punteggio;
        }

        public String getPrimaCarta() {
            if (mano.size() >= 1) {
                return mano.get(0).toString();
            }
            return "";
        }

        public String getSecondaCarta() {
            if (mano.size() >= 2) {
                return mano.get(1).toString();
            }
            return "";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(nome).append(": ");
            for (Carta carta : mano) {
                sb.append(carta).append(", ");
            }
            if (!mano.isEmpty()) {
                sb.setLength(sb.length() - 2); // Rimuove l'ultima virgola e spazio
            }
            return sb.toString();
        }
    }
}
