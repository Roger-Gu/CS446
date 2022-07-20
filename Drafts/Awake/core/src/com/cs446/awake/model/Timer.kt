package com.cs446.awake.model

class Timer(var time: Int) {
    var activeTimer = false
    var timerLimit = 10
    var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.
}