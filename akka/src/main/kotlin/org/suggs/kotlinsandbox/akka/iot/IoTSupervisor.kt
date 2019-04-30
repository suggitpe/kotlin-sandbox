package org.suggs.kotlinsandbox.akka.iot

import akka.actor.AbstractActor
import akka.actor.Props
import akka.event.Logging

class IoTSupervisor: AbstractActor(){
    private val log = Logging.getLogger(context.system, this)

    companion object{
        fun props(): Props {
            return Props.create(IoTSupervisor::class.java)
        }
    }

    override fun preStart(){
        log.info("IoT supervisor started")
    }

    override fun postStop() {
        log.info("IoT supervisor stopped")
    }

    override fun createReceive(): Receive {
        return receiveBuilder().build()
    }

}