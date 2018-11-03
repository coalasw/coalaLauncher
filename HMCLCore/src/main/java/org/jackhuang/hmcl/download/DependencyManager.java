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
package org.jackhuang.hmcl.download;

import org.jackhuang.hmcl.game.GameRepository;
import org.jackhuang.hmcl.game.Version;
import org.jackhuang.hmcl.task.Task;
import org.jackhuang.hmcl.util.CacheRepository;

/**
 * Do everything that will connect to Internet.
 * Downloading Minecraft files.
 *
 * @author huangyuhui
 */
public interface DependencyManager {

    /**
     * The relied game repository.
     */
    GameRepository getGameRepository();

    /**
     * The cache repository
     */
    CacheRepository getCacheRepository();

    /**
     * Check if the game is complete.
     * Check libraries, assets files and so on.
     *
     * @return the task to check game completion.
     */
    Task checkGameCompletionAsync(Version version);

    /**
     * Check if the game is complete.
     * Check libraries, assets files and so on.
     *
     * @return the task to check game completion.
     */
    Task checkLibraryCompletionAsync(Version version);

    /**
     * The builder to build a brand new game then libraries such as Forge, LiteLoader and OptiFine.
     */
    GameBuilder gameBuilder();

    /**
     * Install a library to a version.
     * **Note**: Installing a library may change the version.json.
     *
     * @param gameVersion the Minecraft version that the library relies on.
     * @param version the version.json.
     * @param libraryId the type of being installed library. i.e. "forge", "liteloader", "optifine"
     * @param libraryVersion the version of being installed library.
     * @return the task to install the specific library.
     */
    Task installLibraryAsync(String gameVersion, Version version, String libraryId, String libraryVersion);

    /**
     * Install a library to a version.
     * **Note**: Installing a library may change the version.json.
     *
     * @param version the version.json.\
     * @param libraryVersion the remote version of being installed library.
     * @return the task to install the specific library.
     */
    Task installLibraryAsync(Version version, RemoteVersion libraryVersion);

    /**
     * Get registered version list.
     *
     * @param id the id of version list. i.e. game, forge, liteloader, optifine
     * @throws IllegalArgumentException if the version list of specific id is not found.
     */
    VersionList<?> getVersionList(String id);
}
