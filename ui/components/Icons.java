package myweigh.ui.components;

import myweigh.model.Exercise;
import myweigh.model.ExerciseType;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Vector line icons drawn with Java2D, so they scale cleanly and follow the theme colors. */
public final class Icons {
    public enum Kind {
        HOME, PLANS, CALENDAR, PROGRESS, PROFILE, SETTINGS, FLAME, PULSE, DROP, DUMBBELL, CHECK,
        CHECK_CIRCLE, CIRCLE, X_CIRCLE, CHEVRON_RIGHT, CHEVRON_LEFT, CHEVRON_DOWN, SEARCH, MAIL, LOCK,
        SHIELD, EYE, EYE_OFF, CLOCK, RUN, PLUS, MINUS, CLOSE, LOGOUT, TROPHY, HEART, UPDOWN, TREND,
        PALETTE, BELL, GLOBE, RULER, HELP, INFO, KEY, EDIT, TRASH, SCALE, TARGET, GLASS, BOLT, BOOK, USER_EDIT
    }

    private Icons() {
    }

    public static Icon of(Kind kind, int size, Color color) {
        return new VectorIcon(kind, size, color);
    }

    /** Picks an icon that represents an exercise. */
    public static Kind forExercise(Exercise e) {
        if (e.isRehabExercise()) {
            return Kind.HEART;
        }
        if (!e.isGymExercise()) {
            return Kind.BOLT;
        }
        if (e.getType() == ExerciseType.HOLD) {
            return Kind.CLOCK;
        }
        if (!e.isBodyweight()) {
            return Kind.DUMBBELL;
        }
        if ("Legs".equals(e.getMuscleGroup())) {
            return Kind.RUN;
        }
        return Kind.UPDOWN;
    }

    private static final class VectorIcon implements Icon {
        private final Kind kind;
        private final int size;
        private final Color color;

        VectorIcon(Kind kind, int size, Color color) {
            this.kind = kind;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double s = size / 24.0;
            g2.scale(s, s);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g2);
            g2.dispose();
        }

