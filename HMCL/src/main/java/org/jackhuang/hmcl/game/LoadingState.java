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

import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public enum LoadingState {
    DEPENDENCIES("launch.state.dependencies"),
    MODS("launch.state.modpack"),
    LOGGING_IN("launch.state.logging_in"),
    LAUNCHING("launch.state.waiting_launching"),
    DONE("launch.state.done");

    private final String key;

    LoadingState(String key) {
        this.key = key;
    }

    public String getLocalizedMessage() {
        return i18n(key);
    }
}
