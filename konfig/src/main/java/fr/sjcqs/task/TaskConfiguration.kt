package fr.sjcqs.task

import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer

interface TaskConfiguration {
    val variants: DomainObjectCollection<out BaseVariant>
    val sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
}
