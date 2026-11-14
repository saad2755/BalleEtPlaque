import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Jeu classique : une plaque (raquette) renvoie une petite balle.
 * Commandes : souris ou flèches gauche/droite. Espace = rejouer.
 * Compilation : javac BalleEtPlaque.java
 * Exécution   : java BalleEtPlaque
 */
public class BalleEtPlaque extends JPanel implements ActionListener, KeyListener {

    static final int LARGEUR = 600, HAUTEUR = 450;
    static final int PLAQUE_L = 100, PLAQUE_H = 12, BALLE_D = 14;

    double balleX, balleY, vx, vy;
    int plaqueX;
    int score, record;
    boolean perdu;
    boolean gauche, droite;
    final Timer timer = new Timer(10, this); // ~100 images/seconde

    public BalleEtPlaque() {
        setPreferredSize(new Dimension(LARGEUR, HAUTEUR));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                plaqueX = e.getX() - PLAQUE_L / 2;
                limiterPlaque();
            }
        });
        nouvellePartie();
        timer.start();
    }

    void nouvellePartie() {
        balleX = LARGEUR / 2.0;
        balleY = HAUTEUR / 3.0;
        vx = 2.5 * (Math.random() < 0.5 ? -1 : 1);
        vy = 2.5;
        plaqueX = (LARGEUR - PLAQUE_L) / 2;
        score = 0;
        perdu = false;
    }

    void limiterPlaque() {
        plaqueX = Math.max(0, Math.min(LARGEUR - PLAQUE_L, plaqueX));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!perdu) {
            // Déplacement au clavier
            if (gauche)  plaqueX -= 6;
            if (droite)  plaqueX += 6;
            limiterPlaque();

            // Déplacement de la balle
            balleX += vx;
            balleY += vy;

            // Rebonds sur les murs gauche, droit et le plafond
            if (balleX <= 0)                 { balleX = 0; vx = -vx; }
            if (balleX >= LARGEUR - BALLE_D) { balleX = LARGEUR - BALLE_D; vx = -vx; }
            if (balleY <= 0)                 { balleY = 0; vy = -vy; }

            // Rebond sur la plaque
            int plaqueY = HAUTEUR - 40;
            if (vy > 0
                    && balleY + BALLE_D >= plaqueY
                    && balleY + BALLE_D <= plaqueY + PLAQUE_H + 6
                    && balleX + BALLE_D >= plaqueX
                    && balleX <= plaqueX + PLAQUE_L) {

                balleY = plaqueY - BALLE_D;
                vy = -vy;

                // L'angle dépend de l'endroit où la balle touche la plaque
                double centreBalle = balleX + BALLE_D / 2.0;
                double centrePlaque = plaqueX + PLAQUE_L / 2.0;
                double impact = (centreBalle - centrePlaque) / (PLAQUE_L / 2.0); // -1 à 1
                vx = impact * 4.5;

                // Accélération progressive
                vy *= 1.04;
                score++;
                record = Math.max(record, score);
            }

            // Balle perdue
            if (balleY > HAUTEUR) perdu = true;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Plaque
        g2.setColor(new Color(80, 180, 255));
        g2.fillRoundRect(plaqueX, HAUTEUR - 40, PLAQUE_L, PLAQUE_H, 8, 8);

        // Balle
        g2.setColor(Color.WHITE);
        g2.fillOval((int) balleX, (int) balleY, BALLE_D, BALLE_D);

        // Score
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Score : " + score + "   Record : " + record, 10, 20);

        if (perdu) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 32));
            centrer(g2, "Perdu !", HAUTEUR / 2 - 10);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            centrer(g2, "Appuie sur ESPACE pour rejouer", HAUTEUR / 2 + 25);
        }
    }

    void centrer(Graphics2D g2, String texte, int y) {
        int w = g2.getFontMetrics().stringWidth(texte);
        g2.drawString(texte, (LARGEUR - w) / 2, y);
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)  gauche = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) droite = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE && perdu) nouvellePartie();
    }
    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)  gauche = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) droite = false;
    }
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Balle et plaque");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new BalleEtPlaque());
            f.pack();
            f.setResizable(false);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}