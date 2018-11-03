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
package org.jackhuang.hmcl.auth.authlibinjector;

import org.jackhuang.hmcl.auth.AuthInfo;
import org.jackhuang.hmcl.auth.AuthenticationException;
import org.jackhuang.hmcl.auth.CharacterSelector;
import org.jackhuang.hmcl.auth.ServerDisconnectException;
import org.jackhuang.hmcl.auth.yggdrasil.YggdrasilAccount;
import org.jackhuang.hmcl.auth.yggdrasil.YggdrasilService;
import org.jackhuang.hmcl.auth.yggdrasil.YggdrasilSession;
import org.jackhuang.hmcl.game.Arguments;
import org.jackhuang.hmcl.util.function.ExceptionalSupplier;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import static org.jackhuang.hmcl.util.io.IOUtils.readFullyWithoutClosing;

public class AuthlibInjectorAccount extends YggdrasilAccount {
    private AuthlibInjectorServer server;
    private ExceptionalSupplier<AuthlibInjectorArtifactInfo, ? extends IOException> authlibInjectorDownloader;

    protected AuthlibInjectorAccount(YggdrasilService service, AuthlibInjectorServer server, ExceptionalSupplier<AuthlibInjectorArtifactInfo, ? extends IOException> authlibInjectorDownloader, String username, UUID characterUUID, YggdrasilSession session) {
        super(service, username, characterUUID, session);

        this.authlibInjectorDownloader = authlibInjectorDownloader;
        this.server = server;
    }

    @Override
    public synchronized AuthInfo logIn() throws AuthenticationException {
        return inject(super::logIn);
    }

    @Override
    protected AuthInfo logInWithPassword(String password, CharacterSelector selector) throws AuthenticationException {
        return inject(() -> super.logInWithPassword(password, selector));
    }

    private AuthInfo inject(ExceptionalSupplier<AuthInfo, AuthenticationException> loginAction) throws AuthenticationException {
        CompletableFuture<byte[]> prefetchedMetaTask = CompletableFuture.supplyAsync(() -> {
            try (InputStream in = new URL(server.getUrl()).openStream()) {
                return readFullyWithoutClosing(in);
            } catch (IOException e) {
                throw new CompletionException(new ServerDisconnectException(e));
            }
        });

        CompletableFuture<AuthlibInjectorArtifactInfo> artifactTask = CompletableFuture.supplyAsync(() -> {
            try {
                return authlibInjectorDownloader.get();
            } catch (IOException e) {
                throw new CompletionException(new AuthlibInjectorDownloadException(e));
            }
        });

        AuthInfo auth = loginAction.get();
        byte[] prefetchedMeta;
        AuthlibInjectorArtifactInfo artifact;

        try {
            prefetchedMeta = prefetchedMetaTask.get();
            artifact = artifactTask.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthenticationException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof AuthenticationException) {
                throw (AuthenticationException) e.getCause();
            } else {
                throw new AuthenticationException(e.getCause());
            }
        }

        return auth.withArguments(new Arguments().addJVMArguments(
                "-javaagent:" + artifact.getLocation().toString() + "=" + server.getUrl(),
                "-Dauthlibinjector.side=client",
                "-Dorg.to2mbn.authlibinjector.config.prefetched=" + Base64.getEncoder().encodeToString(prefetchedMeta)));
    }

    @Override
    public Map<Object, Object> toStorage() {
        Map<Object, Object> map = super.toStorage();
        map.put("serverBaseURL", server.getUrl());
        return map;
    }

    public AuthlibInjectorServer getServer() {
        return server;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), server.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuthlibInjectorAccount))
            return false;
        AuthlibInjectorAccount another = (AuthlibInjectorAccount) obj;
        return super.equals(another) && server.equals(another.server);
    }
}
