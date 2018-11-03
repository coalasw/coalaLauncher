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
package org.jackhuang.hmcl.util.function;

import java.util.concurrent.Callable;

/**
 *
 * @author huangyuhui
 */
public interface ExceptionalRunnable<E extends Exception> {

    void run() throws E;

    default Callable<?> toCallable() {
        return () -> {
            run();
            return null;
        };
    }

    static ExceptionalRunnable<?> fromRunnable(Runnable r) {
        return r::run;
    }

}
