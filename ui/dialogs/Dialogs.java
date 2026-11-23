package myweigh.ui.dialogs;

import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Themed replacements for JOptionPane: message, confirm, prompt and choice dialogs. */
public final class Dialogs {
    private Dialogs() {
    }

    public static void info(Component parent, String title, String message) {
        new MessageDialog(parent, title, message, null, false).showDialog();
    }

    public static void error(Component parent, String message) {
        info(parent, "Please check your input", message);
    }

    public static boolean confirm(Component parent, String title, String message, String okText, boolean danger) {
        MessageDialog d = new MessageDialog(parent, title, message, okText, danger);
        d.showDialog();
        return d.confirmed;
    }

    public static String prompt(Component parent, String title, String label, String initial) {
        PromptDialog d = new PromptDialog(parent, title, label, initial);
        d.showDialog();
        return d.result;
    }

    public static <T> T choose(Component parent, String title, List<T> options, T current, Function<T, String> labeler) {
        ChoiceDialog<T> d = new ChoiceDialog<>(parent, title, options, current, labeler);
        d.showDialog();
        return d.result;
    }

    private static final class MessageDialog extends AppDialog {
        private boolean confirmed;

        MessageDialog(Component parent, String title, String message, String okText, boolean danger) {
            super(parent, title);
            content.add(new WrapText(message, Theme.font(Font.PLAIN, 14), Theme.get().text));
            if (okText == null) {
                PillButton ok = new PillButton("OK", PillButton.Style.PRIMARY);
                ok.addActionListener(e -> dispose());
                addFooterButton(ok);
            } else {
                PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
                cancel.addActionListener(e -> dispose());
                PillButton ok = new PillButton(okText, danger ? PillButton.Style.DANGER : PillButton.Style.PRIMARY);
                ok.addActionListener(e -> {
                    confirmed = true;
                    dispose();
                });
                addFooterButton(cancel);
                addFooterButton(ok);
            }
        }

        @Override
        protected int dialogWidth() {
            return 400;
        }
    }

    private static final class PromptDialog extends AppDialog {
        private String result;

        PromptDialog(Component parent, String title, String label, String initial) {
            super(parent, title);
            InputField field = new InputField(Icons.Kind.SEARCH, label, false, null);
            field.setText(initial == null ? "" : initial);
            content.add(UIFactory.fieldGroup(label, field));
            PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
            cancel.addActionListener(e -> dispose());
            PillButton ok = new PillButton("Apply", PillButton.Style.PRIMARY);
            Runnable submit = () -> {
                result = field.getText().trim();
                dispose();
            };
            ok.addActionListener(e -> submit.run());
            field.addActionListener(e -> submit.run());
            addFooterButton(cancel);
            addFooterButton(ok);
        }

        @Override
        protected int dialogWidth() {
            return 400;
        }
    }

    private static final class ChoiceDialog<T> extends AppDialog {
        private T result;
        private T pending;
        private final List<JLabel> marks = new ArrayList<>();

        ChoiceDialog(Component parent, String title, List<T> options, T current, Function<T, String> labeler) {
            super(parent, title);
            pending = current;
            Theme t = Theme.get();
            for (T option : options) {
                JLabel mark = new JLabel();
                marks.add(mark);
                content.add(new ListCard(null, labeler.apply(option), null, mark, false, () -> {
                    pending = option;
                    refresh(options);
                }));
            }
            refresh(options);
            PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
            cancel.addActionListener(e -> dispose());
            PillButton ok = new PillButton("Select", PillButton.Style.PRIMARY);
            ok.addActionListener(e -> {
                result = pending;
                dispose();
            });
            addFooterButton(cancel);
            addFooterButton(ok);
        }

        private void refresh(List<T> options) {
            Theme t = Theme.get();
            for (int i = 0; i < options.size(); i++) {
                boolean on = options.get(i).equals(pending);
                marks.get(i).setIcon(Icons.of(on ? Icons.Kind.CHECK_CIRCLE : Icons.Kind.CIRCLE, 22, on ? t.primary : t.border));
            }
        }

        @Override
        protected int dialogWidth() {
            return 400;
        }
    }
}