        private static void line(Graphics2D g, double x1, double y1, double x2, double y2) {
            g.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        private static void poly(Graphics2D g, double... p) {
            Path2D path = new Path2D.Double();
            path.moveTo(p[0], p[1]);
            for (int i = 2; i < p.length; i += 2) {
                path.lineTo(p[i], p[i + 1]);
            }
            g.draw(path);
        }

        private static void circle(Graphics2D g, double cx, double cy, double r) {
            g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        }

        private static void dot(Graphics2D g, double cx, double cy, double r) {
            g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        }

        private static void rrect(Graphics2D g, double x, double y, double w, double h, double r) {
            g.draw(new RoundRectangle2D.Double(x, y, w, h, r, r));
        }

        private static void arc(Graphics2D g, double x, double y, double w, double h, double start, double extent) {
            g.draw(new Arc2D.Double(x, y, w, h, start, extent, Arc2D.OPEN));
        }

        private static void eye(Graphics2D g) {
            Path2D p = new Path2D.Double();
            p.moveTo(2.5, 12);
            p.quadTo(12, 3, 21.5, 12);
            p.quadTo(12, 21, 2.5, 12);
            p.closePath();
            g.draw(p);
            circle(g, 12, 12, 3);
        }

        private void draw(Graphics2D g) {
            Path2D p = new Path2D.Double();
            switch (kind) {
                case HOME:
                    poly(g, 3, 11, 12, 3.5, 21, 11);
                    poly(g, 5.5, 9.5, 5.5, 20.5, 18.5, 20.5, 18.5, 9.5);
                    poly(g, 10, 20.5, 10, 15, 14, 15, 14, 20.5);
                    break;
                case PLANS:
                    rrect(g, 3.5, 5, 17, 15.5, 4);
                    line(g, 3.5, 10, 20.5, 10);
                    line(g, 8, 3, 8, 7);
                    line(g, 16, 3, 16, 7);
                    poly(g, 9, 15, 11, 17, 15, 13);
                    break;
                case CALENDAR:
                    rrect(g, 3.5, 5, 17, 15.5, 4);
                    line(g, 3.5, 10, 20.5, 10);
                    line(g, 8, 3, 8, 7);
                    line(g, 16, 3, 16, 7);
                    break;
                case PROGRESS:
                    line(g, 6, 20, 6, 13);
                    line(g, 12, 20, 12, 5);
                    line(g, 18, 20, 18, 10);
                    break;
                case PROFILE:
                    circle(g, 12, 8, 4);
                    arc(g, 4.5, 13.5, 15, 14, 0, 180);
                    break;
                case USER_EDIT:
                    circle(g, 10, 8, 4);
                    arc(g, 3, 13.5, 14, 14, 0, 180);
                    line(g, 16.5, 15.5, 21, 11);
                    break;
                case SETTINGS:
                    circle(g, 12, 12, 3);
                    circle(g, 12, 12, 7);
                    for (int i = 0; i < 8; i++) {
                        double a = Math.PI / 4 * i;
                        line(g, 12 + 7 * Math.cos(a), 12 + 7 * Math.sin(a), 12 + 9.5 * Math.cos(a), 12 + 9.5 * Math.sin(a));
                    }
                    break;
                case FLAME:
                    p.moveTo(12, 2.5);
                    p.curveTo(13, 7, 18, 9, 18, 14.5);
                    p.curveTo(18, 18.5, 15.3, 21.5, 12, 21.5);
                    p.curveTo(8.7, 21.5, 6, 18.5, 6, 15);
                    p.curveTo(6, 12, 7.5, 10.5, 8.5, 9.5);
                    p.curveTo(8.8, 11.5, 9.7, 12.5, 10.8, 12.8);
                    p.curveTo(10.3, 9, 11, 5.5, 12, 2.5);
                    p.closePath();
                    g.draw(p);
                    break;
                case PULSE:
                    poly(g, 2.5, 12, 7, 12, 9.5, 5.5, 14, 18.5, 16.5, 12, 21.5, 12);
                    break;
                case DROP:
                    p.moveTo(12, 3);
                    p.curveTo(15, 7, 19, 10.5, 19, 14.5);
                    p.curveTo(19, 18.6, 15.9, 21, 12, 21);
                    p.curveTo(8.1, 21, 5, 18.6, 5, 14.5);
                    p.curveTo(5, 10.5, 9, 7, 12, 3);
                    p.closePath();
                    g.draw(p);
                    break;
                case DUMBBELL:
                    line(g, 7.5, 12, 16.5, 12);
                    rrect(g, 4.5, 7.5, 3, 9, 1.5);
                    rrect(g, 16.5, 7.5, 3, 9, 1.5);
                    line(g, 2.5, 10, 2.5, 14);
                    line(g, 21.5, 10, 21.5, 14);
                    break;
                case CHECK:
                    poly(g, 5, 12.5, 10, 17.5, 19, 7);
                    break;
                case CHECK_CIRCLE:
                    circle(g, 12, 12, 9);
                    poly(g, 8, 12.5, 11, 15.5, 16.5, 9.5);
                    break;
                case CIRCLE:
                    circle(g, 12, 12, 9);
                    break;
                case X_CIRCLE:
                    circle(g, 12, 12, 9);
                    line(g, 9, 9, 15, 15);
                    line(g, 15, 9, 9, 15);
                    break;
                case CHEVRON_RIGHT:
                    poly(g, 9, 5.5, 15.5, 12, 9, 18.5);
                    break;
                case CHEVRON_LEFT:
                    poly(g, 15, 5.5, 8.5, 12, 15, 18.5);
                    break;
                case CHEVRON_DOWN:
                    poly(g, 6, 9, 12, 15, 18, 9);
                    break;
                case SEARCH:
                    circle(g, 10.5, 10.5, 6.5);
                    line(g, 15.5, 15.5, 20.5, 20.5);
                    break;
                case MAIL:
                    rrect(g, 3, 5.5, 18, 13, 3);
                    poly(g, 3.5, 7, 12, 13, 20.5, 7);
                    break;
                case LOCK:
                    rrect(g, 5, 10.5, 14, 10, 3);
                    arc(g, 8, 3.5, 8, 10, 0, 180);
                    line(g, 8, 8.5, 8, 10.5);
                    line(g, 16, 8.5, 16, 10.5);
                    break;
                case SHIELD:
                    p.moveTo(12, 3);
                    p.lineTo(19.5, 6);
                    p.lineTo(19.5, 11.5);
                    p.curveTo(19.5, 16, 16.5, 19.5, 12, 21);
                    p.curveTo(7.5, 19.5, 4.5, 16, 4.5, 11.5);
                    p.lineTo(4.5, 6);
                    p.closePath();
                    g.draw(p);
                    break;
                case EYE:
                    eye(g);
                    break;
                case EYE_OFF:
                    eye(g);
                    line(g, 4, 4, 20, 20);
                    break;
                case CLOCK:
                    circle(g, 12, 12, 9);
                    poly(g, 12, 7, 12, 12, 15.5, 14);
                    break;
                case RUN:
                    g.draw(new Ellipse2D.Double(5, 3.5, 5, 8));
                    g.draw(new Ellipse2D.Double(5.5, 13.5, 4, 3.5));
                    g.draw(new Ellipse2D.Double(14, 7.5, 5, 8));
                    g.draw(new Ellipse2D.Double(14.5, 17.5, 4, 3.5));
                    break;
                case PLUS:
                    line(g, 12, 5, 12, 19);
                    line(g, 5, 12, 19, 12);
                    break;
                case MINUS:
                    line(g, 5, 12, 19, 12);
                    break;
                case CLOSE:
                    line(g, 6, 6, 18, 18);
                    line(g, 18, 6, 6, 18);
                    break;
                case LOGOUT:
                    poly(g, 10, 4, 5, 4, 5, 20, 10, 20);
                    line(g, 9.5, 12, 20, 12);
                    poly(g, 16, 8, 20, 12, 16, 16);
                    break;
                case TROPHY:
                    p.moveTo(7, 4);
                    p.lineTo(17, 4);
                    p.lineTo(17, 9);
                    p.curveTo(17, 12.5, 14.8, 15, 12, 15);
                    p.curveTo(9.2, 15, 7, 12.5, 7, 9);
                    p.closePath();
                    g.draw(p);
                    arc(g, 3, 5, 5, 6, 90, 180);
                    arc(g, 16, 5, 5, 6, -90, 180);
                    line(g, 12, 15, 12, 19);
                    line(g, 8, 20, 16, 20);
                    break;
                case HEART:
                    p.moveTo(12, 20);
                    p.curveTo(5, 15.5, 3, 12, 3, 8.8);
                    p.curveTo(3, 6, 5.2, 4, 7.6, 4);
                    p.curveTo(9.4, 4, 11, 5, 12, 6.6);
                    p.curveTo(13, 5, 14.6, 4, 16.4, 4);
                    p.curveTo(18.8, 4, 21, 6, 21, 8.8);
                    p.curveTo(21, 12, 19, 15.5, 12, 20);
                    p.closePath();
                    g.draw(p);
                    break;
                case UPDOWN:
                    line(g, 8, 4, 8, 20);
                    poly(g, 4.5, 7.5, 8, 4, 11.5, 7.5);
                    line(g, 16, 4, 16, 20);
                    poly(g, 12.5, 16.5, 16, 20, 19.5, 16.5);
                    break;
                case TREND:
                    poly(g, 3, 17, 9, 11, 13, 15, 21, 7);
                    poly(g, 15, 7, 21, 7, 21, 13);
                    break;
                case PALETTE:
                    circle(g, 12, 12, 9);
                    dot(g, 8, 10, 1.4);
                    dot(g, 12, 7.5, 1.4);
                    dot(g, 16, 10, 1.4);
                    dot(g, 15, 14.5, 1.4);
                    break;
                case BELL:
                    p.moveTo(6, 17);
                    p.lineTo(6, 11);
                    p.curveTo(6, 7.5, 8.7, 5, 12, 5);
                    p.curveTo(15.3, 5, 18, 7.5, 18, 11);
                    p.lineTo(18, 17);
                    g.draw(p);
                    line(g, 4.5, 17, 19.5, 17);
                    line(g, 10, 20.5, 14, 20.5);
                    line(g, 12, 3, 12, 5);
                    break;
                case GLOBE:
                    circle(g, 12, 12, 9);
                    line(g, 3, 12, 21, 12);
                    g.draw(new Ellipse2D.Double(8, 3, 8, 18));
                    break;
                case RULER:
                    p.moveTo(3, 15.5);
                    p.lineTo(15.5, 3);
                    p.lineTo(21, 8.5);
                    p.lineTo(8.5, 21);
                    p.closePath();
                    g.draw(p);
                    line(g, 7, 11.5, 9, 13.5);
                    line(g, 10, 8.5, 12, 10.5);
                    line(g, 13, 5.5, 15, 7.5);
                    break;
                case HELP:
                    circle(g, 12, 12, 9);
                    p.moveTo(9.5, 9.5);
                    p.curveTo(9.5, 6.8, 14.5, 6.8, 14.5, 9.6);
                    p.curveTo(14.5, 11.6, 12, 11.8, 12, 14);
                    g.draw(p);
                    dot(g, 12, 17, 1.1);
                    break;
                case INFO:
                    circle(g, 12, 12, 9);
                    line(g, 12, 11, 12, 16.5);
                    dot(g, 12, 7.8, 1.1);
                    break;
                case KEY:
                    circle(g, 7.5, 12, 4);
                    line(g, 11.5, 12, 21, 12);
                    line(g, 18, 12, 18, 15);
                    line(g, 15, 12, 15, 14.5);
                    break;
                case EDIT:
                    p.moveTo(4, 20);
                    p.lineTo(4.5, 16);
                    p.lineTo(16, 4.5);
                    p.lineTo(19.5, 8);
                    p.lineTo(8, 19.5);
                    p.closePath();
                    g.draw(p);
                    line(g, 13.5, 7, 17, 10.5);
                    break;
                case TRASH:
                    line(g, 4, 7, 20, 7);
                    poly(g, 6, 7, 7, 20, 17, 20, 18, 7);
                    poly(g, 9, 7, 9, 4, 15, 4, 15, 7);
                    line(g, 10, 11, 10, 16);
                    line(g, 14, 11, 14, 16);
                    break;
                case SCALE:
                    rrect(g, 3.5, 3.5, 17, 17, 4);
                    arc(g, 7, 6.5, 10, 8, 0, 180);
                    line(g, 12, 10.5, 14, 8);
                    break;
                case TARGET:
                    circle(g, 12, 12, 9);
                    circle(g, 12, 12, 5);
                    dot(g, 12, 12, 1.4);
                    break;
                case GLASS:
                    poly(g, 6, 3.5, 18, 3.5, 16.5, 20.5, 7.5, 20.5, 6, 3.5);
                    line(g, 6.8, 9, 17.2, 9);
                    break;
                case BOLT:
                    p.moveTo(13, 2.5);
                    p.lineTo(4.5, 13.5);
                    p.lineTo(11.5, 13.5);
                    p.lineTo(10.5, 21.5);
                    p.lineTo(19.5, 10);
                    p.lineTo(12.5, 10);
                    p.closePath();
                    g.draw(p);
                    break;
                case BOOK:
                    rrect(g, 4.5, 3, 15, 18, 3);
                    line(g, 8.5, 3, 8.5, 21);
                    line(g, 11.5, 8, 16, 8);
                    break;
                default:
                    break;
            }
        }
    }
}
