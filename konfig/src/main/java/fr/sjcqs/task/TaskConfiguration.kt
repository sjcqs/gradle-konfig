package fr.sjcqs.task

import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Provider

interface TaskConfiguration {
    val variants: DomainObjectCollection<out BaseVariant>
    val namespaceProvider: Provider<String?> // lazily provide namespace
    val sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
}
