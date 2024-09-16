package rlatapy.composeplayground

import android.os.Bundle
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
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.TypedRealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val realm = RealmManager().realm
        val albumFlow: Flow<String> = realm.query<RealmAlbum>()
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
                                RealmAlbum().apply {
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

class RealmManager {

    private fun config(): RealmConfiguration = RealmConfiguration.Builder(schemas())
        .schemaVersion(1L)
        .name("playground.realm")
        .build()

    private var _realm: Realm? = null
    val realm: Realm
        get() = _realm ?: run {
            _realm = Realm.open(config())
            _realm!!
        }

    private fun schemas(): Set<KClass<out TypedRealmObject>> = setOf(
        RealmAlbum::class,
        RealmTitle::class,
        RealmAuthor::class,
    )
}

class RealmAlbum : RealmObject {
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

class RealmTitle : RealmObject {
    var name: String = ""
    var duration: Int = 0
}

class RealmAuthor : RealmObject {
    var name: String = ""
}
