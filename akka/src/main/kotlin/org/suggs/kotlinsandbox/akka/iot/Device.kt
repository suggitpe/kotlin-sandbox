package org.suggs.kotlinsandbox.akka.iot

import akka.actor.AbstractActor
import akka.actor.Props
import akka.event.Logging
import java.util.*
import java.util.Optional.empty

class Device(val groupId: String, val deviceId: String) : AbstractActor() {

    val log = Logging.getLogger(context.system, this)
    var lastTemperatureReading: Optional<Double> = empty()

    companion object {
        fun props(groupId: String, deviceId: String): Props {
            return Props.create(Device::class.java) { Device(groupId, deviceId) }
        }
    }

    override fun preStart() {
        log.info("""Device actor $groupId-$deviceId started""")
    }

    override fun postStop() {
        log.info("""Device actor $groupId-$deviceId stopped""")
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(RecordTemperature::class.java){r ->
                    log.info("""Recorded temperature reading ${r.value} with request #${r.requestId}""")
                    lastTemperatureReading = Optional.of(r.value)
                    sender.tell(TemperatureRecorded(r.requestId), self)
                }
                .match(ReadTemperature::class.java) { r ->
                    sender.tell(RespondTemperature(r.requestId, lastTemperatureReading), self)
                }.build()
    }
}

data class ReadTemperature(val requestId: Long)
data class RespondTemperature(val requestId: Long, val value: Optional<Double>)
data class RecordTemperature(val requestId: Long, val value: Double)
data class TemperatureRecorded(val requestId: Long)


