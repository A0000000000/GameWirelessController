package xyz.a00000.joystickcustomview.view

import xyz.a00000.joystickcustomview.bean.GameEvent

interface Callback {
    fun event(ev: GameEvent);
}