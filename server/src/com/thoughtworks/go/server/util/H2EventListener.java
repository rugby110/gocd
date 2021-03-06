/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.server.util;

import java.sql.SQLException;

import org.h2.api.DatabaseEventListener;
import org.apache.log4j.Logger;

public class H2EventListener implements DatabaseEventListener {
    private static final Logger LOGGER = Logger.getLogger(H2EventListener.class);
    private int listenerLastState;
    private long startTime;

    public void init(String url) {
        LOGGER.info("Initializing database: " + url);
    }

    public void opened() {
        LOGGER.info("Database is opened");
    }

    public void exceptionThrown(SQLException e, String sql) {
        LOGGER.error("Exception thrown from database on sql statement: " + sql, e);
    }

    public void setProgress(int state, String name, int progress, int max) {
        if (state == listenerLastState) {
            if (System.currentTimeMillis() - startTime > 10000) {
                logState(state, name, progress, max);
                startTime = System.currentTimeMillis();
            }
        } else {
            listenerLastState = state;
            logState(state, name, progress, max);
        }
    }

    void logState(int state, String name, int progress, int max) {
        switch (state) {
            case DatabaseEventListener.STATE_BACKUP_FILE:
                log("Backing up " + name + " " + (100L * progress / max) + "%");
                break;
            case DatabaseEventListener.STATE_CREATE_INDEX:
                log("Creating index " + name + " " + (100L * progress / max) + "%");
                break;
            case DatabaseEventListener.STATE_RECOVER:
                log("Recovering " + name + " " + (100L * progress / max) + "%");
                break;
            case DatabaseEventListener.STATE_SCAN_FILE:
                log("Scanning file " + name + " " + (100L * progress / max) + "%");
                break;
            case DatabaseEventListener.STATE_STATEMENT_START:
            case DatabaseEventListener.STATE_STATEMENT_END:
            case DatabaseEventListener.STATE_STATEMENT_PROGRESS:
                break;
            default:
                log("Unknown state: " + state);
        }
    }

    public void closingDatabase() {
        LOGGER.info("Closing database");
    }

    void log(String message) {
        System.out.println(message);
        LOGGER.info(message);
    }
}
