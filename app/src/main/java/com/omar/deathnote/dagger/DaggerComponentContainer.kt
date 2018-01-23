package com.omar.deathnote.dagger

import com.omar.deathnote.App
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class DaggerComponentContainer private constructor() {

    companion object {

        @JvmStatic
        val instance = DaggerComponentContainer()
    }

    private val components = HashMap<Class<*>, Any>()

    @Inject
    lateinit var screenBuilders: Map<Class<out BaseComponent>, @JvmSuppressWildcards Provider<ComponentBuilder<*>>>

    fun initialize(application: App) {
        if (getItem(AppComponent::class.java) != null) {
            throw IllegalStateException("Already initialized")
        }

        val appComponent = DaggerAppComponent.builder()
                .application(application)
                .build()
        putItem(AppComponent::class.java, appComponent)

    }

    operator fun <T : BaseComponent> get(typeToken: Class<T>): T {
        return get(typeToken) { it.build() }
    }

    fun <T : BaseComponent> get(typeToken: Class<T>, componentBuilder: (ComponentBuilder<T>) -> T): T {
        getItem(AppComponent::class.java)
                ?: throw IllegalStateException("No AppComponent - call DaggerComponentContainer.initialize()")

        if (getItem(typeToken) == null) {
            val provider = screenBuilders[typeToken]
                    ?: throw IllegalStateException("Requested Component $typeToken is not bound - check Mapping and Initialize call")
            putItem(
                    typeToken,
                    componentBuilder.invoke(provider.get() as ComponentBuilder<T>)
            )
        }

        return getItem(typeToken)!!
    }

    fun <T : BaseComponent> remove(typeToken: Class<T>) {
        if (AppComponent::class.java == typeToken) {
            throw IllegalStateException("Trying to remove Application Component")
        }

        components.remove(typeToken)
    }

    fun addComponent(clazz: Class<out BaseComponent>, component: BaseComponent) {
        components.put(clazz, component)
    }

    private fun <T> getItem(key: Class<T>): T? {
        return key.cast(components[key])
    }

    private fun <T> putItem(key: Class<T>, value: T) {
        components.put(key, value as Any)
    }
}
