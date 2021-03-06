package org.dbtools.sample.kotlin.model.database

import org.dbtools.android.domain.config.BuildEnv
import org.dbtools.android.domain.config.TestDatabaseConfig

class TestMainDatabaseConfig(databaseFilename: String) :
        TestDatabaseConfig(DatabaseManagerConst.MAIN_DATABASE_NAME, BuildEnv.GRADLE, databaseFilename, DatabaseManager.MAIN_TABLES_VERSION, DatabaseManager.MAIN_VIEWS_VERSION)
