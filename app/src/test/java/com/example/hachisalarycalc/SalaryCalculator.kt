package com.example.hachisalarycalc

import kotlin.math.ceil

class SalaryCalculator(
    private val wage: Int,
    private val normalWork: Double,
    private val dayWorkTime: Double,
    private val workTime: Double,
    private val nightWorkTime: Double,
    private val singleMultiplier: Double,
    private val doubleMultiplier: Double
) {
    // 手当なしの日給
    private fun calcNormalSalary(): Int {
        val salary = workTime * wage
        return ceil(salary).toInt()
    }
    // 残業手当のみ
    private fun calcOverWorkSalary(): Int {
        val salary = (normalWork * wage.toDouble()) + ((workTime - normalWork) * wage.toDouble() * singleMultiplier)
        return ceil(salary).toInt()
    }
    // 夜勤手当のみ
    private fun calcNightSalary(): Int {
        val salary = (dayWorkTime * wage.toDouble()) + (nightWorkTime * wage.toDouble() * singleMultiplier)
        return ceil(salary).toInt()
    }
    // 残業して夜勤
    private fun calcOverAfterNightSalary(): Int {
        val salary = (normalWork * wage.toDouble()) + ((dayWorkTime - normalWork) * wage.toDouble() * singleMultiplier) + (nightWorkTime * wage.toDouble() * doubleMultiplier)
        return ceil(salary).toInt()
    }
    // 夜勤して残業
    private fun calcNightAfterOverSalary(): Int {
        val salary = (dayWorkTime * wage.toDouble()) + ((normalWork - dayWorkTime) * wage.toDouble() * singleMultiplier) + ((workTime - normalWork) * wage.toDouble() * doubleMultiplier)
        return ceil(salary).toInt()
    }
}