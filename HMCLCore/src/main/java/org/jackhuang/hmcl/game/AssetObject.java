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

import com.google.gson.JsonParseException;
import org.jackhuang.hmcl.util.StringUtils;
import org.jackhuang.hmcl.util.gson.Validation;

/**
 *
 * @author huangyuhui
 */
public final class AssetObject implements Validation {

    private final String hash;
    private final long size;

    public AssetObject() {
        this("", 0);
    }

    public AssetObject(String hash, long size) {
        this.hash = hash;
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public long getSize() {
        return size;
    }

    public String getLocation() {
        return hash.substring(0, 2) + "/" + hash;
    }

    @Override
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(hash) || hash.length() < 2)
            throw new IllegalStateException("AssetObject hash cannot be blank.");
    }
}
