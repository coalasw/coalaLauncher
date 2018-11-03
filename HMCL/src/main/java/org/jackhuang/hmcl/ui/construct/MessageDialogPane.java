/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2018  huangyuhui <huanghongxun4008@126.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.ui.construct;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.jackhuang.hmcl.setting.Theme;
import org.jackhuang.hmcl.ui.FXUtils;
import org.jackhuang.hmcl.ui.SVG;

import java.util.Optional;

import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public final class MessageDialogPane extends StackPane {

    @FXML
    private JFXButton acceptButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private Label content;
    @FXML
    private Label graphic;
    @FXML
    private Label title;
    @FXML
    private HBox actions;

    public MessageDialogPane(String text, String title, int type, Runnable onAccept) {
        FXUtils.loadFXML(this, "/assets/fxml/message-dialog.fxml");

        if (title != null)
            this.title.setText(title);

        content.setText(text);
        acceptButton.setOnMouseClicked(e -> {
            fireEvent(new DialogCloseEvent());
            Optional.ofNullable(onAccept).ifPresent(Runnable::run);
        });

        actions.getChildren().remove(cancelButton);

        switch (type) {
            case MessageBox.INFORMATION_MESSAGE:
                graphic.setGraphic(SVG.infoCircle(Theme.blackFillBinding(), 40, 40));
                break;
            case MessageBox.ERROR_MESSAGE:
                graphic.setGraphic(SVG.closeCircle(Theme.blackFillBinding(), 40, 40));
                break;
            case MessageBox.FINE_MESSAGE:
                graphic.setGraphic(SVG.checkCircle(Theme.blackFillBinding(), 40, 40));
                break;
            case MessageBox.WARNING_MESSAGE:
                graphic.setGraphic(SVG.alert(Theme.blackFillBinding(), 40, 40));
                break;
            case MessageBox.QUESTION_MESSAGE:
                graphic.setGraphic(SVG.helpCircle(Theme.blackFillBinding(), 40, 40));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message box message type " + type);
        }
    }

    public MessageDialogPane(String text, String title, Runnable onAccept, Runnable onCancel) {
        this(text, title, MessageBox.QUESTION_MESSAGE, onAccept);

        cancelButton.setVisible(true);
        cancelButton.setOnMouseClicked(e -> {
            fireEvent(new DialogCloseEvent());
            Optional.ofNullable(onCancel).ifPresent(Runnable::run);
        });

        acceptButton.setText(i18n("button.yes"));
        cancelButton.setText(i18n("button.no"));

        actions.getChildren().add(cancelButton);
    }
}
