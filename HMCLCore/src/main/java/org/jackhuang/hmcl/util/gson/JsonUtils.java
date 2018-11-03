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
package org.jackhuang.hmcl.util.gson;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.jackhuang.hmcl.game.Argument;
import org.jackhuang.hmcl.game.Library;
import org.jackhuang.hmcl.game.RuledArgument;
import org.jackhuang.hmcl.game.StringArgument;
import org.jackhuang.hmcl.util.platform.Platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * @author yushijinhun
 */
public final class JsonUtils {

    public static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .registerTypeAdapter(Library.class, Library.Serializer.INSTANCE)
            .registerTypeAdapter(Argument.class, Argument.Serializer.INSTANCE)
            .registerTypeAdapter(StringArgument.class, Argument.Serializer.INSTANCE)
            .registerTypeAdapter(RuledArgument.class, RuledArgument.Serializer.INSTANCE)
            .registerTypeAdapter(Date.class, DateTypeAdapter.INSTANCE)
            .registerTypeAdapter(UUID.class, UUIDTypeAdapter.INSTANCE)
            .registerTypeAdapter(Platform.class, Platform.Serializer.INSTANCE)
            .registerTypeAdapter(File.class, FileTypeAdapter.INSTANCE)
            .registerTypeAdapterFactory(ValidationTypeAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(LowerCaseEnumTypeAdapterFactory.INSTANCE)
            .create();

    private JsonUtils() {
    }

    public static <T> T fromNonNullJson(String json, Class<T> classOfT) throws JsonParseException {
        T parsed = GSON.fromJson(json, classOfT);
        if (parsed == null)
            throw new JsonParseException("Json object cannot be null.");
        return parsed;
    }
}
