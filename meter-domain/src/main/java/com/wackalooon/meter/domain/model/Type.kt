package com.wackalooon.meter.domain.model

import java.io.Serializable

sealed class Type : Serializable {
    data class Water(val waterType: WaterType) : Type()
    object Gas : Type()
    object Electricity : Type()
}
