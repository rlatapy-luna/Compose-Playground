package rlatapy.composeplayground

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.dynamic.DynamicMutableRealmObject
import io.realm.kotlin.dynamic.getValue
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.migration.AutomaticSchemaMigration
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.TypedRealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import kotlin.random.Random
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val realm = List(RealmManager.migrations.size) { index ->
            runCatching {
                RealmManager(this, index).realm
            }.getOrElse {
                Log.e("migration", "Migration #$index failed", it)
                null
            }
        }.first { it != null }!!

        val albumFlow: Flow<String> = realm.query<RealmNewAlbum>()
            .asFlow()
            .map { resultsChange ->
                resultsChange.list.joinToString("\n\n")
            }


        setContent {
            val albumString by albumFlow.collectAsStateWithLifecycle("")

            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Realm schema version = ${realm.schemaVersion()}")
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    Text(albumString)
                }
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        realm.writeBlocking {
                            copyToRealm(
                                RealmNewAlbum().apply {
                                    name = "MyAlbum ${Random.nextInt(0, 100)}"
                                    author = RealmAuthor().apply {
                                        name = "Author ${Random.nextInt(0, 100)}"
                                    }
                                    titles = List(Random.nextInt(0, 5)) {
                                        RealmTitle().apply {
                                            name = "MyTitle ${Random.nextInt(0, 100)}"
                                            duration = 100
                                        }
                                    }.toRealmList()
                                },
                            )
                        }
                    },
                ) {
                    Text("Add Album")
                }
            }
        }
    }
}

class RealmManager(private val context: Context, private val migrationIdx: Int) {

    companion object {
        val migrations: List<AutomaticSchemaMigration> = listOf(
            Migration0(),
            Migration1(),
            Migration2(),
            Migration3(),
        )
    }

    private fun config(): RealmConfiguration {
        val realmFile = File(context.filesDir, "playground.realm")
        realmFile.copyTo(target = File(context.filesDir, "playground_$migrationIdx.realm"), overwrite = true)

        return RealmConfiguration.Builder(schemas())
            .schemaVersion(2L)
            .name("playground_$migrationIdx.realm")
            .migration(migration = migrations[migrationIdx], resolveEmbeddedObjectConstraints = true)
            .build()
    }

    private var _realm: Realm? = null
    val realm: Realm
        get() = _realm ?: run {
            _realm = Realm.open(config())
            _realm!!
        }

    private fun schemas(): Set<KClass<out TypedRealmObject>> = setOf(
        RealmNewAlbum::class,
        RealmTitle::class,
        RealmAuthor::class,
    )
}

class RealmNewAlbum : RealmObject {
    @PrimaryKey var name: String = ""
    var author: RealmAuthor? = null
    var titles: RealmList<RealmTitle> = realmListOf()

    override fun toString(): String {
        return buildString {
            appendLine("$name - ${author?.name}")
            titles.forEach { title ->
                appendLine("${title.name} - ${title.duration}")
            }
        }
    }
}

class RealmTitle : EmbeddedRealmObject {
    var name: String = ""
    var duration: Int = 0
}

class RealmAuthor : EmbeddedRealmObject {
    var name: String = ""
}

/**
 * Use only dynamic objects
 * Crash because `class io.realm.kotlin.internal.dynamic.DynamicMutableRealmObjectImpl not part of this configuration schema`
 */
class Migration0 : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrationContext.oldRealm.query("RealmAlbum").find().forEach { old ->
            val oldName = old.getValue<String>("name")
            val oldAuthor = old.getObject("author")?.let { migrationContext.newRealm.findLatest(it) }
            val oldTitles = old.getObjectList("titles")

            val newAlbumMig = DynamicMutableRealmObject.create(
                type = "RealmNewAlbum",
                mapOf(
                    "name" to oldName,
                    "author" to oldAuthor,
                    "titles" to oldTitles,
                ),
            )

            migrationContext.newRealm.copyToRealm(newAlbumMig, UpdatePolicy.ERROR)
        }
    }
}

/**
 * Recreate new [RealmAuthor] from old [RealmAuthor]. Use DynamicMutableRealmObject for list of embedded objects
 * Crash because `java.lang.IllegalStateException: [RLM_ERR_ILLEGAL_OPERATION]: Not a list of embedded objects`
 */
class Migration1 : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrationContext.oldRealm.query("RealmAlbum").find().forEach { old ->
            val oldName = old.getValue<String>("name")
            val oldAuthor = old.getObject("author")?.let { migrationContext.newRealm.findLatest(it) }
            val oldTitles = old.getObjectList("titles")

            val newAuthor = oldAuthor?.let {
                RealmAuthor().apply {
                    name = oldAuthor.getValue("name")
                }
            }

            val newAlbumMig = DynamicMutableRealmObject.create(
                type = "RealmNewAlbum",
                mapOf(
                    "name" to oldName,
                    "author" to newAuthor,
                    "titles" to oldTitles,
                ),
            )

            migrationContext.newRealm.copyToRealm(newAlbumMig, UpdatePolicy.ERROR)
        }
    }
}

/**
 * Recreate new [RealmAuthor] from old [RealmAuthor].
 * Recreate newTitles from oldTitles
 * Crash because `java.lang.IllegalStateException: [RLM_ERR_ILLEGAL_OPERATION]: Not a list of embedded objects`
 */
class Migration2 : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrationContext.oldRealm.query("RealmAlbum").find().forEach { old ->
            val oldName = old.getValue<String>("name")
            val oldAuthor = old.getObject("author")?.let { migrationContext.newRealm.findLatest(it) }
            val oldTitles = old.getObjectList("titles")

            val newAuthor = oldAuthor?.let {
                RealmAuthor().apply {
                    name = oldAuthor.getValue("name")
                }
            }

            val newTitles = oldTitles.mapTo(realmListOf()) {
                RealmTitle().apply {
                    name = it.getValue("name")
                    duration = it.getValue<Long>("duration").toInt()
                }
            }

            val newAlbumMig = DynamicMutableRealmObject.create(
                type = "RealmNewAlbum",
                mapOf(
                    "name" to oldName,
                    "author" to newAuthor,
                    "titles" to newTitles,
                ),
            )

            migrationContext.newRealm.copyToRealm(newAlbumMig, UpdatePolicy.ERROR)
        }
    }
}

/**
 * Recreate new [RealmAuthor] from old [RealmAuthor].
 * Use a RealmList of DynamicMutableRealmObject
 * Crash because `java.lang.IllegalStateException: [RLM_ERR_ILLEGAL_OPERATION]: Not a list of embedded objects`
 */
class Migration3 : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrationContext.oldRealm.query("RealmAlbum").find().forEach { old ->
            val oldName = old.getValue<String>("name")
            val oldAuthor = old.getObject("author")?.let { migrationContext.newRealm.findLatest(it) }
            val oldTitles = old.getObjectList("titles")

            val newAuthor = oldAuthor?.let {
                RealmAuthor().apply {
                    name = oldAuthor.getValue("name")
                }
            }

            val newTitles = oldTitles.mapTo(realmListOf()) {
                migrationContext.newRealm.findLatest(it)
            }

            val newAlbumMig = DynamicMutableRealmObject.create(
                type = "RealmNewAlbum",
                mapOf(
                    "name" to oldName,
                    "author" to newAuthor,
                    "titles" to newTitles,
                ),
            )

            migrationContext.newRealm.copyToRealm(newAlbumMig, UpdatePolicy.ERROR)
        }
    }
}
