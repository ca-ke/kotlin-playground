package coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import models.Experience
import models.Experience.Junior as Junior
import models.Experience.Pleno as Pleno
import models.Experience.Senior as Senior
import models.Person

data class Developer(override val name: String, override val age: Int, override val experience: Experience) : Person

@ExperimentalCoroutinesApi
fun main() {
    fun Person.isJunior() = this.experience == Junior
    fun Person.isUnder25() = this.age <= 25

    /*
     Produces a finite number of items that are Developers with distinguish age and experience
     on the internal channel for the produce coroutine builder.
     */
    fun CoroutineScope.produceProductionLine(): ReceiveChannel<Person> = produce {
        val productionLineMembers = ArrayList<Person>()
        productionLineMembers.add(Developer(name = "Desenvolvedor 1", age = 22, experience = Junior))
        productionLineMembers.add(Developer(name = "Desenvolvedor 2", age = 15, experience = Junior))
        productionLineMembers.add(Developer(name = "Desenvolvedor 3", age = 47, experience = Pleno))
        productionLineMembers.add(Developer(name = "Desenvolvedor 4", age = 14, experience = Pleno))
        productionLineMembers.add(Developer(name = "Desenvolvedor 5", age = 23, experience = Senior))
        productionLineMembers.add(Developer(name = "Desenvolvedor 6", age = 19, experience = Senior))

        productionLineMembers.forEach {
            send(it)
        }
    }

    /*
     Produces only under 25 developer on the internal channel for the produce coroutine builder
     */
    fun CoroutineScope.buildDeveloperChannelUnder25From(items: ReceiveChannel<Person>) = produce {
        for (item in items) {
            if (item.isUnder25()) {
                send(item)
            }
        }
    }

    /*
     Produces only Junior developers on the internal channel for the produce coroutine builder
     */
    fun CoroutineScope.buildJuniorDeveloperChannel(items: ReceiveChannel<Person>) = produce {
        for (item in items) {
            if (item.isJunior()) {
                send(item)
            }
        }
    }

    suspend fun consumeChannel(channel: ReceiveChannel<Person>, type: String) {
        println("---- $type ----")
        channel.consumeEach(::println)
    }

    runBlocking {
        // Produce a stream of developers
        val productionLine = produceProductionLine()
        /*
            productionLine is then passed to the developerUnder25Channel via the
            buildDeveloperChannelUnder25From method, which feeds the stream of items into the
            developerUnder25Channel. This channel then checks the developer age. If the age is <= 25, then
            it sends the item in its own channel.
         */
        val developerUnder25Channel = buildDeveloperChannelUnder25From(productionLine)
        /*
            developerUnder25Channel is then passed to the juniorDeveloperUnder25Channel via the
            buildJuniorDeveloperChannel method, which feeds the stream of items into the
            juniorDeveloperUnder25Channel. This channel then checks the developer experience. If he is a Junior, then
            it sends the item in its own channel.
         */
        val juniorDeveloperUnder25Channel = buildJuniorDeveloperChannel(developerUnder25Channel)

        consumeChannel(
            channel = juniorDeveloperUnder25Channel,
            type = "Membros da linha de produção que são Juniores e tem menor que 25"
        )

        productionLine.cancel()
        developerUnder25Channel.cancel()
        juniorDeveloperUnder25Channel.cancel()
    }
}