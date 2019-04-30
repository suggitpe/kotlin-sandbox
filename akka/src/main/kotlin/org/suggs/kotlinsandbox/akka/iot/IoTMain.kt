package org.suggs.kotlinsandbox.akka.iot

import akka.actor.ActorSystem

fun main(){
    val system = ActorSystem.create("iot-system")

    try{
        val supervisor = system.actorOf(IoTSupervisor.props(), "iot-supervisor")

        println(">>> Press Enter to exit the system <<<")
        System.`in`.read()
    }
    finally{
        system.terminate()
    }
}