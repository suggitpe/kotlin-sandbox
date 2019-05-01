package org.suggs.kotlinsandbox.akka.iot

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import scala.concurrent.duration.FiniteDuration
import java.util.*
import java.util.concurrent.TimeUnit

class DeviceTest {

    companion object {
        lateinit var system: ActorSystem

        @JvmStatic
        @BeforeAll
        fun `setup akka system`() {
            system = ActorSystem.create("test-system")
        }

        @JvmStatic
        @AfterAll
        fun `teardown akka system`() {
            TestKit.shutdownActorSystem(system, FiniteDuration.create(10, TimeUnit.SECONDS), true)
        }
    }

    @Test
    fun `replies with empty reading if no temperature is known`() {
        val probe = TestKit(system)
        val deviceActor = system.actorOf(Device.props("group", "device"))
        deviceActor.tell(ReadTemperature(42L), probe.testActor())
        val response = probe.expectMsgClass(RespondTemperature::class.java)
        assertThat(response.requestId).isEqualTo(42L)
        assertThat(response.value).isEqualTo(Optional.empty<Double>())
    }

    @Test
    fun `replies with temperature reading when temperature is known`(){
        val probe = TestKit(system)
        val deviceActor = system.actorOf(Device.props("group", "device"))

        deviceActor.tell(RecordTemperature(1L, 24.0), probe.testActor())
        assertThat(probe.expectMsgClass(TemperatureRecorded::class.java).requestId).isEqualTo(1L)

        deviceActor.tell(ReadTemperature(2L), probe.testActor())
        val response = probe.expectMsgClass(RespondTemperature::class.java)
        assertThat(response.value).isEqualTo(Optional.of(24.0))
    }

    @Test
    fun `replies with the latest temperature reading when it has received many`(){
        val probe = TestKit(system)
        val deviceActor = system.actorOf(Device.props("group", "device"))

        deviceActor.tell(RecordTemperature(1L, 24.0), probe.testActor())
        deviceActor.tell(RecordTemperature(2L, 25.0), probe.testActor())
        deviceActor.tell(RecordTemperature(3L, 26.0), probe.testActor())
        probe.expectMsgClass(TemperatureRecorded::class.java)
        probe.expectMsgClass(TemperatureRecorded::class.java)
        probe.expectMsgClass(TemperatureRecorded::class.java)

        deviceActor.tell(ReadTemperature(4L), probe.testActor())
        val response = probe.expectMsgClass(RespondTemperature::class.java)
        assertThat(response.value).isEqualTo(Optional.of(26.0))
    }
}