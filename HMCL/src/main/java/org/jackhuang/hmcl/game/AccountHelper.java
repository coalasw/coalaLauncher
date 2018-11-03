/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2018  huangyuhui <huanghongxun2008@126.com>
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
package org.jackhuang.hmcl.game;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jackhuang.hmcl.Metadata;
import org.jackhuang.hmcl.auth.Account;
import org.jackhuang.hmcl.auth.yggdrasil.GameProfile;
import org.jackhuang.hmcl.auth.yggdrasil.Texture;
import org.jackhuang.hmcl.auth.yggdrasil.YggdrasilAccount;
import org.jackhuang.hmcl.setting.Accounts;
import org.jackhuang.hmcl.task.FileDownloadTask;
import org.jackhuang.hmcl.task.Scheduler;
import org.jackhuang.hmcl.task.Schedulers;
import org.jackhuang.hmcl.task.Task;
import org.jackhuang.hmcl.ui.DialogController;
import org.jackhuang.hmcl.util.io.NetworkUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public final class AccountHelper {

    private AccountHelper() {}

    public static final File SKIN_DIR = Metadata.HMCL_DIRECTORY.resolve("skins").toFile();

    public static void loadSkins() {
        for (Account account : Accounts.getAccounts()) {
            if (account instanceof YggdrasilAccount) {
                new SkinLoadTask((YggdrasilAccount) account, false).start();
            }
        }
    }

    public static Task loadSkinAsync(YggdrasilAccount account) {
        return new SkinLoadTask(account, false);
    }

    public static Task refreshSkinAsync(YggdrasilAccount account) {
        return new SkinLoadTask(account, true);
    }

    private static File getSkinFile(UUID uuid) {
        return new File(SKIN_DIR, uuid + ".png");
    }

    public static Image getSkin(YggdrasilAccount account) {
        return getSkin(account, 1);
    }

    public static Image getSkin(YggdrasilAccount account, double scaleRatio) {
        UUID uuid = account.getUUID();
        if (uuid == null)
            return getSteveSkin(scaleRatio);

        File file = getSkinFile(uuid);
        if (file.exists()) {
            Image original = new Image("file:" + file.getAbsolutePath());
            return new Image("file:" + file.getAbsolutePath(),
                    original.getWidth() * scaleRatio,
                    original.getHeight() * scaleRatio,
                    false, false);
        }
        return getDefaultSkin(uuid, scaleRatio);
    }

    public static Image getSkinImmediately(YggdrasilAccount account, GameProfile profile, double scaleRatio) throws Exception {
        File file = getSkinFile(profile.getId());
        downloadSkin(account, profile, true);
        if (!file.exists())
            return getDefaultSkin(profile.getId(), scaleRatio);

        String url = "file:" + file.getAbsolutePath();
        return scale(url, scaleRatio);
    }

    public static Image getHead(Image skin, int scaleRatio) {
        final int size = 8 * scaleRatio;
        final int faceOffset = (int) Math.round(scaleRatio * 4d / 9d);
        BufferedImage image = SwingFXUtils.fromFXImage(skin, null);
        BufferedImage head = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = head.createGraphics();
        g2d.drawImage(image, faceOffset, faceOffset, size - faceOffset, size - faceOffset,
                size, size, size + size, size + size, null);
        if (image.getHeight() > 32) {
            g2d.drawImage(image, 0, 0, size, size,
                    40 * scaleRatio, 8 * scaleRatio, 48 * scaleRatio, 16 * scaleRatio, null);
        }
        return SwingFXUtils.toFXImage(head, null);
    }

    private static class SkinLoadTask extends Task {
        private final YggdrasilAccount account;
        private final boolean refresh;
        private final List<Task> dependencies = new LinkedList<>();

        public SkinLoadTask(YggdrasilAccount account, boolean refresh) {
            this.account = account;
            this.refresh = refresh;
        }

        @Override
        public Scheduler getScheduler() {
            return Schedulers.io();
        }

        @Override
        public Collection<Task> getDependencies() {
            return dependencies;
        }

        @Override
        public void execute() throws Exception {
            if (!account.isLoggedIn() && (account.getCharacter() == null || refresh))
                DialogController.logIn(account);

            downloadSkin(account, refresh);
        }
    }

    private static void downloadSkin(YggdrasilAccount account, GameProfile profile, boolean refresh) throws Exception {
        account.clearCache();

        File file = getSkinFile(profile.getId());
        if (!refresh && file.exists())
            return;
        Optional<Texture> texture = account.getSkin(profile);
        if (!texture.isPresent()) return;
        String url = texture.get().getUrl();
        new FileDownloadTask(NetworkUtils.toURL(url), file).run();
    }

    private static void downloadSkin(YggdrasilAccount account, boolean refresh) throws Exception {
        account.clearCache();

        if (account.getCharacter() == null) return;
        File file = getSkinFile(account.getUUID());
        if (!refresh && file.exists())
            return;
        Optional<Texture> texture = account.getSkin();
        if (!texture.isPresent()) return;
        String url = texture.get().getUrl();
        new FileDownloadTask(NetworkUtils.toURL(url), file).run();
    }

    public static Image scale(String url, double scaleRatio) {
        Image origin = new Image(url);
        return new Image(url,
                origin.getWidth() * scaleRatio,
                origin.getHeight() * scaleRatio,
                false, false);
    }

    public static Image getSteveSkin(double scaleRatio) {
        return scale("/assets/img/steve.png", scaleRatio);
    }

    public static Image getAlexSkin(double scaleRatio) {
        return scale("/assets/img/alex.png", scaleRatio);
    }

    public static Image getDefaultSkin(UUID uuid, double scaleRatio) {
        int type = uuid.hashCode() & 1;
        if (type == 1)
            return getAlexSkin(scaleRatio);
        else
            return getSteveSkin(scaleRatio);
    }
}
