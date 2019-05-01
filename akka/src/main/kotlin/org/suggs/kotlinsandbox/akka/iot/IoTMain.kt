package org.suggs.kotlinsandbox.akka.iot

import akka.actor.ActorSystem
import java.lang.Thread.sleep

fun main(){
    val system = ActorSystem.create("iot-system")

    try{
        val supervisor = system.actorOf(IoTSupervisor.props(), "iot-supervisor")

        sleep(100L)
        println(">>> Press Enter to exit the system <<<")
        System.`in`.read()
    }
    finally{
        system.terminate()
    }
}