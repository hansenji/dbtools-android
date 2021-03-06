/*
 * DatabaseBaseManager.kt
 *
 * GENERATED FILE - DO NOT EDIT
 * CHECKSTYLE:OFF
 * 
 */



package org.dbtools.sample.kotlin.model.database

import org.dbtools.android.domain.AndroidDatabase
import org.dbtools.android.domain.AndroidBaseManager
import org.dbtools.android.domain.AndroidDatabaseManager
import org.dbtools.android.domain.config.DatabaseConfig


@Suppress("unused", "ConvertSecondaryConstructorToPrimary", "RemoveEmptySecondaryConstructorBody")
@SuppressWarnings("all")
abstract class DatabaseBaseManager  : AndroidDatabaseManager {


    constructor(databaseConfig: DatabaseConfig) : super(databaseConfig) {
    }

    open fun createMainTables(androidDatabase: AndroidDatabase) {
        val database = androidDatabase.databaseWrapper
        database.beginTransaction()
        
        // Enum Tables
        
        // Tables
        AndroidBaseManager.createTable(database, org.dbtools.sample.kotlin.model.database.main.individual.IndividualConst.CREATE_TABLE)
        AndroidBaseManager.createTable(database, org.dbtools.sample.kotlin.model.database.main.individualdata.IndividualDataConst.CREATE_TABLE)
        
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    override fun onCreate(androidDatabase: AndroidDatabase) {
        logger.i(TAG, "Creating database: ${androidDatabase.name}")
        if (androidDatabase.name == DatabaseManagerConst.MAIN_DATABASE_NAME) {
            createMainTables(androidDatabase)
        }
    }

    open fun createMainViews(androidDatabase: AndroidDatabase) {
        val database = androidDatabase.databaseWrapper
        database.beginTransaction()
        
        // Views
        AndroidBaseManager.createTable(database, org.dbtools.sample.kotlin.model.database.main.individualview.IndividualViewManager.CREATE_VIEW)
        
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    open fun dropMainViews(androidDatabase: AndroidDatabase) {
        val database = androidDatabase.databaseWrapper
        database.beginTransaction()
        
        // Views
        AndroidBaseManager.dropTable(database, org.dbtools.sample.kotlin.model.database.main.individualview.IndividualViewManager.DROP_VIEW)
        
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    override fun onCreateViews(androidDatabase: AndroidDatabase) {
        logger.i(TAG, "Creating database views: ${androidDatabase.name}")
        if (androidDatabase.name == DatabaseManagerConst.MAIN_DATABASE_NAME) {
            createMainViews(androidDatabase)
        }
    }

    override fun onDropViews(androidDatabase: AndroidDatabase) {
        logger.i(TAG, "Dropping database views: ${androidDatabase.name}")
        if (androidDatabase.name == DatabaseManagerConst.MAIN_DATABASE_NAME) {
            dropMainViews(androidDatabase)
        }
    }


}